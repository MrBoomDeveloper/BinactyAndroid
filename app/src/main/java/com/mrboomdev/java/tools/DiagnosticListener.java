package com.mrboomdev.java.tools;

public interface DiagnosticListener<S> {
	void report(Diagnostic<? extends S> var1);
}