package com.mrboomdev.java;

import com.mrboomdev.java.lang.Version;

import java.util.List;
import java.util.Optional;
public class Java {
	public static final Version RUNTIME_VERSION;

	static {
		RUNTIME_VERSION = new Version(
				List.of(11),
				Optional.of("10"),
				Optional.of(10),
				Optional.of("11"));
	}
}