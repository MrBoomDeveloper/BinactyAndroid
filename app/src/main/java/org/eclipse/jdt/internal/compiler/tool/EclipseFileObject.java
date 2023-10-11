/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Walter Harley   - Patch for ensuring the parent folders are created
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.tool;

import androidx.annotation.NonNull;

import com.mrboomdev.java.lang.model.element.Modifier;
import com.mrboomdev.java.lang.model.element.NestingKind;
import com.mrboomdev.java.tools.SimpleJavaFileObject;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Implementation of a Java file object that corresponds to a file on the file system
 */
public class EclipseFileObject extends SimpleJavaFileObject {
	File f;
	private final Charset charset;
	private boolean parentsExist; // parent directories exist

	public EclipseFileObject(String className, URI uri, Kind kind, Charset charset) {
		super(uri, kind);
		this.f = new File(this.uri);
		this.charset = charset;
		this.parentsExist = false;
	}

	/* (non-Javadoc)
	 * @see javax.tools.JavaFileObject#getAccessLevel()
	 */
	@Override
	public Modifier getAccessLevel() {
		// cannot express multiple modifier
		if (getKind() != Kind.CLASS) {
			return null;
		}
		ClassFileReader reader = null;
   		try {
			reader = ClassFileReader.read(this.f);
		} catch (ClassFormatException | IOException e) {
			// ignore
		}
		if (reader == null) {
			return null;
		}
		final int accessFlags = reader.accessFlags();
		if ((accessFlags & ClassFileConstants.AccPublic) != 0) {
			return Modifier.PUBLIC;
		}
		if ((accessFlags & ClassFileConstants.AccAbstract) != 0) {
			return Modifier.ABSTRACT;
		}
		if ((accessFlags & ClassFileConstants.AccFinal) != 0) {
			return Modifier.FINAL;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.tools.JavaFileObject#getNestingKind()
	 */
	@Override
	public NestingKind getNestingKind() {
		switch(this.kind) {
			case SOURCE :
				return NestingKind.TOP_LEVEL;
			case CLASS :
        		ClassFileReader reader = null;
        		try {
        			reader = ClassFileReader.read(this.f);
        		} catch (ClassFormatException | IOException e) {
        			// ignore
        		}
				if (reader == null) {
        			return null;
        		}
        		if (reader.isAnonymous()) {
        			return NestingKind.ANONYMOUS;
        		}
        		if (reader.isLocal()) {
        			return NestingKind.LOCAL;
        		}
        		if (reader.isMember()) {
        			return NestingKind.MEMBER;
        		}
        		return NestingKind.TOP_LEVEL;
        	default:
        		return null;
		}
	}

	/**
	 * @see com.mrboomdev.java.tools.FileObject#delete()
	 */
	@Override
	public boolean delete() {
		return this.f.delete();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EclipseFileObject)) {
			return false;
		}
		EclipseFileObject eclipseFileObject = (EclipseFileObject) o;
		return eclipseFileObject.toUri().equals(this.uri);
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		try {
			return Util.getCharContents(this, ignoreEncodingErrors, org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(this.f), this.charset.name());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getLastModified() {
		return this.f.lastModified();
	}

	@Override
	public String getName() {
        return this.f.getPath();
    }

	@Override
	public int hashCode() {
		return this.f.hashCode();
	}


	@Override
	public InputStream openInputStream() throws IOException {
		return new BufferedInputStream(new FileInputStream(this.f));
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		ensureParentDirectoriesExist();
		return new BufferedOutputStream(new FileOutputStream(this.f));
	}

	@Override
	public Reader openReader(boolean ignoreEncodingErrors) {
		try {
			return new BufferedReader(new FileReader(this.f));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Writer openWriter() throws IOException {
		ensureParentDirectoriesExist();
		return new BufferedWriter(new FileWriter(this.f));
	}

	@NonNull
	@Override
	public String toString() {
		return this.f.getAbsolutePath();
	}

    private void ensureParentDirectoriesExist() throws IOException {
        if (!this.parentsExist) {
            File parent = this.f.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    // could have been concurrently created
                    if (!parent.exists() || !parent.isDirectory())
                        throw new IOException("Unable to create parent directories for " + this.f); //$NON-NLS-1$
                }
            }
            this.parentsExist = true;
        }
    }
}