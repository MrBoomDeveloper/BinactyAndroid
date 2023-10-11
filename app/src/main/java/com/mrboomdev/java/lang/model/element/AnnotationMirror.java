package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.DeclaredType;

import java.util.Map;

public interface AnnotationMirror {
	DeclaredType getAnnotationType();

	Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues();
}