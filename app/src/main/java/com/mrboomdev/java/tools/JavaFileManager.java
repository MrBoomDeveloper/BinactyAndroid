package com.mrboomdev.java.tools;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

public interface JavaFileManager extends Closeable, Flushable, OptionChecker {
	ClassLoader getClassLoader(Location var1);

	Iterable<JavaFileObject> list(Location var1, String var2, Set<JavaFileObject.Kind> var3, boolean var4) throws IOException;

	String inferBinaryName(Location var1, JavaFileObject var2);

	boolean isSameFile(FileObject var1, FileObject var2);

	boolean handleOption(String var1, Iterator<String> var2);

	boolean hasLocation(Location var1);

	JavaFileObject getJavaFileForInput(Location var1, String var2, JavaFileObject.Kind var3) throws IOException;

	JavaFileObject getJavaFileForOutput(Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException;

	FileObject getFileForInput(Location var1, String var2, String var3) throws IOException;

	FileObject getFileForOutput(Location var1, String var2, String var3, FileObject var4) throws IOException;

	void flush() throws IOException;

	void close() throws IOException;

	default Location getLocationForModule(Location location, String moduleName) throws IOException {
		throw new UnsupportedOperationException();
	}

	default Location getLocationForModule(Location location, JavaFileObject fo) throws IOException {
		throw new UnsupportedOperationException();
	}

	default <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service) throws IOException {
		throw new UnsupportedOperationException();
	}

	default String inferModuleName(Location location) throws IOException {
		throw new UnsupportedOperationException();
	}

	default Iterable<Set<Location>> listLocationsForModules(Location location) throws IOException {
		throw new UnsupportedOperationException();
	}

	default boolean contains(Location location, FileObject fo) throws IOException {
		throw new UnsupportedOperationException();
	}

	public interface Location {
		String getName();

		boolean isOutputLocation();

		default boolean isModuleOrientedLocation() {
			return this.getName().matches("\\bMODULE\\b");
		}
	}
}