package com.mrboomdev.binacty.script;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.script.entry.ScriptEntry;
import com.mrboomdev.platformer.game.pack.PackData;

import java.util.ArrayList;
import java.util.List;

public class ScriptManager {
	public final ScriptBridge bridge = new ScriptBridge(this);
	private final ScriptCompiler compiler = new ScriptCompiler();
	private final List<ScriptEntry> entries = new ArrayList<>();
	private boolean didCalledCreate;

	public boolean isReady() {
		if(entries.isEmpty()
		|| compiler.isCompiling()
		|| !compiler.isReady()
		|| !isSoftReady()) return false;

		for(var entry : entries) {
			if(!entry.isReady()) return false;
		}

		return true;
	}

	public void compile(@NonNull List<PackData.GamemodeEntry> entries) {
		for(var entry : entries) {
			var entryHolder = compiler.compile(entry);
			this.entries.add(entryHolder);
		}
	}

	public String ping() {
		if(compiler.isCompiling()) {
			return "Compiling scripts " + Math.round(compiler.getProgress() * 100) + "%";
		}

		if(!compiler.isReady()) {
			compiler.loadIntoMemoryAll();

			return "Loading scripts into memory...";
		}

		if(!isSoftReady()) {
			if(!didCalledCreate) {
				for(var entry : entries)
					entry.create();

				didCalledCreate = true;
			}

			return "Starting the game...";
		}

		return "Done!";
	}

	private boolean isSoftReady() {
		for(var entry : entries) {
			if(!entry.isSoftReady()) return false;
		}

		return true;
	}
}