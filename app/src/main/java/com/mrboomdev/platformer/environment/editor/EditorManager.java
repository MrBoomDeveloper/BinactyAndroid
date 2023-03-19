package com.mrboomdev.platformer.environment.editor;

import com.badlogic.gdx.Gdx;
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
import com.mrboomdev.platformer.ui.gameplay.widgets.ButtonWidget.NewButtonWidget;
import com.mrboomdev.platformer.util.ActorUtil;
import java.util.HashMap;

public class EditorManager implements CoreUi.UiDrawer {
    private UiState editorVisibility = UiState.COLLAPSED;
	public static String current = "wall";
	public static int layer = 1;

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
					game.environment.map.addTile(EditorManager.current, roundPosition, EditorManager.layer);
				}
			});
		tileSetter.setZIndex(0);
		
		var tilesGrid = (GridWidget)new GridWidget(15, true)
			.setBackground(0, 0, 0, .5f)
			.setScrollable(true)
			.setCount(2, 999)
			.toSize(200, Gdx.graphics.getHeight() - (game.settings.screenInset * 2))
			.toPosition(Gdx.graphics.getWidth() - 200 - game.settings.screenInset, game.settings.screenInset)
			.addTo(stage);
		
		{
			var exitButton = (NewButtonWidget)new NewButtonWidget(NewButtonWidget.Style.BULLET)
				.setText("Exit", game.assets.get("bulletButton.ttf"))
				.onClick(() -> game.launcher.exit())
				.toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - 200 - game.settings.screenInset)
				.addTo(stage);
			
			var saveButton = (NewButtonWidget)new NewButtonWidget(NewButtonWidget.Style.BULLET)
				.setText("Save to Android/data", game.assets.get("bulletButton.ttf"))
				.toPosition(game.settings.screenInset + 150, Gdx.graphics.getHeight() - 200 - game.settings.screenInset)
				.onClick(() -> {
					Gson gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation()
						.create();
				
					var map = game.environment.map;
					Gdx.files.external("exportedMap.json").writeString(gson.toJson(map), false);
				})
				.addTo(stage);
		}
		
		for(int i = -1; i < 4; i++) {
			final int a = i;
			var layerButton = (NewButtonWidget)new NewButtonWidget(NewButtonWidget.Style.BULLET)
				.setText("Use layer " + i, game.assets.get("bulletButton.ttf"))
				.toPosition(game.settings.screenInset, Gdx.graphics.getHeight() / 2 - i * 75 + 50)
				.onClick(() -> layer = a)
				.addTo(stage);
		}
		
		for(HashMap.Entry<String, MapTile> tile : game.environment.map.tilesPresets.entrySet()) {
			var tileSprite = tile.getValue().sprite != null
				? tile.getValue().sprite
				: tile.getValue().devSprite;
			
			var placeTileButton = (ButtonWidget)new ButtonWidget(tileSprite)
				.onClick(() -> current = tile.getKey())
				.connectToScroller(tilesGrid)
				.addTo(stage);
			
			tilesGrid.add(placeTileButton);
			current = tile.getKey();
		}
		
		var eraserButton = (ButtonWidget)new ButtonWidget(new Sprite(new Texture(Gdx.files.internal("world/effects/boom.png"))))
			.onClick(() -> current = "ERASER")
			.connectToScroller(tilesGrid)
			.addTo(stage);
		tilesGrid.add(eraserButton);
		
		//stage.setDebugAll(true);
	}

    @Override
    public void drawUi() {}
	
	public enum UiState {
        SHOWN,
        COLLAPSED
    }
}