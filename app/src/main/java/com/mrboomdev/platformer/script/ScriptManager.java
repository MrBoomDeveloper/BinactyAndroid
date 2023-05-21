package com.mrboomdev.platformer.script;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.script.bridge.AudioBridge;
import com.mrboomdev.platformer.script.bridge.EntitiesBridge;
import com.mrboomdev.platformer.script.bridge.GameBridge;
import com.mrboomdev.platformer.script.bridge.MapBridge;
import com.mrboomdev.platformer.script.bridge.UiBridge;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.LogUtil;
import bsh.EvalError;
import bsh.Interpreter;

public class ScriptManager {
	public GameBridge gameBridge;
	public EntitiesBridge entitiesBridge;
	public UiBridge uiBridge;
	public AudioBridge audioBridge;
	public MapBridge mapBridge;
	private final Interpreter interpreter;

	public ScriptManager(FileUtil source) {
		this.interpreter = new Interpreter();
		this.gameBridge = new GameBridge(source);
		this.mapBridge = new MapBridge();
		this.uiBridge = new UiBridge();
		this.entitiesBridge = new EntitiesBridge(source);
		this.audioBridge = new AudioBridge(source.getParent());
		this.eval(FileUtil.internal("packs/core/src/scripts/DefaultScript.java").readString(true));
		this.put("__source", source.getParent());
		this.put("game", gameBridge);
		this.put("ui", uiBridge);
		this.put("map", mapBridge);
		this.put("entities", entitiesBridge);
		this.put("audio", audioBridge);
		this.put("core", GameHolder.getInstance());
	}
	
	public void eval(String code) {
		try {
			interpreter.eval(code);
		} catch(Exception e) {
			handleException(e);
		}
	}
	
	public void put(String reference, Object value) {
		try {
			interpreter.set(reference, value);
		} catch(EvalError e) {
			handleException(e);
		}
	}
	
	private void handleException(@NonNull Throwable t) {
		t.printStackTrace();
		LogUtil.crash("Script error has occurred!", LogUtil.throwableToString(t));
	}
}