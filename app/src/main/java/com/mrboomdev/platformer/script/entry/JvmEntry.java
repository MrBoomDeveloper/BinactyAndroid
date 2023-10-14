package com.mrboomdev.platformer.script.entry;

import com.android.tools.r8.CompilationFailedException;
import com.android.tools.r8.D8;
import com.android.tools.r8.D8Command;
import com.android.tools.r8.OutputMode;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;

import java.util.List;
import java.util.stream.Collectors;

public class JvmEntry implements ScriptEntry {
	private static final String TAG = "JvmEntry";
	private boolean isCompiled;

	@Override
	public boolean isReady() {
		return isCompiled();
	}

	@Override
	public boolean isCompiled() {
		return isCompiled;
	}

	@Override
	public void compile(PackData.GamemodeEntry entry) {
		try {
			var output = BoomFile.external(".cache/" + entry.id + "/dex_compiled.jar");
			var inputSource = BoomFile.fromString(entry.scriptsPath, entry.source);

			if(entry.source == BoomFile.Source.INTERNAL) {
				inputSource = output.goTo("/jvm_copy");
				inputSource.remove();
				BoomFile.internal(entry.scriptsPath).copyTo(inputSource);
			}

			var inputFiles = inputSource.listFilesRecursively()
					.stream()
					.map(item -> item.getFile().toPath())
					.collect(Collectors.toList());

			var classpaths = prepareAndGetClasspaths()
					.stream()
					.map(item -> item.getFile().toPath())
					.collect(Collectors.toList());

			D8.run(D8Command.builder()
					.setIntermediate(true)
					.addClasspathFiles(classpaths)
					.setOutput(output.getFile().toPath(), OutputMode.DexIndexed)
					.addProgramFiles(inputFiles)
					.build());

			isCompiled = true;
			LogUtil.debug(TAG, "Successfully compiled dex files!");
		} catch(CompilationFailedException e) {
			throw new BoomException("Failed to compile into DEX!", e);
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

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void destroy() {

	}
}