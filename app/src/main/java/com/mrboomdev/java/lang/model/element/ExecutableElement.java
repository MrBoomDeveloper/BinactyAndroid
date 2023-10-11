package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.TypeMirror;

import java.util.List;

public interface ExecutableElement extends Element, Parameterizable {
	TypeMirror asType();

	List<? extends TypeParameterElement> getTypeParameters();

	TypeMirror getReturnType();

	List<? extends VariableElement> getParameters();

	TypeMirror getReceiverType();

	boolean isVarArgs();

	boolean isDefault();

	List<? extends TypeMirror> getThrownTypes();

	AnnotationValue getDefaultValue();

	Name getSimpleName();
}