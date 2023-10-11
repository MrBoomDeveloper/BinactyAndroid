package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.TypeMirror;

import java.util.List;

public interface PackageElement extends Element, QualifiedNameable {
	TypeMirror asType();

	Name getQualifiedName();

	Name getSimpleName();

	List<? extends Element> getEnclosedElements();

	boolean isUnnamed();

	Element getEnclosingElement();
}