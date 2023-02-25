package com.mrboomdev.platformer.environment.gamemode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction.*;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.widgets.FadeWidget;
import com.mrboomdev.platformer.widgets.TextWidget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.microedition.khronos.opengles.GL10;

public class GamemodeManager implements CoreUi.UiDrawer {
	public List<StackOperation> stack = new ArrayList<>();
	public GamemodeScript script;
	private TextWidget title;
	private FadeWidget fade;
	private GameHolder game = GameHolder.getInstance();
	private boolean isFirstDraw = true;
	
	public GamemodeManager(GamemodeScript script) {
		this.script = script;
		script.start.forEach(function -> stack.add(new StackOperation(function)));
	}
	
	private void firstDraw() {
		isFirstDraw = false;
	}
	
	@Override
	public void drawUi() {
		if(isFirstDraw) firstDraw();
		
		stack.forEach(operation -> {
			var function = operation.function;
			operation.progress += Gdx.graphics.getDeltaTime();
			game.analytics.log("Gamemode", "Do operation: " + function.action.name());
			switch(function.action) {
				case FADE:
					fade.start(function.options.from, function.options.direction);
					break;
					
				case TITLE:
					title.setText(function.options.text);
					title.setOpacity(operation.progress < 1
						? title.opacity + Gdx.graphics.getDeltaTime()
						: (operation.progress < (function.options.duration - 1)
							? (Math.min(1, title.opacity + Gdx.graphics.getDeltaTime()))
							: title.opacity - Gdx.graphics.getDeltaTime()));
					if(title.opacity <= 0) operation.isFinished = true;
					break;
			}
		});
		
		stack = stack.stream().filter(operation -> {
			return (operation.progress < operation.function.options.duration ||
				(operation.function.isLong ? !operation.isFinished : false));
		}).collect(Collectors.toList());
	}
	
	@Override
	public void setupStage(Stage stage) {
		fade = (FadeWidget) new FadeWidget(0).addTo(stage);
		title = (TextWidget) new TextWidget("title.ttf").setOpacity(0)
			.toPosition(new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 50))
			.addTo(stage);
	}
	
	public class StackOperation {
		public float progress = 0;
		public boolean isFinished = false;
		public GamemodeFunction function;
		
		public StackOperation(GamemodeFunction function) {
			this.function = function;
		}
	}
}