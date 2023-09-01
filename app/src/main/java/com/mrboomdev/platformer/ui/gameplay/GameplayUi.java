package com.mrboomdev.platformer.ui.gameplay;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.gameplay.layout.InventoryLayout;
import com.mrboomdev.platformer.ui.gameplay.screens.EditorScreen;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.mrboomdev.platformer.widgets.StatBarWidget;

public class GameplayUi {
	public EditorScreen editor;
	public ObjectMap<String, ActorUtil> widgets = new ObjectMap<>();
	private final GameHolder game = GameHolder.getInstance();
	private CharacterEntity connectedEntity;
	
	public void createFreeRoam(Stage stage) {
		widgets.put("debug", new DebugValuesWidget().toPosition(
				game.settings.screenInset,
				Gdx.graphics.getHeight() - game.settings.screenInset - 75
		));

		if(game.settings.debugValues || BuildConfig.DEBUG) stage.addActor(widgets.get("debug"));

		var joystick = new JoystickWidget();
		widgets.put("joystick", joystick);

		joystick.onUpdate(power -> {
				if(!game.settings.isControlsEnabled || !joystick.isVisible) {
					connectedEntity.usePower(Vector2.Zero, 0);
					return;
				}

				connectedEntity.usePower(power, connectedEntity.stats.speed *
						(game.settings.enableEditor ? (game.environment.camera.zoom * 8) : 1));

				getWidget("debug", DebugValuesWidget.class).setValue("Movement Joystick Power", power.toString());
			})
			.toPosition(game.settings.screenInset, game.settings.screenInset)
			.toSize(250, 250)
			.addTo(stage);
	}
	
	public void createCombat(Stage stage) {
		var dash = new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 17, 14, 14));
		widgets.put("dash", dash);

		dash.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size - game.settings.screenInset - 25,
				ActionButton.size + 150 + game.settings.screenInset)
			.onClick(() -> {
				if(!game.settings.isControlsEnabled || !dash.isVisible) return;
				connectedEntity.dash();
			})
			.addTo(stage);

		var use = new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 49, 14, 14));
		widgets.put("use", use);

		use.setActive(false)
			.onClick(() -> {
				if(!game.settings.isControlsEnabled || !use.isVisible) return;
				connectedEntity.interact();
			})
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size * 2 - game.settings.screenInset - 155,
				game.settings.screenInset)
			.addTo(stage);

		widgets.put("pause", new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 33, 17, 14, 14))
			.toPosition(
				Gdx.graphics.getWidth() - ActionButton.size - game.settings.screenInset,
				Gdx.graphics.getHeight() - ActionButton.size - game.settings.screenInset)
			.onClick(() -> game.launcher.pause())
			.addTo(stage));
		
		widgets.put("inventory", new InventoryLayout()
			.toPosition(Gdx.graphics.getWidth() / 2f, game.settings.screenInset)
			.addTo(stage));

		for(int i = 0; i < 2; i++) {
			if(game.settings.enableEditor) break;
			widgets.put("stats_" + (i == 0 ? "health" : "stamina"), new StatBarWidget(i == 0 ? StatBarWidget.Track.HEALTH : StatBarWidget.Track.STAMINA)
					.toPosition(
							game.settings.screenInset,
							Gdx.graphics.getHeight() - game.settings.screenInset - (i == 0 ? StatBarWidget.SIZE : StatBarWidget.SIZE * 2.5f))
					.addTo(stage));
		}
		
		var aimJoystick = new JoystickWidget();
		widgets.put("aim", aimJoystick);

		aimJoystick.onUpdate(power -> {
				if(connectedEntity == null) return;

				boolean isActive = game.settings.isControlsEnabled && aimJoystick.isVisible;
				var item = connectedEntity.inventory.getCurrentItem();

				if(isActive) {
					CameraUtil.setCameraOffset(power.x / 25, power.y / 25);
					connectedEntity.setIsAiming(aimJoystick.isActive);
				} else {
					CameraUtil.setCameraOffset(0, 0);
					connectedEntity.setIsAiming(false);
				}

				if(item != null && isActive) item.setPower(power);

				var aimSprite = connectedEntity.aimSprite;
				if(aimJoystick.isActive && isActive) {
					var position = connectedEntity.getPosition().cpy().add(power.cpy().scl(.2f));

					aimSprite.setAlpha(.5f);
					aimSprite.setCenter(position.x, position.y);

					connectedEntity.wasPower = power.cpy();
				} else {
					aimSprite.setAlpha(0);
				}
			})
			.onUse(power -> {
				if(!game.settings.isControlsEnabled || !aimJoystick.isVisible) return;

				connectedEntity.attack(power.scl(.2f));
			})
			.toPosition(Gdx.graphics.getWidth() - 225 - game.settings.screenInset, game.settings.screenInset)
			.toSize(225, 225)
			.addTo(stage);
	}
	
	public void createEditor(Stage stage) {
		editor = new EditorScreen();
		editor.create(stage);
	}

	public void draw(SpriteBatch batch) {
		if(editor != null) editor.draw(batch);
	}
	
	public <T extends ActorUtil> T getWidget(String name, @NonNull Class<T> type) {
		return type.cast(widgets.get(name));
	}
	
	public void connectCharacter(CharacterEntity character) {
		this.connectedEntity = character;
		for(var widget : widgets.values()) {
			widget.connectToEntity(character);
		}
	}
}