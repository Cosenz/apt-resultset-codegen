package it.cosenzproject.tools.generator;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import it.cosenzproject.tools.annotation.RSColumn;
import it.cosenzproject.tools.processor.RSConverterProcessor;
import it.cosenzproject.tools.processor.definition.RSConvertDefinition;

public class RSConvertGenerator {

	public static final String CONVERT_SUFFIX = "RSConverter";

	public static void build(RSConvertDefinition definition, Elements elementUtils, Filer filer) throws IOException {
		TypeElement interfaceTypeElement = definition.getTypeElement();
		String adapterClassName = interfaceTypeElement.getSimpleName() + CONVERT_SUFFIX;

		PackageElement pkg = elementUtils.getPackageOf(interfaceTypeElement);
		String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();

		// aggiungo l'annotation Generated con data e commenti per versione e autore
		// l'attributo value è obbligatorio
		AnnotationSpec annotationSpec = AnnotationSpec.builder(Generated.class)
		        .addMember("value", "$S", RSConverterProcessor.class.getName()).addMember("date", "$S", new Date().toString())
		        .addMember("comments", "$S", "version: 1.0.0, author: Andrea Cosentino").build();
		TypeSpec.Builder builder = TypeSpec.classBuilder(adapterClassName).addModifiers(Modifier.PUBLIC).addAnnotation(annotationSpec);
		// Potevamo generare la javadoc così al posto dell'annotazione Generated
		// builder.addJavadoc("Generato automaticamente.\n\n @since $L\n @author Andrea
		// Cosentino\n\n", (new Date()).toString());
		builder.addMethod(buildCreateMethod(definition, elementUtils, "convert"));
		TypeSpec typeSpec = builder.build();
		JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
	}

	private static MethodSpec buildCreateMethod(RSConvertDefinition definition, Elements elementUtils, String methodName) {
		TypeElement typeElement = definition.getTypeElement();

		ClassName methodParam = ClassName.get(ResultSet.class);

		MethodSpec.Builder method = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC, Modifier.STATIC)
		        .addException(SQLException.class).addParameter(methodParam, "value").returns(TypeName.get(typeElement.asType()));

		method.addStatement("$L result=new $L()", typeElement.getSimpleName(), typeElement.getSimpleName());
		for (Element item : definition.getTypeElement().getEnclosedElements()) {
			if (item.getKind() == ElementKind.FIELD) {
				RSColumn column = item.getAnnotation(RSColumn.class);
				String statement = String.format("result.set%s(value.get%s(\"$L\"))", item.getSimpleName().toString().substring(0, 1)
				        .toUpperCase(Locale.ITALY).concat(item.getSimpleName().toString().substring(1)), convertType(item));
				if (column != null) {
					method.addStatement(statement, column.value());
				} else {
					method.addStatement(statement, item.getSimpleName());
				}
			}
		}

		method.addStatement("return result");

		return method.build();
	}

	private static String convertType(Element item) {
		String type = item.asType().toString();
		String[] pkType = type.split("\\.");
		type = pkType[pkType.length - 1];
		if ("Integer".equals(type)) {
			type = "Int";
		}
		return type;
	}
}
