package com.mrboomdev.platformer.environment.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrboomdev.platformer.environment.editor.widgets.ButtonWidget;
import com.mrboomdev.platformer.environment.editor.widgets.GridWidget;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreUi;

public class EditorManager implements CoreUi.UiDrawer {
    private UiState editorVisibility = UiState.COLLAPSED;

    @Override
    public void setupStage(Stage stage) {
		//TODO: Move block placer code from GameplayUi to here
		
		var game = GameHolder.getInstance();
		
		new GridWidget(25, true)
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
				Gson gson = new Gson();
				var map = game.environment.map;
				Gdx.files.external("test.json").writeString(gson.toJson(map), false);
			})
			.addTo(stage);
		
		var wallButton = (ButtonWidget)new ButtonWidget("Set Wall")
			.addTo(stage);
		
		tilesGrid.add(exitButton, saveButton, wallButton);
	}

    @Override
    public void drawUi() {}
	
	public enum UiState {
        SHOWN,
        COLLAPSED
    }
}