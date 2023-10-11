package com.mrboomdev.java.annotation.processing;

import com.mrboomdev.java.lang.model.SourceVersion;
import com.mrboomdev.java.lang.model.util.Elements;
import com.mrboomdev.java.lang.model.util.Types;

import java.util.Locale;
import java.util.Map;

public interface ProcessingEnvironment {
	Map<String, String> getOptions();

	Messager getMessager();

	Filer getFiler();

	Elements getElementUtils();

	Types getTypeUtils();

	SourceVersion getSourceVersion();

	Locale getLocale();

	default boolean isPreviewEnabled() {
		return false;
	}
}