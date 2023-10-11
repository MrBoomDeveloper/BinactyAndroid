package com.mrboomdev.platformer.script.engine;

import com.android.tools.r8.CompilationFailedException;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MbsEngine {
	private static final String TAG = "MbsEngine";
	private final FileUtil source;
	private final String id;

	public MbsEngine(String id, FileUtil main) {
		this.id = id;
		this.source = main;
	}

	public void compile() {
		try {
			var compiler = new MbsCompiler(id);
			compiler.addSources(List.of(BoomFile.external("Test.java")));

			compiler.prepareFiles();
			compiler.compileJava();
			compiler.compileDex();
		} catch(CompilationFailedException e) {
			throw new BoomException("Failed to compile a dex file!", e);
		} catch(IOException e) {
			throw new BoomException("IOException has happened while compiling java classes!", e);
		} catch(InvocationTargetException e) {
			throw new BoomException("Failed to invoke your code!", e);
		}
	}
}