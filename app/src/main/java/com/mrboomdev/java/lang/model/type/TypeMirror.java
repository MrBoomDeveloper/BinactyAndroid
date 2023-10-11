package com.mrboomdev.java.lang.model.type;

import androidx.annotation.NonNull;

import com.mrboomdev.java.lang.model.AnnotatedConstruct;
import com.mrboomdev.java.lang.model.element.AnnotationMirror;

import java.lang.annotation.Annotation;
import java.util.List;

public interface TypeMirror extends AnnotatedConstruct {
	TypeKind getKind();

	boolean equals(Object var1);

	int hashCode();

	@NonNull
	String toString();

	List<? extends AnnotationMirror> getAnnotationMirrors();

	<A extends Annotation> A getAnnotation(Class<A> var1);

	<A extends Annotation> A[] getAnnotationsByType(Class<A> var1);

	<R, P> R accept(TypeVisitor<R, P> var1, P var2);
}