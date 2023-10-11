package com.mrboomdev.java.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum StandardLocation implements JavaFileManager.Location {
	CLASS_OUTPUT,
	SOURCE_OUTPUT,
	CLASS_PATH,
	SOURCE_PATH,
	ANNOTATION_PROCESSOR_PATH,
	ANNOTATION_PROCESSOR_MODULE_PATH,
	PLATFORM_CLASS_PATH,
	NATIVE_HEADER_OUTPUT,
	MODULE_SOURCE_PATH,
	UPGRADE_MODULE_PATH,
	SYSTEM_MODULES,
	MODULE_PATH,
	PATCH_MODULE_PATH;

	private static final ConcurrentMap<String, JavaFileManager.Location> locations = new ConcurrentHashMap();

	private StandardLocation() {
	}

	public static JavaFileManager.Location locationFor(final String name) {
		if (locations.isEmpty()) {
			StandardLocation[] var1 = values();
			int var2 = var1.length;

			for(int var3 = 0; var3 < var2; ++var3) {
				JavaFileManager.Location location = var1[var3];
				locations.putIfAbsent(location.getName(), location);
			}
		}

		locations.putIfAbsent(name, new JavaFileManager.Location() {
			public String getName() {
				return name;
			}

			public boolean isOutputLocation() {
				return name.endsWith("_OUTPUT");
			}
		});
		return (JavaFileManager.Location)locations.get(name);
	}

	public String getName() {
		return this.name();
	}

	public boolean isOutputLocation() {
		switch (this) {
			case CLASS_OUTPUT:
			case SOURCE_OUTPUT:
			case NATIVE_HEADER_OUTPUT:
				return true;
			default:
				return false;
		}
	}

	public boolean isModuleOrientedLocation() {
		switch (this) {
			case MODULE_SOURCE_PATH:
			case ANNOTATION_PROCESSOR_MODULE_PATH:
			case UPGRADE_MODULE_PATH:
			case SYSTEM_MODULES:
			case MODULE_PATH:
			case PATCH_MODULE_PATH:
				return true;
			default:
				return false;
		}
	}
}