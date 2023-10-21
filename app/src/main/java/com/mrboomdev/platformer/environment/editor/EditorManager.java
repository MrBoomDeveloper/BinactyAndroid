package com.mrboomdev.platformer.environment.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.environment.editor.widgets.ButtonWidget;
import com.mrboomdev.platformer.environment.editor.widgets.GridWidget;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.CameraUtil;

import java.util.HashMap;

public class EditorManager implements CoreUi.UiDrawer {
	public static String current = "SELECT";
	public static int layer = 1;

    @Override
    public void setupStage(Stage stage) {
		var game = GameHolder.getInstance();
		
		var tileSetter = new ActorUtil(){}.toSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).addTo(stage);
		tileSetter.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Vector3 gamePosition = CameraUtil.camera.unproject(new Vector3(x, Gdx.graphics.getHeight() - y, 0));
				float[] roundPosition = {Math.round(gamePosition.x), Math.round(gamePosition.y), 0};
				game.environment.map.addTile(EditorManager.current, roundPosition, EditorManager.layer);
			}
		});
		tileSetter.setZIndex(0);
		
		var tilesGrid = (GridWidget)new GridWidget(15, true)
			.setBackground(0, 0, 0, .2f)
			.setScrollable(true)
			.setCount(2, 999)
			.toSize(200, Gdx.graphics.getHeight() - 8)
			.toPosition(Gdx.graphics.getWidth() - 208, 8)
			.addTo(stage);
		
		for(HashMap.Entry<String, MapTile> tile : game.environment.map.tilesPresets.entrySet()) {
			var tileSprite = tile.getValue().sprite != null
				? tile.getValue().sprite
				: tile.getValue().devSprite;
			
			tilesGrid.add(new ButtonWidget(tileSprite, tile.getKey())
				.onClick(() -> {
					current = tile.getKey();
					game.environment.ui.editor.selectTile(null);
				})
				.connectToScroller(tilesGrid)
				.addTo(stage));
		}
	}

    @Override
    public void drawUi() {}
}