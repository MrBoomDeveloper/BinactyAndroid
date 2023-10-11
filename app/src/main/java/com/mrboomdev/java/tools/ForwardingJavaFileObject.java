package com.mrboomdev.java.tools;

import com.mrboomdev.java.lang.model.element.Modifier;
import com.mrboomdev.java.lang.model.element.NestingKind;
public class ForwardingJavaFileObject<F extends JavaFileObject> extends ForwardingFileObject<F> implements JavaFileObject {
	protected ForwardingJavaFileObject(F fileObject) {
		super(fileObject);
	}

	public JavaFileObject.Kind getKind() {
		return ((JavaFileObject)this.fileObject).getKind();
	}

	public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind) {
		return ((JavaFileObject)this.fileObject).isNameCompatible(simpleName, kind);
	}

	public NestingKind getNestingKind() {
		return ((JavaFileObject)this.fileObject).getNestingKind();
	}

	public Modifier getAccessLevel() {
		return ((JavaFileObject)this.fileObject).getAccessLevel();
	}
}