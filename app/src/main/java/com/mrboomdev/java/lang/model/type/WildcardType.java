package com.mrboomdev.java.lang.model.type;

public interface WildcardType extends TypeMirror {
	TypeMirror getExtendsBound();

	TypeMirror getSuperBound();
}