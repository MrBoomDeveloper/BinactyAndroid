package com.mrboomdev.java.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface StandardJavaFileManager extends JavaFileManager {
	boolean isSameFile(FileObject var1, FileObject var2);

	Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> var1);

	default Iterable<? extends JavaFileObject> getJavaFileObjectsFromPaths(Collection<? extends Path> paths) {
		return this.getJavaFileObjectsFromFiles(asFiles(paths));
	}

	/** @deprecated */
	@Deprecated(
			since = "13"
	)
	default Iterable<? extends JavaFileObject> getJavaFileObjectsFromPaths(Iterable<? extends Path> paths) {
		return this.getJavaFileObjectsFromPaths(asCollection(paths));
	}

	Iterable<? extends JavaFileObject> getJavaFileObjects(File... var1);

	default Iterable<? extends JavaFileObject> getJavaFileObjects(Path... paths) {
		return this.getJavaFileObjectsFromPaths(Arrays.asList(paths));
	}

	Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> var1);

	Iterable<? extends JavaFileObject> getJavaFileObjects(String... var1);

	void setLocation(JavaFileManager.Location var1, Iterable<? extends File> var2) throws IOException;

	default void setLocationFromPaths(JavaFileManager.Location location, Collection<? extends Path> paths) throws IOException {
		this.setLocation(location, asFiles(paths));
	}

	default void setLocationForModule(JavaFileManager.Location location, String moduleName, Collection<? extends Path> paths) throws IOException {
		throw new UnsupportedOperationException();
	}

	Iterable<? extends File> getLocation(JavaFileManager.Location var1);

	default Iterable<? extends Path> getLocationAsPaths(JavaFileManager.Location location) {
		return asPaths(this.getLocation(location));
	}

	default Path asPath(FileObject file) {
		throw new UnsupportedOperationException();
	}

	default void setPathFactory(PathFactory f) {
	}

	private static Iterable<Path> asPaths(Iterable<? extends File> files) {
		return () -> {
			return new Iterator<Path>() {
				final Iterator<? extends File> iter = files.iterator();

				public boolean hasNext() {
					return this.iter.hasNext();
				}

				public Path next() {
					return ((File)this.iter.next()).toPath();
				}
			};
		};
	}

	private static Iterable<File> asFiles(Iterable<? extends Path> paths) {
		return () -> {
			return new Iterator<File>() {
				final Iterator<? extends Path> iter = paths.iterator();

				public boolean hasNext() {
					return this.iter.hasNext();
				}

				public File next() {
					Path p = (Path)this.iter.next();

					try {
						return p.toFile();
					} catch (UnsupportedOperationException var3) {
						throw new IllegalArgumentException(p.toString(), var3);
					}
				}
			};
		};
	}

	private static <T> Collection<T> asCollection(Iterable<T> iterable) {
		if (iterable instanceof Collection) {
			return (Collection)iterable;
		} else {
			List<T> result = new ArrayList<>();
			Iterator var2 = iterable.iterator();

			while(var2.hasNext()) {
				T item = (T) var2.next();
				result.add(item);
			}

			return result;
		}
	}

	interface PathFactory {
		Path getPath(String var1, String... var2);
	}
}