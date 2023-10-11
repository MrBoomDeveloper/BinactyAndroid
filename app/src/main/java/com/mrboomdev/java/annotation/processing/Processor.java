package com.mrboomdev.java.annotation.processing;

import com.mrboomdev.java.lang.model.SourceVersion;
import com.mrboomdev.java.lang.model.element.AnnotationMirror;
import com.mrboomdev.java.lang.model.element.Element;
import com.mrboomdev.java.lang.model.element.ExecutableElement;
import com.mrboomdev.java.lang.model.element.TypeElement;

import java.util.Set;

public interface Processor {
	Set<String> getSupportedOptions();

	Set<String> getSupportedAnnotationTypes();

	SourceVersion getSupportedSourceVersion();

	void init(ProcessingEnvironment var1);

	boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2);

	Iterable<? extends Completion> getCompletions(Element var1, AnnotationMirror var2, ExecutableElement var3, String var4);
}