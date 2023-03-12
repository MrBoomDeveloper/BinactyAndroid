package com.mrboomdev.platformer.ui.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;

public class GameplayUi {
	private ObjectMap<String, ActorUtil> widgets = new ObjectMap<>();
	private GameHolder game;
	
	public GameplayUi() {
		game = GameHolder.getInstance();
	}
	
	public void createFreeRoam(Stage stage) {
		widgets.put("debug", new DebugValuesWidget().toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - game.settings.screenInset - 150));
		if(game.settings.debugValues) stage.addActor(widgets.get("debug"));
	}
	
	public void createCombat(Stage stage) {
		
	}
	
	public void createEditor(Stage stage) {
		
	}
	
	public void connectCharacter(CharacterEntity character) {
		for(var widget : widgets.values()) {
			widget.connectToEntity(character);
		}
	}
}