package com.mrboomdev.platformer.script.entry;

import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;

import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaEntry extends JvmEntry {
	private static final String TAG = "JavaEntry";

	@Override
	public void compile(PackData.GamemodeEntry entry) {
		var thread = new Thread(() -> {
			try(var stringWriter = new StringWriter();
				var printWriter = new PrintWriter(stringWriter)) {
				var output = BoomFile.external("cached/" + entry.mainPath.hashCode());

				var classPath = BoomFile.external("classpath.jar");
				classPath.remove();
				BoomFile.internal("classpath.jar").copyTo(classPath);

				var compileTargets = (entry.source == BoomFile.Source.INTERNAL
					? output.goTo("/java_copy").getPath()
					: output.getPath()) + "/**/*.java";

				if(entry.source == BoomFile.Source.INTERNAL) {
					var target = output.goTo("/java_copy");
					BoomFile.internal(entry.scriptsPath).copyTo(target);
				}

				var destination = BoomFile.global(output.goTo("/java_compiled"));
				destination.remove();

				List<String> args = new ArrayList<>(List.of(
						"-" + "11",
						"-proc:none",
						"-cp", classPath.getPath(),
						"-d", destination.getPath(),
						compileTargets
				));

				/*args.addAll(BoomFile.fromString(entry.scriptsPath, entry.source)
						.listRecursively()
						.stream()
						.filter(item -> item.getPath().endsWith(".java"))
						.map(item -> BoomFile.global(item).getPath())
						.collect(Collectors.toList()));*/

				LogUtil.debug(TAG, "Start with the following args: " + Arrays.toString(args.toArray(new String[0])));

				var jdtCompiler = new Main(printWriter, printWriter, false, null, null);
				boolean isSuccessful = jdtCompiler.compile(args.toArray(new String[0]));

				if(isSuccessful) {
					LogUtil.debug(TAG, "Finished compilation successfully! Compiler output: " + stringWriter);
					super.compile(entry);
				} else {
					throw new BoomException("Failed to compile java! ", new BoomException(stringWriter));
				}
			} catch(IOException e) {
				throw new BoomException(e);
			}
		});

		thread.setName("JavaCompilerThread");
		thread.start();
	}

	@Override
	public boolean isReady() {
		return false;
	}
}