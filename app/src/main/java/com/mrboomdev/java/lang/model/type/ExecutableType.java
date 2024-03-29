package com.mrboomdev.java.lang.model.type;

import java.util.List;

public interface ExecutableType extends TypeMirror {
	List<? extends TypeVariable> getTypeVariables();

	TypeMirror getReturnType();

	List<? extends TypeMirror> getParameterTypes();

	TypeMirror getReceiverType();

	List<? extends TypeMirror> getThrownTypes();
}