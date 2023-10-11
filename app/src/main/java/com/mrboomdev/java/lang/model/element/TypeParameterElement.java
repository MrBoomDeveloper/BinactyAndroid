package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.TypeMirror;

import java.util.List;

public interface TypeParameterElement extends Element {
	TypeMirror asType();

	Element getGenericElement();

	List<? extends TypeMirror> getBounds();

	Element getEnclosingElement();
}