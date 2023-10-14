package com.mrboomdev.platformer.script.entry;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;

import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaEntry extends JvmEntry {
	private static final String TAG = "JavaEntry";
	private float progress;

	public JavaEntry(PackData.GamemodeEntry entry) {
		super(entry);
	}

	@NonNull
	private String prepareAndGetSourcesPath(@NonNull PackData.GamemodeEntry entry) {
		var output = BoomFile.external(".cache/" + entry.id);
		var javaSources = BoomFile.fromString(entry.scriptsPath, entry.source);

		if(entry.source == BoomFile.Source.INTERNAL) {
			javaSources = output.goTo("/java_copy");
			javaSources.remove();
			BoomFile.internal(entry.scriptsPath).copyTo(javaSources);
		}

		var allowedExtensions = List.of(".java", ".jar");

		List<BoomFile<?>> javaSourcesList = new ArrayList<>(javaSources.listFilesRecursively());

		javaSourcesList = javaSourcesList.stream()
				.filter(item -> {
					var path = item.getRelativePath();

					return allowedExtensions.stream().anyMatch(path::endsWith);
				})
				.collect(Collectors.toList());

		var compileTargets = output.goTo("sources.txt");
		compileTargets.remove();
		compileTargets.writeString("");

		for(var clazz : javaSourcesList) {
			var clazzPath = clazz.getAbsolutePath();
			compileTargets.writeString(clazzPath + "\n", true);
		}

		return compileTargets.getAbsolutePath();
	}

	@NonNull
	private String prepareAndGetClasspathsJoined() {
		var classpaths = prepareAndGetClasspaths();

		StringBuilder result = new StringBuilder();

		for(int i = 0; i < classpaths.size(); i++) {
			if(i > 0) result.append(":");
			result.append(classpaths.get(i).getAbsolutePath());
		}

		return result.toString();
	}

	@Override
	public float getProgress() {
		return (progress + super.getProgress()) / 2;
	}

	@Override
	public void compile() {
		try(var stringWriter = new StringWriter(); var printWriter = new PrintWriter(stringWriter)) {
			var entry = getEntry();
			var output = BoomFile.external(".cache/" + entry.id);
			progress = .2f;

			var javaCompiled = BoomFile.global(output.goTo("/java_compiled"));
			javaCompiled.remove();
			progress = .4f;

			var classpaths = prepareAndGetClasspathsJoined();
			progress = .6f;

			var sources = prepareAndGetSourcesPath(entry);
			progress = .8f;

			List<String> args = List.of(
					"-" + "11",
					"-proc:none",
					"-d", javaCompiled.getAbsolutePath(),
					"-cp", classpaths,
					"@" + sources);

			LogUtil.debug(TAG, "Start with the following args: " + Arrays.toString(args.toArray(new String[0])));

			var jdtCompiler = new Main(printWriter, printWriter, false, null, null);
			boolean isSuccessful = jdtCompiler.compile(args.toArray(new String[0]));
			progress = 1;

			if(isSuccessful) {
				LogUtil.debug(TAG, "Finished compilation successfully! Compiler output: " + stringWriter);

				var newEntry = FunUtil.copy(PackData.GamemodeEntry.class, entry);
				newEntry.source = BoomFile.Source.GLOBAL;
				newEntry.scriptsPath = javaCompiled.getAbsolutePath();

				setEntry(newEntry);
				super.compile();
			} else {
				throw new BoomException("Failed to compile java! ", new BoomException(stringWriter));
			}
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}
}