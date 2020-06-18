package it.cosenzproject.tools.processor.definition;

import javax.lang.model.element.TypeElement;

public class RSConvertDefinition {

	private String name;
	private TypeElement typeElement;
	private String fullName;

	public TypeElement getTypeElement() {
		return typeElement;
	}

	public String getFullName() {
		return fullName;
	}

	public RSConvertDefinition(TypeElement classElement) {
		this.typeElement = classElement;
		name = classElement.getSimpleName().toString();
	}

	public String getName() {
		return name;
	}
}
