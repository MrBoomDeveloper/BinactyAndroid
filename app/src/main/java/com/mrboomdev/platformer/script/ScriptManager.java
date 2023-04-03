package com.mrboomdev.platformer.script;

import bsh.EvalError;
import bsh.Interpreter;
import com.badlogic.gdx.Gdx;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.script.bridge.EntitiesBridge;
import com.mrboomdev.platformer.script.bridge.GameBridge;
import com.mrboomdev.platformer.script.bridge.UiBridge;

public class ScriptManager {
	public GameBridge gameBridge;
	public EntitiesBridge entitiesBridge;
	public UiBridge uiBridge;
	private GameHolder game = GameHolder.getInstance();
	private Interpreter interpreter;
	
	public ScriptManager() {
		this.interpreter = new Interpreter();
		this.gameBridge = new GameBridge();
		this.uiBridge = new UiBridge();
		this.entitiesBridge = new EntitiesBridge();
		
		this.eval("import com.mrboomdev.platformer.entity.Entity.Target;");
		this.put("game", gameBridge);
		this.put("ui", uiBridge);
		this.put("entities", entitiesBridge);
		this.put("core", game);
	}
	
	public void eval(String code) {
		try {
			interpreter.eval(code);
		} catch(EvalError e) {
			e.printStackTrace();
			Gdx.files.external("crash.txt").writeString(e.getMessage(), false);
			game.launcher.exit(GameLauncher.Status.CRASH);
		}
	}
	
	public <T> T get(String reference) {
		return null;
	}
	
	public void put(String reference, Object value) {
		try {
			interpreter.set(reference, value);
		} catch(EvalError e) {
			e.printStackTrace();
			Gdx.files.external("crash.txt").writeString(e.getMessage(), false);
			game.launcher.exit(GameLauncher.Status.CRASH);
		}
	}
}