package com.mrboomdev.platformer.environment.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrboomdev.platformer.environment.editor.widgets.ButtonWidget;
import com.mrboomdev.platformer.environment.editor.widgets.GridWidget;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.util.ActorUtil;
import java.util.HashMap;

public class EditorManager implements CoreUi.UiDrawer {
    private UiState editorVisibility = UiState.COLLAPSED;
	public static String current = "wall";

    @Override
    public void setupStage(Stage stage) {
		var game = GameHolder.getInstance();
		game.settings.mainPlayer.gainDamage(-99999);
		
		var tileSetter = new ActorUtil(){}.toSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).addTo(stage);
		tileSetter.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Vector3 gamePosition = game.environment.camera.unproject(new Vector3(x, Gdx.graphics.getHeight() - y, 0));
					float[] roundPosition = {Math.round(gamePosition.x), Math.round(gamePosition.y), 0};
					game.environment.map.addTile(EditorManager.current, roundPosition, 1);
				}
			});
		tileSetter.setZIndex(0);
		
		var actionsGrid = (GridWidget)new GridWidget(25, true)
			.setBackground(0, 0, 0, .5f)
			.setCount(999, 1)
			.toSize(800, 200)
			.toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - 200 - game.settings.screenInset)
			.addTo(stage);
		
		var tilesGrid = (GridWidget)new GridWidget(15, true)
			.setBackground(0, 0, 0, .5f)
			.setScrollable(true)
			.setCount(2, 999)
			.toSize(400, Gdx.graphics.getHeight() - (game.settings.screenInset * 2))
			.toPosition(Gdx.graphics.getWidth() - 400 - game.settings.screenInset, game.settings.screenInset)
			.addTo(stage);
		
		var exitButton = (ButtonWidget)new ButtonWidget("Exit")
			.onClick(() -> game.launcher.exit())
			.addTo(stage);
		
		var saveButton = (ButtonWidget)new ButtonWidget("Save")
			.onClick(() -> {
				Gson gson = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation()
					.create();
				
				var map = game.environment.map;
				Gdx.files.external("test.json").writeString(gson.toJson(map), false);
			})
			.addTo(stage);
		
		actionsGrid.add(exitButton, saveButton);
		
		for(HashMap.Entry<String, MapTile> tile : game.environment.map.tilesPresets.entrySet()) {
			var placeTileButton = (ButtonWidget)new ButtonWidget(tile.getValue().sprite)
				.onClick(() -> current = tile.getKey())
				.connectToScroller(tilesGrid)
				.addTo(stage);
			
			tilesGrid.add(placeTileButton);
			current = tile.getKey();
		}
		
		var eraserButton = (ButtonWidget)new ButtonWidget(new Sprite(new Texture(Gdx.files.internal("effects/boom.png"))))
			.onClick(() -> current = "ERASER")
			.connectToScroller(tilesGrid)
			.addTo(stage);
		tilesGrid.add(eraserButton);
	}

    @Override
    public void drawUi() {}
	
	public enum UiState {
        SHOWN,
        COLLAPSED
    }
}