package com.mrboomdev.binacty.rn;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.mrboomdev.platformer.util.helper.BoomException;

import java.lang.reflect.Field;

public class ReactParser {

	@NonNull
	public static WritableMap serializeMap(@NonNull Object o) {
		var map = Arguments.createMap();

		for(var field : o.getClass().getFields()) {
			try {
				var value = field.get(0);
				var type = field.getType();
				if(value == null) continue;

				if(field.getType().isPrimitive()) {
					putPrimitiveToMap(map, field);
					continue;
				}

				if(type.isAssignableFrom(String.class)) {
					map.putString(field.getName(), value.toString());
				}

				throw BoomException.builder("Unknown class type: ")
						.addQuoted(field.getType().getCanonicalName())
						.build();
			} catch(IllegalAccessException e) {
				throw new BoomException("Cannot access a field while serializing to a React map", e);
			}
		}

		return map;
	}

	private static void putPrimitiveToMap(WritableMap map, @NonNull Field field) throws IllegalAccessException {
		var type = field.getType();

		if(type.isAssignableFrom(Integer.class)) {
			map.putInt(field.getName(), field.getInt(field));
		}

		if(type.isAssignableFrom(Float.class)) {
			map.putDouble(field.getName(), field.getFloat(field));
		}

		if(type.isAssignableFrom(Double.class)) {
			map.putDouble(field.getName(), field.getDouble(field));
		}
	}

	public static <T> T parseMap(ReadableMap map) {
		return null;
	}
}