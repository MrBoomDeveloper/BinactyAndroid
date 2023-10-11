package com.mrboomdev.java.tools;

import com.mrboomdev.java.lang.model.SourceVersion;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface Tool {
	default String name() {
		return "";
	}

	int run(InputStream var1, OutputStream var2, OutputStream var3, String... var4);

	Set<SourceVersion> getSourceVersions();
}