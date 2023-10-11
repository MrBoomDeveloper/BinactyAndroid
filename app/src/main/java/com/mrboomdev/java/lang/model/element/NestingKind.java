package com.mrboomdev.java.lang.model.element;

public enum NestingKind {
	TOP_LEVEL,
	MEMBER,
	LOCAL,
	ANONYMOUS;

	private NestingKind() {
	}

	public boolean isNested() {
		return this != TOP_LEVEL;
	}
}