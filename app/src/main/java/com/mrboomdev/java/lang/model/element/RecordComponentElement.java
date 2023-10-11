package com.mrboomdev.java.lang.model.element;

public interface RecordComponentElement extends Element {
	Element getEnclosingElement();

	Name getSimpleName();

	ExecutableElement getAccessor();
}