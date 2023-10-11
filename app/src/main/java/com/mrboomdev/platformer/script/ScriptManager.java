package com.mrboomdev.platformer.script;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.script.bridge.AudioBridge;
import com.mrboomdev.platformer.script.bridge.EntitiesBridge;
import com.mrboomdev.platformer.script.bridge.GameBridge;
import com.mrboomdev.platformer.script.bridge.MapBridge;
import com.mrboomdev.platformer.script.bridge.UiBridge;
import com.mrboomdev.platformer.script.entry.ScriptEntry;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class ScriptManager {
	public GameBridge gameBridge;
	public EntitiesBridge entitiesBridge;
	public UiBridge uiBridge;
	public AudioBridge audioBridge;
	public MapBridge mapBridge;
	private boolean isCompiledAll;
	private final List<ScriptEntry> entries = new ArrayList<>();
	//private Interpreter interpreter;
	//private ScriptEntry runner;

	public boolean isReady() {
		if(entries.isEmpty()) return false;

		for(var entry : entries) {
			if(!entry.isReady()) return false;
		}

		return true;
	}

	public void compile(@NonNull List<PackData.GamemodeEntry> entries) {
		for(var entry : entries) {
			var entryHolder = ScriptCompiler.compile(entry);
			entryHolder.compile(entry);
			this.entries.add(entryHolder);
		}
	}

	public String ping() {
		if(!isCompiledAll) {
			for(var entry : entries) {
				if(entry.isCompiled()) isCompiledAll = true;
			}

			return "Compiling scripts...";
		}

		return "Idk...";
	}

	private void intiMbs(FileUtil source) {
		//var a = new MbsEngine("HelloWorld", source);
		//a.compile();

		//a.loadClass("HelloWorld");
		//var b = a.getClass("HelloWorld");
		//var d = b.getMethod("a");
		//d.invoke(null);
		//var c = b.newInstance();

		//System.out.println(c);
	}

	private void initJar(FileUtil source, String main) {
		/*try {
			ClassLoader loader = new DexClassLoader(
					source.getFullPath(true),
					source.getParent().goTo("cache").getFullPath(true),
					null,
					getClass().getClassLoader());

			Class<?> mainClass = loader.loadClass(main);
			//runner = (ScriptEntry) mainClass.newInstance();
			//runner.init(new ScriptClient());
		} catch(IllegalAccessException e) {
			handleException(e, "Required constructor has private access level. Please, make it public, so we can invoke it!");
		} catch(ClassNotFoundException e) {
			handleException(e, "Can't find script's Main! Check if you entered the correct path to it!");
		} catch(ClassCastException e) {
			handleException(e, "Main Entry doesn't implements ScriptEntry interface!");
		} catch(Exception e) {
			handleException(e);
		}*/
	}

	private void initBeanshell(FileUtil source) {
		//this.interpreter = new Interpreter();

		var path = "packs/core/src/scripts/DefaultScript.java";
		this.eval(FileUtil.internal(path).readString());

		initScriptable(source);
	}

	private void initScriptable(@NonNull FileUtil source) {
		this.gameBridge = new GameBridge(source);
		this.mapBridge = new MapBridge();
		this.uiBridge = new UiBridge();
		this.entitiesBridge = new EntitiesBridge(source);
		this.audioBridge = new AudioBridge(source.getParent());

		this.put("__source", source.getParent());
		this.put("game", gameBridge);
		this.put("ui", uiBridge);
		this.put("map", mapBridge);
		this.put("entities", entitiesBridge);
		this.put("audio", audioBridge);
		this.put("core", GameHolder.getInstance());
	}
	
	public void eval(String code) {
		//if(engine == GameSettings.Engine.GROOVY) return;

		try {
			//interpreter.eval(code);
		} catch(Exception e) {
			handleException(e);
		}
	}
	
	public void put(String reference, Object value) {
		/*switch(engine) {
			case BEANSHELL: try {
				interpreter.set(reference, value);
			} catch(EvalError e) {
				handleException(e);
			} break;

			*//*case GROOVY: {
				//groovyEntry.setProperty(reference, value);
			} break;*//*

			default: {
				throw new BoomException("Unsupported engine!");
			}
		}*/
	}

	public void triggerLoaded() {
		//if(runner != null) runner.loaded();
		//if(interpreter != null) gameBridge.callListener(GameBridge.Function.BUILD);
	}

	public void triggerStarted() {
		//if(runner != null) runner.start();
		//if(interpreter != null) gameBridge.callListener(GameBridge.Function.START);
	}

	public void triggerEnded() {
		//if(runner != null) runner.finish();
		//if(interpreter != null) gameBridge.callListener(GameBridge.Function.END);
	}
	
	private void handleException(@NonNull Throwable t) {
		handleException(t, "It looks that something strange has happened and we don't know why ._.");
	}

	private void handleException(@NonNull Throwable t, String message) {
		throw new BoomException("Script error has occurred!\n" + message, t);
	}
}