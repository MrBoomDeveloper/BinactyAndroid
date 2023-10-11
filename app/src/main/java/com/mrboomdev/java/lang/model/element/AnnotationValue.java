package com.mrboomdev.java.lang.model.element;

import androidx.annotation.NonNull;

public interface AnnotationValue {
	Object getValue();

	@NonNull
	String toString();

	<R, P> R accept(AnnotationValueVisitor<R, P> var1, P var2);
}