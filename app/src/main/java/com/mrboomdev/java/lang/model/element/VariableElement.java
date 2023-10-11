package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.TypeMirror;

public interface VariableElement extends Element {
	TypeMirror asType();

	Object getConstantValue();

	Name getSimpleName();

	Element getEnclosingElement();
}