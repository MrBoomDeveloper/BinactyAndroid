package com.mrboomdev.platformer.script.entry;

import com.android.tools.r8.CompilationFailedException;
import com.android.tools.r8.D8;
import com.android.tools.r8.D8Command;
import com.android.tools.r8.OutputMode;
import com.mrboomdev.binacty.api.client.BinactyClient;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import dalvik.system.DexClassLoader;

public class JvmEntry extends ScriptEntry {
	private static final String TAG = "JvmEntry";
	private BoomFile<?> dexLocation;
	private BinactyClient client;
	private boolean isCompiled, isLoaded, didInit;
	private float progress;

	public JvmEntry(PackData.GamemodeEntry entry) {
		super(entry);
	}

	@Override
	public boolean isSoftReady() {
		return didInit && client.isReady();
	}

	@Override
	public boolean isReady() {
		return isCompiled() && isLoaded;
	}

	@Override
	public boolean isCompiled() {
		return isCompiled;
	}

	@Override
	public float getProgress() {
		return progress;
	}

	@Override
	public void compile() {
		try {
			var entry = getEntry();
			var output = BoomFile.external(".cache/" + entry.id);
			var inputSource = BoomFile.fromString(entry.scriptsPath, entry.source);

			dexLocation = output.goTo("dex_compiled.jar");
			dexLocation.remove();

			if(entry.source == BoomFile.Source.INTERNAL) {
				inputSource = output.goTo("jvm_copy/");
				inputSource.remove();
				progress = .2f;

				BoomFile.internal(entry.scriptsPath).copyTo(inputSource);
				progress = .4f;
			}

			var inputFiles = inputSource.listFilesRecursively()
					.stream()
					.map(item -> item.getFile().toPath())
					.collect(Collectors.toList());

			var classpaths = prepareAndGetClasspaths()
					.stream()
					.map(item -> item.getFile().toPath())
					.collect(Collectors.toList());

			progress = .8f;

			D8.run(D8Command.builder()
					.setIntermediate(true)
					.addClasspathFiles(classpaths)
					.setOutput(dexLocation.getFile().toPath(), OutputMode.DexIndexed)
					.addProgramFiles(inputFiles)
					.build());

			dexLocation.getFile().setWritable(false);

			isCompiled = true;
			progress = 1;
			LogUtil.debug(TAG, "Successfully compiled dex files!");
		} catch(CompilationFailedException e) {
			throw new BoomException("Failed to compile into DEX!", e);
		}
	}

	@Override
	public void load() {
		var entry = getEntry();
		var cache = BoomFile.external(".cache/" + entry.id);

		var classLoader = new DexClassLoader(
				dexLocation.getAbsolutePath(),
				cache.goTo("optimized/").getAbsolutePath(),
				null,
				getClass().getClassLoader());

		try {
			var clazz = classLoader.loadClass(entry.mainPath);
			var constructor = clazz.getConstructor(String.class);
			var client = constructor.newInstance(entry.id);

			if(client instanceof BinactyClient) {
				this.client = (BinactyClient) client;
				isLoaded = true;
			} else {
				throw new BoomException("Invalid parent class! Make sure that it extends a: \""
						+ BinactyClient.class.getName() + "\" class!");
			}
		} catch(ClassNotFoundException e) {
			throw new BoomException("Failed to found a requested class!", e);
		} catch(NoSuchMethodException e) {
			throw new BoomException("Failed to found a required class constructor!", e);
		} catch(InvocationTargetException e) {
			throw new BoomException("Failed to invoke a constructor!", e);
		} catch(IllegalAccessException e) {
			throw new BoomException("Can't access to a private constructor!", e);
		} catch(InstantiationException e) {
			throw new BoomException("Invalid class entry!", e);
		}
	}

	public List<BoomFile<?>> prepareAndGetClasspaths() {
		var output = BoomFile.external(".cache/libraries");

		var javaClasspath = BoomFile.global(output.goTo("classpath.jar"));
		var apiClasspath = BoomFile.global(output.goTo("api.jar"));

		javaClasspath.remove();
		apiClasspath.remove();

		BoomFile.internal("classpath.jar").copyTo(javaClasspath);
		BoomFile.internal("BinactyApi.jar").copyTo(apiClasspath);

		return List.of(apiClasspath, javaClasspath);
	}

	@Override
	public void start() {
		client.start();
	}

	@Override
	public void create() {
		client.create();
		didInit = true;
	}

	@Override
	public void destroy() {
		client.destroy();
	}
}