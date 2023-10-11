package com.mrboomdev.java.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MirroredTypesException extends RuntimeException {
	private static final long serialVersionUID = 269L;
	transient List<? extends TypeMirror> types;

	MirroredTypesException(String message, TypeMirror type) {
		super(message);
		List<TypeMirror> tmp = new ArrayList<>();
		tmp.add(type);
		this.types = Collections.unmodifiableList(tmp);
	}

	public MirroredTypesException(List<? extends TypeMirror> types) {
		super("Attempt to access Class objects for TypeMirrors " + new ArrayList<>(types));
		this.types = Collections.unmodifiableList(types);
	}

	public List<? extends TypeMirror> getTypeMirrors() {
		return this.types;
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		this.types = null;
	}
}