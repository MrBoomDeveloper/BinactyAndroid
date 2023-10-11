package com.mrboomdev.java.lang.model.type;

import com.mrboomdev.java.lang.model.element.Element;

public interface TypeVariable extends ReferenceType {
	Element asElement();

	TypeMirror getUpperBound();

	TypeMirror getLowerBound();
}