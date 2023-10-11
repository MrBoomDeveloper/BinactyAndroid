package com.mrboomdev.platformer.environment.gamemode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.binacty.game.core.CoreLauncher;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameSettings;
import com.mrboomdev.platformer.script.ScriptManager;
import com.mrboomdev.platformer.script.bridge.UiBridge;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.mrboomdev.platformer.widgets.FadeWidget;
import com.mrboomdev.platformer.widgets.TextWidget;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GamemodeManager {
	public List<StackOperation> stack = new LinkedList<>();
	public TextWidget title, timer;
	public FadeWidget fade;
	private final GameHolder game = GameHolder.getInstance();
	private boolean isTimerSetup, isTimerEnd;
	private float time = -1, timerSpeed;
	private Status status = Status.PREPARING;
	private Runnable buildCompletedCallback;
	private float gameOverTimeout;
	private boolean isBroken;
	
	public GamemodeManager(FileUtil scenario) {
		game.environment.gamemode = this;

		game.script = new ScriptManager(scenario, "pack.Main", GameSettings.Engine.BEANSHELL);
		if(!game.settings.enableEditor) game.script.eval(scenario.readString(true));
	}
	
	public GamemodeManager build(Runnable callback) {
		this.buildCompletedCallback = callback;
		status = Status.LOADING_RESOURCES;

		game.script.triggerLoaded();
		return this;
	}
	
	public String ping() {
		if(game.assets.update()
		&& game.externalAssets.update()
		&& status == Status.LOADING_RESOURCES) {
			buildCompletedCallback.run();
			status = Status.DONE;

			LogUtil.debug("GameStart", "Done evaluating gamemode's script");
			return "Done!";
		}

		int progress = Math.round((game.assets.getProgress() + game.externalAssets.getProgress()) * 50);
		return "Loading gamemode resources " + progress + "%";
	}
	
	public void runFunction(GamemodeFunction function) {
		stack.add(new StackOperation(function, function));
	}
	
	public void update() {
		if(game.settings.enableEditor || isBroken) {
			isBroken = true;
			return;
		}

		if(!stack.isEmpty()) {
			for(var operation : stack) {
				var function = operation.function;
				operation.progress += Gdx.graphics.getDeltaTime();

				switch(function.action) {
					case GAME_OVER:
						CameraUtil.setTarget(null);
						CameraUtil.setCameraZoom(.9f, .01f);

						game.settings.isControlsEnabled = false;
						game.settings.isUiVisible = false;
						game.stats.isWin = time == 0;

						gameOverTimeout = 1;
						fade.start(0, .5f, .5f);
						break;

					case TIMER_SETUP:
						timer.setOpacity(1);
						time = function.options.time;
						timerSpeed = function.speed;
						isTimerSetup = true;
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
				if(operation.progress < operation.function.duration) return true;
				return operation.function.isLong && !operation.isFinished;
			}).collect(Collectors.toList());
		}
		
		if(gameOverTimeout > 0) {
			gameOverTimeout += Gdx.graphics.getDeltaTime();

			if(gameOverTimeout > 2.5f) {
				isBroken = true;

				game.script.triggerEnded();
				game.launcher.exit(CoreLauncher.ExitStatus.GAME_OVER);
			}
		}
		
		updateTimer(Gdx.graphics.getDeltaTime());
	}
	
	public void createUi(Stage stage) {
		if(game.settings.enableEditor) return;

		timer = new TextWidget("timer.ttf").setOpacity(0)
			.toPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - game.settings.screenInset)
			.addTo(stage);

		fade = new FadeWidget(0).addTo(stage);
		
		title = new TextWidget("title.ttf").setOpacity(0)
			.toPosition(new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f + 50))
			.addTo(stage);
	}
	
	private void updateTimer(float delta) {
		if(!isTimerSetup || isTimerEnd) return;
		
		time = Math.max(0, time - (delta * timerSpeed));
		timer.setText(FunUtil.formatTimer(time, "mm:ss"));
		
		if(time == 0) {
			isTimerEnd = true;
			game.script.uiBridge.callListener(UiBridge.Function.TIMER_END);
		}
	}
	
	public static class StackOperation {
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
		PREPARING,
		LOADING_RESOURCES,
		DONE
	}
}