package com.mrboomdev.platformer.util.io;

import java.lang.reflect.Field;
public class ParserUtil<T extends Class<?>> {
	public Field[] fields;
	private final T clazz;

	public ParserUtil(T clazz) {
		this.clazz = clazz;
	}
}