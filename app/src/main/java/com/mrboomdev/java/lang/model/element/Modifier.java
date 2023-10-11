package com.mrboomdev.java.lang.model.element;

import androidx.annotation.NonNull;

import java.util.Locale;

public enum Modifier {
	PUBLIC,
	PROTECTED,
	PRIVATE,
	ABSTRACT,
	DEFAULT,
	STATIC,
	SEALED,
	NON_SEALED {
		@NonNull
		public String toString() {
			return "non-sealed";
		}
	},
	FINAL,
	TRANSIENT,
	VOLATILE,
	SYNCHRONIZED,
	NATIVE,
	STRICTFP;

	private Modifier() {
	}

	@NonNull
	public String toString() {
		return this.name().toLowerCase(Locale.US);
	}
}