package it.cosenzproject.tools.generator;

import static it.cosenzproject.tools.util.Constants.CONVERT_SUFFIX;
import static it.cosenzproject.tools.util.Constants.STRING;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
import it.cosenzproject.tools.util.Constants;

/**
 * @author Andrea Cosentino
 *
 */
public class RSConvertGenerator {

	public static void build(RSConvertDefinition definition, Elements elementUtils, Filer filer) throws IOException {
		TypeElement interfaceTypeElement = definition.getTypeElement();
		String adapterClassName = interfaceTypeElement.getSimpleName() + CONVERT_SUFFIX;

		PackageElement pkg = elementUtils.getPackageOf(interfaceTypeElement);
		String packageName = pkg.isUnnamed() ? "default" : pkg.getQualifiedName().toString();

		// aggiungo l'annotation Generated con data e commenti per versione e autore
		// l'attributo value è obbligatorio
		AnnotationSpec annotationSpec = AnnotationSpec.builder(Generated.class)
		        .addMember("value", "$S", RSConverterProcessor.class.getName())
				.addMember("date", "$S", new Date().toString())
		        .addMember("comments", "$S", "codegen version: 1.0.0")
				.build();
		TypeSpec.Builder builder = TypeSpec.classBuilder(adapterClassName)
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(annotationSpec);
		// Potevamo generare la javadoc così al posto dell'annotazione Generated
		// builder.addJavadoc("Generato automaticamente.\n\n @since $L\n @author Andrea
		// Cosentino\n\n", (new Date()).toString());
		builder.addMethod(buildCreateMethod(definition, "convert"));
		TypeSpec typeSpec = builder.build();
		JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
	}

	private static MethodSpec buildCreateMethod(RSConvertDefinition definition, String methodName) {
		TypeElement typeElement = definition.getTypeElement();

		ClassName methodParam = ClassName.get(ResultSet.class);

		MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
		        .addException(SQLException.class)
				.addParameter(methodParam, Constants.PARAM_NAME)
				.returns(TypeName.get(typeElement.asType()));

		method.addStatement("$L result=new $L()", typeElement.getSimpleName(), typeElement.getSimpleName());
		for (Element item : definition.getTypeElement().getEnclosedElements()) {
			if (item.getKind() == ElementKind.FIELD) {
				RSColumn column = item.getAnnotation(RSColumn.class);
				String type = convertType(item);
				StatementBuilder statementBuilder = new StatementBuilder()
						.setPropertyName(item.getSimpleName().toString())
						.setResultSetType(type)
						.setConverter(column.converter());
				String statement = statementBuilder.build();
				chooseStament(method, item, column, type, statement);
			}
		}
		method.addStatement("return result");

		return method.build();
	}

	/**
	 * Call correct override addStatement method
	 * @param method
	 * @param item
	 * @param column
	 * @param type
	 * @param statement
	 */
	private static void chooseStament(MethodSpec.Builder method, Element item, RSColumn column, String type, String statement) {
		if (column != null && !column.converter().isEmpty())
			method.addStatement(statement);
		else if (column != null && !column.value().isEmpty() && STRING.equals(type)) {
			method.addStatement(statement, column.value(), column.value());
		}
		else if(column != null && !column.value().isEmpty()) {
			method.addStatement(statement, column.value());
		}
		else if(STRING.equals(type)) {
			method.addStatement(statement, item.getSimpleName(), item.getSimpleName());
		}else {
			method.addStatement(statement, item.getSimpleName());
		}
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
