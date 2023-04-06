package com.mrboomdev.platformer.script;

import bsh.EvalError;
import bsh.Interpreter;
import com.badlogic.gdx.Gdx;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.script.bridge.AudioBridge;
import com.mrboomdev.platformer.script.bridge.EntitiesBridge;
import com.mrboomdev.platformer.script.bridge.GameBridge;
import com.mrboomdev.platformer.script.bridge.UiBridge;
import com.mrboomdev.platformer.util.io.FileUtil;

public class ScriptManager {
	public GameBridge gameBridge;
	public EntitiesBridge entitiesBridge;
	public UiBridge uiBridge;
	public AudioBridge audioBridge;
	private GameHolder game = GameHolder.getInstance();
	private Interpreter interpreter;
	private FileUtil source;
	
	public ScriptManager(FileUtil source) {
		this.source = source;
		this.interpreter = new Interpreter();
		this.gameBridge = new GameBridge(source);
		this.uiBridge = new UiBridge();
		this.entitiesBridge = new EntitiesBridge(source);
		this.audioBridge = new AudioBridge();
		
		this.eval("import com.mrboomdev.platformer.entity.Entity.Target;");
		this.eval("import com.mrboomdev.platformer.script.bridge.GameBridge.GameListener;");
		this.eval("import com.mrboomdev.platformer.script.bridge.EntitiesBridge.EntityListener;");
		this.eval("import com.mrboomdev.platformer.script.bridge.UiBridge.UiListener;");
		this.eval("import com.mrboomdev.platformer.entity.character.CharacterCreator;");
		this.put("game", gameBridge);
		this.put("ui", uiBridge);
		this.put("entities", entitiesBridge);
		this.put("audio", audioBridge);
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