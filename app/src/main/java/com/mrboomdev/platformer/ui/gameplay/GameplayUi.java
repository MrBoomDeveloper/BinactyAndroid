package com.mrboomdev.platformer.ui.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.editor.EditorManager;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.ui.gameplay.screens.EditorScreen;
import com.mrboomdev.platformer.ui.gameplay.widgets.ButtonWidget;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.squareup.moshi.Moshi;

public class GameplayUi {
	public EditorScreen editor;
	public ObjectMap<String, ActorUtil> widgets = new ObjectMap<>();
	private GameHolder game = GameHolder.getInstance();
	private CharacterEntity connectedEntity;
	
	public void createFreeRoam(Stage stage) {
		widgets.put("debug", new DebugValuesWidget().toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - game.settings.screenInset - 75));
		if(game.settings.debugValues) stage.addActor(widgets.get("debug"));
		widgets.put("joystick", new JoystickWidget().addTo(stage));
	}
	
	public void createCombat(Stage stage) {
		new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 17, 33, 14, 14))
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size - game.settings.screenInset - 25,
				ActionButton.size + game.settings.screenInset + 25)
			.onClick(() -> connectedEntity.attack(Vector2.Zero))
			.addTo(stage);
		
		new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 17, 14, 14))
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size - game.settings.screenInset - 25,
				ActionButton.size * 2 + 50 + game.settings.screenInset + 25)
			.onClick(() -> connectedEntity.dash())
			.addTo(stage);
		
		widgets.put("use", new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 49, 14, 14))
			.onClick(() -> connectedEntity.interact())
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size * 2 - game.settings.screenInset - 75,
				ActionButton.size + game.settings.screenInset - 25)
			.addTo(stage));
	}
	
	public void createEditor(Stage stage) {
		editor = new EditorScreen();
		editor.create(stage);
	}
	
	public void createOverlay(Stage stage) {
		
	}
	
	public void connectCharacter(CharacterEntity character) {
		this.connectedEntity = character;
		for(var widget : widgets.values()) {
			widget.connectToEntity(character);
		}
	}
}