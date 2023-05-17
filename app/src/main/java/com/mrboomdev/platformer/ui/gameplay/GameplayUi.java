package com.mrboomdev.platformer.ui.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.gameplay.layout.InventoryLayout;
import com.mrboomdev.platformer.ui.gameplay.screens.EditorScreen;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.JoystickWidget;

public class GameplayUi {
	public EditorScreen editor;
	public ObjectMap<String, ActorUtil> widgets = new ObjectMap<>();
	private GameHolder game = GameHolder.getInstance();
	private CharacterEntity connectedEntity;
	
	public void createFreeRoam(Stage stage) {
		widgets.put("debug", new DebugValuesWidget().toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - game.settings.screenInset - 75));
		if(game.settings.debugValues) stage.addActor(widgets.get("debug"));
		
		widgets.put("joystick", new JoystickWidget()
			.onUpdate(power -> {
				connectedEntity.usePower(power, connectedEntity.stats.speed * (game.settings.enableEditor ? game.environment.camera.zoom * 3 : 1), false);
				getWidget("debug", DebugValuesWidget.class).setValue("Movement Joystick Power", power.toString());
			})
			.toPosition(game.settings.screenInset, game.settings.screenInset)
			.toSize(250, 250)
			.addTo(stage));
	}
	
	public void createCombat(Stage stage) {
		new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 17, 14, 14))
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size - game.settings.screenInset - 25,
				ActionButton.size + 150 + game.settings.screenInset)
			.onClick(() -> connectedEntity.dash())
			.addTo(stage);
		
		widgets.put("use", new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 49, 14, 14))
			.setActive(false)
			.onClick(() -> connectedEntity.interact())
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size * 2 - game.settings.screenInset - 155,
				game.settings.screenInset)
			.addTo(stage));
			
		new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 33, 17, 14, 14))
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size - game.settings.screenInset,
				Gdx.graphics.getHeight() - ActionButton.size - game.settings.screenInset)
			.onClick(() -> {
				game.launcher.pause();
			})
			.addTo(stage);
		
		widgets.put("inventory", new InventoryLayout()
			.toPosition(Gdx.graphics.getWidth() / 2, game.settings.screenInset)
			.addTo(stage));
		
		new JoystickWidget()
			.onUpdate(power -> {
				//TODO: Develop aiming for punches and shooting
			})
			.onUse(power -> connectedEntity.attack(power))
			.toPosition(Gdx.graphics.getWidth() - 225 - game.settings.screenInset, game.settings.screenInset)
			.toSize(225, 225)
			.addTo(stage);
	}
	
	public void createEditor(Stage stage) {
		editor = new EditorScreen();
		editor.create(stage);
	}
	
	public void createOverlay(Stage stage) {
		
	}
	
	public <T extends ActorUtil> T getWidget(String name, Class<T> type) {
		return type.cast(widgets.get(name));
	}
	
	public void connectCharacter(CharacterEntity character) {
		this.connectedEntity = character;
		for(var widget : widgets.values()) {
			widget.connectToEntity(character);
		}
	}
}