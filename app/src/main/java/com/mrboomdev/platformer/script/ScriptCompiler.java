package com.mrboomdev.platformer.script;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.script.entry.BeanshellEntry;
import com.mrboomdev.platformer.script.entry.JavaEntry;
import com.mrboomdev.platformer.script.entry.JvmEntry;
import com.mrboomdev.platformer.script.entry.ScriptEntry;
import com.mrboomdev.platformer.util.helper.BoomException;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class ScriptCompiler {
	private final List<ScriptEntry> entries = new ArrayList<>();
	private boolean isStartedLoading, isReady;
	private boolean isCompiling = true;

	@NonNull
	@Contract("_ -> new")
	private ScriptEntry createEntry(@NonNull PackData.GamemodeEntry entry) {
		switch(entry.engine) {
			case JAVA: return new JavaEntry(entry);
			case JVM: return new JvmEntry(entry);
			case BEANSHELL: return new BeanshellEntry(entry);
			default: throw new BoomException("Unknown entry engine: " + entry.engine);
		}
	}

	@NonNull
	@Contract("_ -> new")
	public ScriptEntry compile(@NonNull PackData.GamemodeEntry entry) {
		this.isCompiling = true;
		this.isReady = false;

		var myEntry = createEntry(entry);
		entries.add(myEntry);

		new Thread(myEntry::compile).start();

		return myEntry;
	}

	public float getProgress() {
		if(entries.isEmpty()) return 0;
		if(!isCompiling) return 1;

		float progress = entries.stream()
				.map(ScriptEntry::getProgress)
				.reduce(0f, Float::sum);

		return progress / entries.size();
	}

	public void loadIntoMemoryAll() {
		if(isStartedLoading) return;
		isStartedLoading = true;

		for(var entry : entries) {
			new Thread(entry::load).start();
		}
	}

	public boolean isReady() {
		if(isReady) return true;
		if(isCompiling) return false;

		for(var entry : entries) {
			if(!entry.isReady()) return false;
		}

		return (isReady = true);
	}

	public boolean isCompiling() {
		if(!isCompiling) return false;
		if(entries.isEmpty()) return true;

		for(var entry : entries) {
			if(!entry.isCompiled()) return true;
		}

		return (isCompiling = false);
	}
}