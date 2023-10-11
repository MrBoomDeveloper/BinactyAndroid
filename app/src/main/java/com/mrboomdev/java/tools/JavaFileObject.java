package com.mrboomdev.java.tools;

import com.mrboomdev.java.lang.model.element.Modifier;
import com.mrboomdev.java.lang.model.element.NestingKind;

import java.util.Objects;

public interface JavaFileObject extends FileObject {
	Kind getKind();

	boolean isNameCompatible(String var1, Kind var2);

	NestingKind getNestingKind();

	Modifier getAccessLevel();

	enum Kind {
		SOURCE(".java"),
		CLASS(".class"),
		HTML(".html"),
		OTHER("");

		public final String extension;

		Kind(String extension) {
			this.extension = (String) Objects.requireNonNull(extension);
		}
	}
}