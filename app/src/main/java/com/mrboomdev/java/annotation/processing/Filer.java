package com.mrboomdev.java.annotation.processing;

import com.mrboomdev.java.lang.model.element.Element;
import com.mrboomdev.java.tools.FileObject;
import com.mrboomdev.java.tools.JavaFileManager;
import com.mrboomdev.java.tools.JavaFileObject;

import java.io.IOException;

public interface Filer {
	JavaFileObject createSourceFile(CharSequence var1, Element... var2) throws IOException;

	JavaFileObject createClassFile(CharSequence var1, Element... var2) throws IOException;

	FileObject createResource(JavaFileManager.Location var1, CharSequence var2, CharSequence var3, Element... var4) throws IOException;

	FileObject getResource(JavaFileManager.Location var1, CharSequence var2, CharSequence var3) throws IOException;
}