package com.mrboomdev.platformer.environment.editor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mrboomdev.platformer.environment.editor.widgets.ButtonWidget;
import com.mrboomdev.platformer.environment.editor.widgets.ItemWidget;
import com.mrboomdev.platformer.scenes.core.CoreUi;

public class EditorManager implements CoreUi.UiDrawer {
    private UiState editorVisibility = UiState.COLLAPSED;

    @Override
    public void setupStage(Stage stage) {
		Table table = new Table();
		ScrollPane scroll = new ScrollPane(table);
		
		new ItemWidget()
			.addTo(table);
		
		new ButtonWidget("Exit")
			.addTo(stage);
		
		new ButtonWidget("Toggle Editor")
			.addTo(stage);
		
		stage.addActor(scroll);
	}

    @Override
    public void drawUi() {}
	
	public enum UiState {
        SHOWN,
        COLLAPSED
    }
}