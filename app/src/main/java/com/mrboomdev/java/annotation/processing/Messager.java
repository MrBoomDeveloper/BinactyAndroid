package com.mrboomdev.java.annotation.processing;

import com.mrboomdev.java.lang.model.element.AnnotationMirror;
import com.mrboomdev.java.lang.model.element.AnnotationValue;
import com.mrboomdev.java.lang.model.element.Element;
import com.mrboomdev.java.tools.Diagnostic;

public interface Messager {
	void printMessage(Diagnostic.Kind var1, CharSequence var2);

	void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3);

	void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3, AnnotationMirror var4);

	void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3, AnnotationMirror var4, AnnotationValue var5);
}