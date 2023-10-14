package com.mrboomdev.platformer.script.engine;

import androidx.annotation.NonNull;

import com.android.tools.r8.CompilationFailedException;
import com.android.tools.r8.CompilationMode;
import com.android.tools.r8.D8;
import com.android.tools.r8.D8Command;
import com.android.tools.r8.OutputMode;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.binacty.util.file.InternalBoomFile;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MbsCompiler {
	private static final String TAG = "MbsCompiler";
	private final BoomFile<?> output;
	private BoomFile<?> classpath;
	private final List<String> sources = new ArrayList<>();

	public MbsCompiler(String id) {
		this.output = BoomFile.external("compiled/" + id);
	}

	public void prepareFiles() {
		output.remove();

		output.goTo("java").createDirectory();
		output.goTo("dex").createDirectory();

		setClasspath(
				BoomFile.internal("classpath.jar"),
				BoomFile.external("classpath.jar"));
	}

	public void setClasspath(@NonNull BoomFile<?> classpath, @NonNull BoomFile<?> destination) {
		destination.remove();

		classpath.copyTo(destination);
		this.classpath = BoomFile.global(destination);
	}

	public void addSources(@NonNull List<BoomFile<?>> files) {
		var fullPaths = files.stream()
				.map(item -> {
					if(item instanceof InternalBoomFile) {
						throw new BoomException("Currently unavailable!");
					}

					return item.getAbsolutePath();
				})
				.collect(Collectors.toList());

		sources.addAll(fullPaths);
	}

	public void compileJava() throws IOException, InvocationTargetException {
		try(var stringWriter = new StringWriter();
			var printWriter = new PrintWriter(stringWriter)) {

			var output = this.output.goTo("java");

			List<String> args = new ArrayList<>(List.of(
					"-" + "11",
					"-proc:none",
					"-cp", classpath.getAbsolutePath(),
					"-d", output.getAbsolutePath()
			));

			args.addAll(sources);

			LogUtil.debug(TAG, "Start with the following args: " + Arrays.toString(args.toArray(new String[0])));

			var jdtCompiler = new org.eclipse.jdt.internal.compiler.batch.Main(printWriter, printWriter, false, null, null);
			boolean isSuccessful = jdtCompiler.compile(args.toArray(new String[0]));

			if(isSuccessful) {
				LogUtil.debug(TAG, "Finished compilation successfully! Compiler output: " + stringWriter);
			} else {
				throw new BoomException("Failed to compile java! ", new BoomException(stringWriter));
			}
		}
	}

	public void compileDex() throws CompilationFailedException {
		var output = this.output.goTo("dex");

		var jars = new ArrayList<Path>();

		D8.run(D8Command.builder()
				.setMode(CompilationMode.DEBUG)
				.setIntermediate(true)
				.setMinApiLevel(26)
				.addProgramFiles(jars)
				.addProgramFiles()
				.setOutput(output.getFile().toPath(), OutputMode.DexIndexed)
				.build());
	}
}