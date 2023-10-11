package com.mrboomdev.java.lang.model;

import com.mrboomdev.java.lang.model.element.AnnotationMirror;

import java.lang.annotation.Annotation;
import java.util.List;

public interface AnnotatedConstruct {
	List<? extends AnnotationMirror> getAnnotationMirrors();

	<A extends Annotation> A getAnnotation(Class<A> var1);

	<A extends Annotation> A[] getAnnotationsByType(Class<A> var1);
}