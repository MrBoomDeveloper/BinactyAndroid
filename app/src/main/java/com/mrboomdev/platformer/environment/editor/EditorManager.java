package com.mrboomdev.platformer.environment.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
		
		var exitButton = (ButtonWidget)new ButtonWidget("Exit")
			.addTo(stage);
		
		new GridWidget(25)
			.setBackground(0, 0, 0, .5f)
			.add(exitButton)
			.toSize(800, 200)
			.toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - 200 - game.settings.screenInset)
			.addTo(stage);
		
		new GridWidget(15)
			.setBackground(0, 0, 0, .5f)
			.setScrollable(true)
			.toSize(400, Gdx.graphics.getHeight() - (game.settings.screenInset * 2))
			.toPosition(Gdx.graphics.getWidth() - 400 - game.settings.screenInset, game.settings.screenInset)
			.addTo(stage);
	}

    @Override
    public void drawUi() {}
	
	public enum UiState {
        SHOWN,
        COLLAPSED
    }
}