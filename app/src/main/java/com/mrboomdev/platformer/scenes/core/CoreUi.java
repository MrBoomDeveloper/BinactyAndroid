package com.mrboomdev.platformer.scenes.core;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public abstract class CoreUi {
	private final Array<UiDrawer> drawers = new Array<>();
	public Stage stage;
    
    public void render(float delta) {
		for(UiDrawer drawer : drawers) {
			drawer.drawUi();
		}
	}
	
	public void attachLayerDrawer(UiDrawer drawer) {
		drawers.add(drawer);
		drawer.setupStage(stage);
	}
    
    public abstract void dispose();
	
	public interface UiDrawer {
		void setupStage(Stage stage);
		void drawUi();
	}
}