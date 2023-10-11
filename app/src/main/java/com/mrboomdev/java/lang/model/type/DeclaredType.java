package com.mrboomdev.java.lang.model.type;

import com.mrboomdev.java.lang.model.element.Element;

import java.util.List;

public interface DeclaredType extends ReferenceType {
	Element asElement();

	TypeMirror getEnclosingType();

	List<? extends TypeMirror> getTypeArguments();
}