package com.mrboomdev.platformer.environment.gamemode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction.*;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.FileUtil;
import com.mrboomdev.platformer.widgets.FadeWidget;
import com.mrboomdev.platformer.widgets.TextWidget;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GamemodeManager implements CoreUi.UiDrawer {
	public List<StackOperation> stack = new ArrayList<>();
	public GamemodeScript script;
	private TextWidget title, timer;
	private FadeWidget fade;
	private GameHolder game = GameHolder.getInstance();
	private boolean isTimerSetup, isTimerEnd;
	private float time, timerSpeed;
	private Status status = Status.PREPAIRING;
	private Runnable buildCompletedCallback;
	private FileUtil source;
	public static GamemodeManager instance;
	
	public GamemodeManager(GamemodeScript script) {
		this.script = script;
		script.start.forEach(function -> stack.add(new StackOperation(function, null)));
		instance = this;
	}
	
	public GamemodeManager build(FileUtil source, Runnable callback) {
		LoadingFiles.loadToManager(script.load, source.getParent().getPath(), game.assets);
		this.buildCompletedCallback = callback;
		this.source = source;
		status = Status.LOADING_RESOURCES;
		return this;
	}
	
	public void ping() {
		if(status == Status.LOADING_RESOURCES && game.assets.update(17)) {
			buildCompletedCallback.run();
			status = Status.DONE;
		}
	}
	
	public void runFunction(GamemodeFunction function) {
		stack.add(new StackOperation(function, function));
	}
	
	private void triggerListeners(GamemodeFunction function) {
		if(script.listeners.containsKey(function.action)) {
			script.listeners.get(function.action).forEach(listener -> {
				stack.add(new StackOperation(listener, function));
			});
		}
	}
	
	private boolean resolveConditions(StackOperation operation) {
		if(operation.function.conditions == null) return true;
		var conditions = operation.function.conditions;
		
		if(operation.caller != null) {
			var caller = operation.caller;
			if(conditions.target != null && (conditions.target != caller.options.target)) return false;
		}
		return true;
	}
	
	@Override
	public void drawUi() {
		List<StackOperation> oldStack = new ArrayList<>(stack);
		for(StackOperation operation : oldStack) {
			triggerListeners(operation.function);
		}
		
		for(var operation : stack) {
			var function = operation.function;
			operation.progress += Gdx.graphics.getDeltaTime();
			if(!resolveConditions(operation)) continue;
			
			switch(function.action) {
				case GAME_OVER:
					game.stats.isWin = time == 0 ? true : false;
					game.launcher.exit(GameLauncher.Status.GAME_OVER);
					break;
					
				case PLAY_MUSIC:
					Array<Music> musicQueue = new Array<>();
					var assets = GameHolder.getInstance().assets;
					for(String track : function.options.queue) {
						musicQueue.add(assets.get(source.getParent().goTo(track).getPath()));
					}
					AudioUtil.playMusic(musicQueue, 100);
					break;
					
				case STOP_MUSIC:
					AudioUtil.stopMusic();
					break;
					
				case TIMER_SETUP:
					timer.setOpacity(1);
					time = function.options.time;
					timerSpeed = function.speed;
					isTimerSetup = true;
					break;
					
				case FADE:
					fade.start(function.options.from, function.options.to, function.speed);
					break;
					
				case TITLE:
					title.setText(function.options.text);
					title.setOpacity(operation.progress < 1
						? title.opacity + Gdx.graphics.getDeltaTime()
						: (operation.progress < (function.duration - 1)
							? (Math.min(1, title.opacity + Gdx.graphics.getDeltaTime()))
							: title.opacity - Gdx.graphics.getDeltaTime()));
					if(title.opacity <= 0) operation.isFinished = true;
					break;
			}
		}
		
		stack = stack.stream().filter(operation -> {
			return (operation.progress < operation.function.duration ||
				(operation.function.isLong ? !operation.isFinished : false));
		}).collect(Collectors.toList());
		
		updateTimer(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void setupStage(Stage stage) {
		timer = (TextWidget)new TextWidget("timer.ttf")
			.setOpacity(0)
			.toPosition(new Vector2(
				Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() - game.settings.screenInset))
			.addTo(stage);
		
		fade = (FadeWidget) new FadeWidget(script.options.initialFade).addTo(stage);
		
		title = (TextWidget) new TextWidget("title.ttf").setOpacity(0)
			.toPosition(new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 50))
			.addTo(stage);
	}
	
	private void updateTimer(float delta) {
		if(!isTimerSetup || isTimerEnd) return;
		
		time = Math.max(0, time - (delta * timerSpeed));
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
		timer.setText(dateFormat.format(time * 1000));
		
		if(time == 0) {
			isTimerEnd = true;
			runFunction(new GamemodeFunction(Action.TIMER_END, null, null));
		}
	}
	
	public class StackOperation {
		public float progress = 0;
		public boolean isFinished = false;
		public GamemodeFunction function;
		public GamemodeFunction caller;
		
		public StackOperation(GamemodeFunction function, GamemodeFunction caller) {
			this.function = function;
			this.caller = caller;
		}
	}
	
	private enum Status {
		PREPAIRING,
		LOADING_RESOURCES,
		DONE
	}
}