package com.mrboomdev.platformer.script;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.script.entry.BeanshellEntry;
import com.mrboomdev.platformer.script.entry.JavaEntry;
import com.mrboomdev.platformer.script.entry.JvmEntry;
import com.mrboomdev.platformer.script.entry.ScriptEntry;
import com.mrboomdev.platformer.util.helper.BoomException;

import org.jetbrains.annotations.Contract;

public class ScriptCompiler {

	@NonNull
	@Contract("_ -> new")
	public static ScriptEntry compile(@NonNull PackData.GamemodeEntry entry) {
		switch(entry.engine) {
			case JAVA: return new JavaEntry();
			case JVM: return new JvmEntry();
			case BEANSHELL: return new BeanshellEntry();
			default: throw new BoomException("Unknown entry engine: " + entry.engine);
		}
	}
}