package com.mrboomdev.java.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Objects;

public class ForwardingFileObject<F extends FileObject> implements FileObject {
	protected final F fileObject;

	protected ForwardingFileObject(F fileObject) {
		this.fileObject = (F) Objects.requireNonNull(fileObject);
	}

	public URI toUri() {
		return this.fileObject.toUri();
	}

	public String getName() {
		return this.fileObject.getName();
	}

	public InputStream openInputStream() throws IOException {
		return this.fileObject.openInputStream();
	}

	public OutputStream openOutputStream() throws IOException {
		return this.fileObject.openOutputStream();
	}

	public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
		return this.fileObject.openReader(ignoreEncodingErrors);
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return this.fileObject.getCharContent(ignoreEncodingErrors);
	}

	public Writer openWriter() throws IOException {
		return this.fileObject.openWriter();
	}

	public long getLastModified() {
		return this.fileObject.getLastModified();
	}

	public boolean delete() {
		return this.fileObject.delete();
	}
}