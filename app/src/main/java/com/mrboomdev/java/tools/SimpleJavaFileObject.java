package com.mrboomdev.java.tools;

import androidx.annotation.NonNull;

import com.mrboomdev.java.lang.model.element.Modifier;
import com.mrboomdev.java.lang.model.element.NestingKind;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Objects;

public class SimpleJavaFileObject implements JavaFileObject {
	protected final URI uri;
	protected final JavaFileObject.Kind kind;

	protected SimpleJavaFileObject(URI uri, JavaFileObject.Kind kind) {
		Objects.requireNonNull(uri);
		Objects.requireNonNull(kind);
		if (uri.getPath() == null) {
			throw new IllegalArgumentException("URI must have a path: " + uri);
		} else {
			this.uri = uri;
			this.kind = kind;
		}
	}

	public URI toUri() {
		return this.uri;
	}

	public String getName() {
		return this.toUri().getPath();
	}

	public InputStream openInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	public OutputStream openOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	public Reader openReader(boolean ignoreEncodingErrors) {
		CharSequence charContent = this.getCharContent(ignoreEncodingErrors);
		if (charContent == null) {
			throw new UnsupportedOperationException();
		} else {
			if (charContent instanceof CharBuffer) {
				CharBuffer buffer = (CharBuffer)charContent;
				if (buffer.hasArray()) {
					return new CharArrayReader(buffer.array());
				}
			}

			return new StringReader(charContent.toString());
		}
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		throw new UnsupportedOperationException();
	}

	public Writer openWriter() throws IOException {
		return new OutputStreamWriter(this.openOutputStream());
	}

	public long getLastModified() {
		return 0L;
	}

	public boolean delete() {
		return false;
	}

	public JavaFileObject.Kind getKind() {
		return this.kind;
	}

	public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind) {
		String baseName = simpleName + kind.extension;
		return kind.equals(this.getKind()) && (baseName.equals(this.toUri().getPath()) || this.toUri().getPath().endsWith("/" + baseName));
	}

	public NestingKind getNestingKind() {
		return null;
	}

	public Modifier getAccessLevel() {
		return null;
	}

	@NonNull
	public String toString() {
		String var10000 = this.getClass().getName();
		return var10000 + "[" + this.toUri() + "]";
	}
}