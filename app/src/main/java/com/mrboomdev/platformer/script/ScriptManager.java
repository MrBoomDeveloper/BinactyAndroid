package com.mrboomdev.platformer.script;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.api.entry.ScriptEntry;
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
import dalvik.system.DexClassLoader;

public class ScriptManager {
	public GameBridge gameBridge;
	public EntitiesBridge entitiesBridge;
	public UiBridge uiBridge;
	public AudioBridge audioBridge;
	public MapBridge mapBridge;
	private Interpreter interpreter;
	private ScriptEntry runner;

	public ScriptManager(FileUtil source, String main, boolean isNative) {
		if(isNative) {
			try {
				ClassLoader loader = new DexClassLoader(
						source.getFullPath(true),
						source.getParent().goTo("cache").getFullPath(true),
						null,
						getClass().getClassLoader());

				Class<?> mainClass = loader.loadClass(main);
				runner = (ScriptEntry) mainClass.newInstance();
				runner.init(new ScriptClient());
			} catch(IllegalAccessException e) {
				handleException(e, "Required constructor has private access level. Please, make it public, so we can invoke it!");
			} catch(ClassNotFoundException e) {
				handleException(e, "Can't find script's Main! Check if you entered the correct path to it!");
			} catch(ClassCastException e) {
				handleException(e, "Main Entry doesn't implements ScriptEntry interface!");
			} catch(Exception e) {
				handleException(e);
			}
			return;
		}

		if(GameHolder.getInstance().settings.playerName.equals("__class")) {
			new ScriptManager(FileUtil.external("pack.jar"),
					"com.mrboomdev.platformer.pack.cutiemarry.ScriptMain", true);
		}

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

	public void triggerLoaded() {
		if(runner != null) runner.loaded();
		if(interpreter != null) gameBridge.callListener(GameBridge.Function.BUILD);
	}

	public void triggerStarted() {
		if(runner != null) runner.start();
		if(interpreter != null) gameBridge.callListener(GameBridge.Function.START);
	}

	public void triggerEnded() {
		if(runner != null) runner.finish();
		if(interpreter != null) gameBridge.callListener(GameBridge.Function.END);
	}
	
	private void handleException(@NonNull Throwable t) {
		handleException(t, "It looks that something strange has happened and we don't know why ._.");
	}

	private void handleException(@NonNull Throwable t, String message) {
		t.printStackTrace();
		LogUtil.crash("Script error has occurred!", message, t);
	}
}