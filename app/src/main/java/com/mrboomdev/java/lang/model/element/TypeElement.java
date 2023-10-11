package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.TypeMirror;

import java.util.List;

public interface TypeElement extends Element, Parameterizable, QualifiedNameable {
	TypeMirror asType();

	List<? extends Element> getEnclosedElements();

	NestingKind getNestingKind();

	Name getQualifiedName();

	Name getSimpleName();

	TypeMirror getSuperclass();

	List<? extends TypeMirror> getInterfaces();

	List<? extends TypeParameterElement> getTypeParameters();

	default List<? extends RecordComponentElement> getRecordComponents() {
		return List.of();
	}

	default List<? extends TypeMirror> getPermittedSubclasses() {
		return List.of();
	}

	Element getEnclosingElement();
}