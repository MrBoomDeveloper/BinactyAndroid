package com.mrboomdev.binacty.script.entry;

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
	private BoomFile<?> collectSourcesInFile(@NonNull PackData.GamemodeEntry entry, @NonNull List<BoomFile<?>> javaSourcesList) {
		var output = BoomFile.external(".cache/" + entry.id);

		var compileTargets = output.goTo("sources.txt");
		compileTargets.remove();
		compileTargets.writeString("");

		for(var clazz : javaSourcesList) {
			var clazzPath = clazz.getAbsolutePath();
			compileTargets.writeString(clazzPath + "\n", true);
		}

		return compileTargets;
	}

	public List<BoomFile<?>> prepareAndGetSources(@NonNull PackData.GamemodeEntry entry) {
		var output = BoomFile.external(".cache/" + entry.id);
		var javaSources = BoomFile.fromString(entry.scriptsPath, entry.scriptsSource);

		if(entry.scriptsSource == BoomFile.Source.INTERNAL) {
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

		return javaSourcesList;
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

			var sources = prepareAndGetSources(entry);
			var sourcesChecksum = getSourcesChecksum(sources);
			progress = .4f;

			var javaCompiled = BoomFile.global(output.goTo("/java_compiled"));
			var lastSourcesChecksum = output.goTo("java_sources_checksum.txt");

			if(lastSourcesChecksum.exists() && lastSourcesChecksum.readString().equals(sourcesChecksum)) {
				LogUtil.debug(TAG, "Skipping compilation, because sources are same.");
				finishSuccessfully(entry, javaCompiled);
				return;
			}

			var classpaths = prepareAndGetClasspathsJoined();
			progress = .6f;

			List<String> args = List.of(
					"-" + "11",
					"-proc:none",
					"-d", javaCompiled.getAbsolutePath(),
					"-cp", classpaths,
					"@" + collectSourcesInFile(entry, sources).getAbsolutePath());

			LogUtil.debug(TAG, "Start with the following args: " + Arrays.toString(args.toArray(new String[0])));

			javaCompiled.remove();
			progress = .8f;

			var jdtCompiler = new Main(printWriter, printWriter, false, null, null);
			boolean isSuccessful = jdtCompiler.compile(args.toArray(new String[0]));

			if(isSuccessful) {
				LogUtil.debug(TAG, "Finished compilation successfully! Compiler output: " + stringWriter);

				lastSourcesChecksum.writeString(sourcesChecksum);
				finishSuccessfully(entry, javaCompiled);
			} else {
				throw new BoomException("Failed to compile java! ", new BoomException(stringWriter));
			}
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}

	private void finishSuccessfully(PackData.GamemodeEntry entry, @NonNull BoomFile<?> javaCompiled) {
		progress = 1;

		var newEntry = FunUtil.copy(PackData.GamemodeEntry.class, entry);
		newEntry.scriptsSource = BoomFile.Source.GLOBAL;
		newEntry.scriptsPath = javaCompiled.getAbsolutePath();

		setEntry(newEntry);
		super.compile();
	}
}