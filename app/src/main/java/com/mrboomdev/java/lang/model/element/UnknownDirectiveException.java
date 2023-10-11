package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.UnknownEntityException;

public class UnknownDirectiveException extends UnknownEntityException {
	private static final long serialVersionUID = 269L;
	private final transient ModuleElement.Directive directive;
	private final transient Object parameter;

	public UnknownDirectiveException(ModuleElement.Directive d, Object p) {
		super("Unknown directive: " + d);
		this.directive = d;
		this.parameter = p;
	}

	public ModuleElement.Directive getUnknownDirective() {
		return this.directive;
	}

	public Object getArgument() {
		return this.parameter;
	}
}