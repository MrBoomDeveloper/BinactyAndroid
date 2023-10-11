package com.mrboomdev.java.annotation.processing;

import com.mrboomdev.java.lang.model.element.Element;
import com.mrboomdev.java.lang.model.element.TypeElement;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public interface RoundEnvironment {
	boolean processingOver();

	boolean errorRaised();

	Set<? extends Element> getRootElements();

	Set<? extends Element> getElementsAnnotatedWith(TypeElement var1);

	default Set<? extends Element> getElementsAnnotatedWithAny(TypeElement... annotations) {
		Set<Element> result = new LinkedHashSet<>();

		for(TypeElement annotation : annotations) {
			result.addAll(this.getElementsAnnotatedWith(annotation));
		}

		return Collections.unmodifiableSet(result);
	}

	Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> var1);

	default Set<? extends Element> getElementsAnnotatedWithAny(Set<Class<? extends Annotation>> annotations) {
		Set<Element> result = new LinkedHashSet<>();

		for(Class<? extends Annotation> aClass : annotations) {
			result.addAll(this.getElementsAnnotatedWith(aClass));
		}

		return Collections.unmodifiableSet(result);
	}
}