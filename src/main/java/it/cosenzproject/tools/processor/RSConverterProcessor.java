package it.cosenzproject.tools.processor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import it.cosenzproject.tools.annotation.RSConvert;
import it.cosenzproject.tools.generator.RSConvertGenerator;
import it.cosenzproject.tools.processor.definition.RSConvertDefinition;

/**
 *
 * @author Andrea Cosentino
 */
@SupportedAnnotationTypes({ "it.cosenzproject.tools.annotation.RSConvert" })
public class RSConverterProcessor extends AbstractProcessor {

	private Elements elements;

	private Filer filer;

	private Messager messager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.annotation.processing.AbstractProcessor#init(javax.annotation.
	 * processing.ProcessingEnvironment)
	 */
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		elements = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			// recupera tutti gli elementi annotati con RSConvert
			List<Element> items = roundEnv.getElementsAnnotatedWith(RSConvert.class).stream()
			        .filter(item -> item.getKind() == ElementKind.CLASS).collect(Collectors.toList());

			for (Element item : items) {
				TypeElement typeElement = (TypeElement) item;
				RSConvertDefinition adapterDefinition = new RSConvertDefinition(typeElement);
				RSConvertGenerator.build(adapterDefinition, elements, filer);
			}
		} catch (Exception e) {
			error(null, e.getMessage());
		}
		return true;
	}

	private void error(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}

}
