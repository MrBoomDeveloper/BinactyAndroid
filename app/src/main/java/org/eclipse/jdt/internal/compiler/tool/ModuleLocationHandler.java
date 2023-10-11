/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation.
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
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.tool;

import androidx.annotation.NonNull;

import com.mrboomdev.java.tools.JavaFileManager;
import com.mrboomdev.java.tools.StandardLocation;

import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModuleLocationHandler {

	Map<JavaFileManager.Location, LocationContainer> containers;

	ModuleLocationHandler() {
		this.containers = new HashMap<>();
	}

	public void newSystemLocation(JavaFileManager.Location loc, ClasspathJrt cp) throws IOException {
		SystemLocationContainer systemLocationWrapper = new SystemLocationContainer(StandardLocation.SYSTEM_MODULES, cp);
		this.containers.put(loc, systemLocationWrapper);
	}
	public void newSystemLocation(JavaFileManager.Location loc, JrtFileSystem jrt) throws IOException {
		SystemLocationContainer systemLocationWrapper = new SystemLocationContainer(StandardLocation.SYSTEM_MODULES, jrt);
		this.containers.put(loc, systemLocationWrapper);
	}
	public void newOutputLocation(JavaFileManager.Location loc) {
		OutputLocationContainer outputWrapper = new OutputLocationContainer(loc);
		this.containers.put(loc,  outputWrapper);
	}

	public LocationWrapper getLocation(JavaFileManager.Location loc, String moduleName) {
		if (loc instanceof LocationWrapper) {
			loc = ((LocationWrapper) loc).loc;
		}
		LocationContainer forwarder = this.containers.get(loc);
		if (forwarder != null) {
			return forwarder.get(moduleName);
		}
		return null;
	}

	public JavaFileManager.Location getLocation(JavaFileManager.Location loc, Path path) {
		LocationContainer forwarder = this.containers.get(loc);
		if (forwarder != null) {
			return forwarder.get(path);
		}
		return null;
	}
	public LocationContainer getLocation(JavaFileManager.Location location) {
		return this.containers.get(location);
	}
	private LocationContainer createNewContainer(JavaFileManager.Location loc) {
		LocationContainer container = (loc == StandardLocation.CLASS_OUTPUT) ?
				new OutputLocationContainer(loc) : new LocationContainer(loc);
		this.containers.put(loc, container);
		return container;
	}
	public void setLocation(JavaFileManager.Location location, Iterable<? extends Path> paths) {
		LocationContainer container = this.containers.get(location);
		if (container == null) {
			container = createNewContainer(location);
		}
		container.setPaths(paths);
	}
	public void setLocation(JavaFileManager.Location location, String moduleName, Iterable<? extends Path> paths) {
		LocationWrapper wrapper = null;
		LocationContainer container = this.containers.get(location);
		if (container != null) {
			wrapper = container.get(moduleName);
		} else {
			container = createNewContainer(location);
		}
		if (wrapper == null) {
			// module name can't be null
			// TODO: Check unnamed modules can have their own module specific path - probably not
			if (moduleName.equals("")) { //$NON-NLS-1$
				wrapper = new LocationWrapper(location, location.isOutputLocation(), paths);
			} else {
				wrapper = new ModuleLocationWrapper(location, moduleName, location.isOutputLocation(), paths);
				for (Path path : paths) {
					container.put(path, wrapper);
				}
			}
		} else {
			wrapper.setPaths(paths);
		}
		container.put(moduleName, wrapper);
	}
	public Iterable<Set<JavaFileManager.Location>> listLocationsForModules(JavaFileManager.Location location) {
		LocationContainer locationContainer = this.containers.get(location);
		if (locationContainer == null) {
			return Collections.emptyList();
		}
		Set<JavaFileManager.Location> set = new HashSet<>(locationContainer.locationNames.values());
		return Collections.singletonList(set);
	}

	class LocationContainer extends LocationWrapper {

		Map<String, LocationWrapper> locationNames;
		Map<Path, LocationWrapper> locationPaths;
		LocationContainer(JavaFileManager.Location loc) {
			super();
			this.loc = loc;
			this.locationNames = new HashMap<>();
			this.locationPaths = new HashMap<>();
		}

		LocationWrapper get(String moduleName) {
			return this.locationNames.get(moduleName);
		}

		void put(String moduleName, LocationWrapper impl) {
			this.locationNames.put(moduleName, impl);
			this.paths = null;
		}

		void put(Path path, LocationWrapper impl) {
			this.locationPaths.put(path, impl);
			this.paths = null;
		}

		JavaFileManager.Location get(Path path) {
			return this.locationPaths.get(path);
		}

		@Override
		void setPaths(Iterable<? extends Path> paths) {
			 super.setPaths(paths);
			 this.clear();
		 }
		@Override
		Iterable<? extends Path> getPaths() {
			if (this.paths != null)
				return this.paths;
			return this.locationPaths.keySet();
		}

		public void clear() {
			this.locationNames.clear();
			this.locationPaths.clear();
		}
	}

	class SystemLocationContainer extends LocationContainer {

		public SystemLocationContainer(JavaFileManager.Location loc, JrtFileSystem jrt) throws IOException {
			super(loc);
			jrt.initialize();
			HashMap<String, Path> modulePathMap = jrt.modulePathMap;
			Set<String> keySet = modulePathMap.keySet();
			for (String mod : keySet) {
				Path path = jrt.file.toPath();
				ModuleLocationWrapper wrapper = new ModuleLocationWrapper(loc, mod, false,
						Collections.singletonList(path));
				this.locationNames.put(mod, wrapper);
				this.locationPaths.put(path, wrapper);
			}
		}
		public SystemLocationContainer(JavaFileManager.Location loc, ClasspathJrt cp) throws IOException {
			this(loc, new JrtFileSystem(cp.file));
		}
	}
	class OutputLocationContainer extends LocationContainer {

		OutputLocationContainer(JavaFileManager.Location loc) {
			super(loc);
		}

		@Override
		void put(String moduleName, LocationWrapper impl) {
			this.locationNames.put(moduleName, impl);
		}

		@Override
		void put(Path path, LocationWrapper impl) {
			this.locationPaths.put(path, impl);
		}
	}

	static class LocationWrapper implements JavaFileManager.Location {

		JavaFileManager.Location loc;
		boolean output;
		List<? extends Path> paths;
		LocationWrapper() {
		}
		public LocationWrapper(JavaFileManager.Location loc, boolean output, Iterable<? extends Path> paths) {
			this.loc = loc;
			this.output = output;
			setPaths(paths);
		}

		@Override
		public String getName() {
			return this.loc.getName();
		}

		@Override
		public boolean isOutputLocation() {
			return this.output;
		}

		Iterable<? extends Path> getPaths() {
			return this.paths;
		}

		void setPaths(Iterable<? extends Path> paths) {
			if (paths == null) {
				this.paths = null;
			} else {
				List<Path> newPaths = new ArrayList<>();
				for (Path file : paths) {
					newPaths.add(file);
				}
				this.paths = Collections.unmodifiableList(newPaths);
			}
		}

		@NonNull
		@Override
		public String toString() {
			return this.loc.toString() + "[]"; //$NON-NLS-1$
		}
	}

	class ModuleLocationWrapper extends LocationWrapper {
		String modName;

		public ModuleLocationWrapper(JavaFileManager.Location loc, String mod, boolean output, Iterable<? extends Path> paths) {
			super(loc, output, paths);
			this.modName = mod;
		}

		@Override
		public String getName() {
			return this.loc.getName() + "[" + this.modName + "]"; //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		public boolean isOutputLocation() {
			return this.output;
		}

		@Override
		Iterable<? extends Path> getPaths() {
			return this.paths;
		}

		@NonNull
		@Override
		public String toString() {
			return this.loc.toString() + "[" + this.modName + "]"; //$NON-NLS-1$//$NON-NLS-2$
		}
	}
	public void close() {
		Collection<LocationContainer> values = this.containers.values();
		for (LocationContainer locationContainer : values) {
			locationContainer.clear();
		}
	}
}