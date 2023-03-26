package com.mrboomdev.platformer.ui.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.ui.gameplay.widgets.ButtonWidget;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.squareup.moshi.Moshi;

public class GameplayUi {
	private ObjectMap<String, ActorUtil> widgets = new ObjectMap<>();
	private GameHolder game = GameHolder.getInstance();
	private CharacterEntity connectedEntity;
	
	public void createFreeRoam(Stage stage) {
		widgets.put("debug", new DebugValuesWidget().toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - game.settings.screenInset - 150));
		if(game.settings.debugValues) stage.addActor(widgets.get("debug"));
		
		widgets.put("use", new ActionButton(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 49, 14, 14)).addTo(stage));
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
		
		widgets.get("use")
			.onClick(() -> connectedEntity.interact())
			.setPosition(
				Gdx.graphics.getWidth() - ActionButton.size * 2 - game.settings.screenInset - 75,
				ActionButton.size + game.settings.screenInset - 25);
	}
	
	public void createEditor(Stage stage) {
		widgets.get("use").setPosition(
			Gdx.graphics.getWidth() - ActionButton.size * 2 - game.settings.screenInset - 50 - 100,
			ActionButton.size / 2 + game.settings.screenInset);
		
		widgets.get("use").onClick(() -> {
			var dialog = new AndroidDialog().setTitle("Set Tile lightning");
					
			dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#dddddd").setText("Tile color. Enter this text in the rgba like format, for example: (red 255 is 1, alpha 1 is 1)"));
			var colorField = new AndroidDialog.Field(AndroidDialog.FieldType.EDIT_TEXT).setText(game.environment.map.atmosphere.environmentLightColor.toString()).setHint("Ex. 0, 0, 0, 1");
			dialog.addSpace(15).addField(colorField).addSpace(30);
					
			dialog.addAction(new AndroidDialog.Action().setText("Cancel").setClickListener(button -> dialog.close()));
			dialog.addAction(new AndroidDialog.Action().setText("Save").setClickListener(button -> {
				var envColor = ColorUtil.parse(colorField.getText());
				game.environment.map.atmosphere.environmentLightColor = envColor;
				game.environment.rayHandler.setAmbientLight(envColor.getColor());
				dialog.close();
			}));
			dialog.show();
		});
		
		new ButtonWidget(ButtonWidget.Style.BULLET)
			.setText("Exit", game.assets.get("bulletButton.ttf"))
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 33, 49, 14, 14))
			.onClick(() -> game.launcher.exit(GameLauncher.Status.LOBBY))
			.toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - ButtonWidget.BULLET_HEIGHT - game.settings.screenInset)
			.addTo(stage);
			
		new ButtonWidget(ButtonWidget.Style.BULLET)
			.setText("Save to Android/data", game.assets.get("bulletButton.ttf"))
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 1, 14, 14))
			.toPosition(game.settings.screenInset + 150, Gdx.graphics.getHeight() - ButtonWidget.BULLET_HEIGHT - game.settings.screenInset)
			.onClick(() -> {
				Moshi moshi = new Moshi.Builder().add(new MapTile.Adapter()).build();
				var adapter = moshi.adapter(MapManager.class);
				var map = game.environment.map;
				Gdx.files.external("exportedMap.json").writeString(adapter.toJson(map), false);
			})
			.addTo(stage);
		
		new ButtonWidget(ButtonWidget.Style.BULLET)
			.setText("Set environment lightning", game.assets.get("bulletButton.ttf"))
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 17, 49, 14, 14))
			.toPosition(game.settings.screenInset, game.settings.screenInset)
			.onClick(() -> {
				var dialog = new AndroidDialog().setTitle("Set Environment lightning");
					
				dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#dddddd").setText("Environment color. Enter this text in the rgba like format, for example: (red 255 is 1, alpha 1 is 1)"));
				var colorField = new AndroidDialog.Field(AndroidDialog.FieldType.EDIT_TEXT).setText(game.environment.map.atmosphere.environmentLightColor.toString()).setHint("Ex. 0, 0, 0, 1");
				dialog.addSpace(15).addField(colorField).addSpace(30);
					
				dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#dddddd").setText("Player lightning color"));
				var playerColorField = new AndroidDialog.Field(AndroidDialog.FieldType.EDIT_TEXT).setText(game.environment.map.atmosphere.playerLightColor.toString()).setHint("Ex. 0, 0, 0, 1");
				dialog.addSpace(15).addField(playerColorField).addSpace(30);
					
				dialog.addAction(new AndroidDialog.Action().setText("Cancel").setClickListener(button -> dialog.close()));
				dialog.addAction(new AndroidDialog.Action().setText("Save").setClickListener(button -> {
					var envColor = ColorUtil.parse(colorField.getText());
					game.environment.map.atmosphere.environmentLightColor = envColor;
					game.environment.rayHandler.setAmbientLight(envColor.getColor());
								
					var playerColor = ColorUtil.parse(playerColorField.getText());
					game.environment.map.atmosphere.playerLightColor = playerColor;
					EntityManager.instance.mainLight.setColor(playerColor.getColor());
								
					dialog.close();
				}));
				dialog.show();
			})
			.addTo(stage);
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