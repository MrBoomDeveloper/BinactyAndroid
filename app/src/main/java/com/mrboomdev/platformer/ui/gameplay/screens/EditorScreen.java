package com.mrboomdev.platformer.ui.gameplay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Moshi;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.environment.editor.EditorManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.gameplay.widgets.ButtonWidget;

public class EditorScreen {
	public MapTile selectedTile;
	private final GameHolder game = GameHolder.getInstance();
	private ObjectMap<String, ActorUtil> widgets;
	private Stage stage;

	public void selectTile(MapTile tile) {
		if(selectedTile != null) selectedTile.isSelected = false;
		if(tile != null) tile.isSelected = true;
		selectedTile = tile;
		if(widgets == null) {
			widgets = new ObjectMap<>();
			widgets.put("DownScaleY", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Downscale Y", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[1] -= .1f;
					selectedTile.update();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 11 + 220)
				.addTo(stage));
			
			widgets.put("ScaleY", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Scale Y", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[1] += .1f;
					selectedTile.update();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 10 + 200)
				.addTo(stage));
				
			widgets.put("DownScaleX", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Downscale X", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[0] -= .1f;
					selectedTile.update();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 9 + 180)
				.addTo(stage));
			
			widgets.put("ScaleX", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Scale X", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[0] += .1f;
					selectedTile.update();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 8 + 160)
				.addTo(stage));
			
			widgets.put("moveUp", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("More Up", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					selectedTile.offset[1] += .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 7 + 140)
				.addTo(stage));
				
			widgets.put("moveDown", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Move Down", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					selectedTile.offset[1] -= .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 6 + 120)
				.addTo(stage));
				
			widgets.put("moveRight", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("More right", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					selectedTile.offset[0] += .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 5 + 100)
				.addTo(stage));
				
			widgets.put("moveLeft", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Move Left", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					selectedTile.offset[0] -= .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 4 + 80)
				.addTo(stage));
			
			widgets.put("setId", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Set ID", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					var dialog = new AndroidDialog().setTitle("Set unique tile ID");
					
					dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#dddddd").setText("Only this single one tile should to have this id."));
					var idField = new AndroidDialog.Field(AndroidDialog.FieldType.EDIT_TEXT).setText(selectedTile.id);
					dialog.addSpace(15).addField(idField).addSpace(30);
					
					dialog.addAction(new AndroidDialog.Action().setText("Cancel").setClickListener(button -> dialog.close()));
					dialog.addAction(new AndroidDialog.Action().setText("Save").setClickListener(button -> {
						selectedTile.id = idField.getText();
						dialog.close();
					}));
					dialog.show();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 3 + 60)
				.addTo(stage));
				
			widgets.put("toggleStyle", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Toggle style", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					var style = selectedTile.style;
					if(style == null) return;
					var types = selectedTile.style.types.keySet();
					style.currentId = style.currentId > types.size() - 2
						? 0 : style.currentId + 1;
					style.selectStyle((String)types.toArray()[style.currentId]);
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 2 + 40)
				.addTo(stage));
				
			widgets.put("flipX", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("FlipX", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					selectedTile.flipX = !selectedTile.flipX;
					selectedTile.update();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT + 20)
				.addTo(stage));
				
			widgets.put("flipY", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("FlipY", game.assets.get("bulletButton.ttf"))
				.onClick(() -> {
					selectedTile.flipY = !selectedTile.flipY;
					selectedTile.update();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset)
				.addTo(stage));
		}
		
		widgets.forEach(widget -> widget.value.setVisible(selectedTile != null));
	}
	
	public void create(Stage stage) {
		this.stage = stage;
	
		new ButtonWidget(ButtonWidget.Style.BULLET)
			.setText("Exit", game.assets.get("bulletButton.ttf"))
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 33, 49, 14, 14))
			.onClick(() -> game.launcher.exit(GameLauncher.Status.LOBBY))
			.toPosition(game.settings.screenInset, Gdx.graphics.getHeight() - ButtonWidget.BULLET_HEIGHT - game.settings.screenInset)
			.addTo(stage);
			
		new ButtonWidget(ButtonWidget.Style.BULLET)
			.setText("Save", game.assets.get("bulletButton.ttf"))
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 1, 14, 14))
			.toPosition(game.settings.screenInset + 135, Gdx.graphics.getHeight() - ButtonWidget.BULLET_HEIGHT - game.settings.screenInset)
			.onClick(() -> {
				try {
					Moshi moshi = new Moshi.Builder().add(new MapTile.Adapter()).build();
					var adapter = moshi.adapter(MapManager.class);
					var map = game.environment.map;
					if(game.mapFile.source == FileUtil.Source.EXTERNAL) {
						game.mapFile.writeString(adapter.toJson(map), true);
					} else {
						Gdx.files.external("exportedMap.json").writeString(adapter.toJson(map), false);
					}

					var dialog = new AndroidDialog().setTitle("Saved successfully!");
					dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#ffffff").setText("Everything is ok, you can continue."));
					dialog.addAction(new AndroidDialog.Action().setText("Continue").setClickListener(button -> dialog.close())).addSpace(30);
					dialog.show();
				} catch(Exception e) {
					e.printStackTrace();
					var dialog = new AndroidDialog().setTitle("Error while saving the file!");
					dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#ffffff").setText(e.getMessage()));
					dialog.addAction(new AndroidDialog.Action().setText("Continue").setClickListener(button -> dialog.close())).addSpace(30);
					dialog.show();
				}
				
			})
			.addTo(stage);
			
		for(int i = -1; i < 4; i++) {
			final int a = i;
			new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Use layer " + i, game.assets.get("bulletButton.ttf"))
				.toPosition(game.settings.screenInset, (float)Gdx.graphics.getHeight() / 2 - i * 70 + 50)
				.onClick(() -> EditorManager.layer = a)
				.addTo(stage);
		}
		
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
					game.environment.entities.mainLight.setColor(playerColor.getColor());
								
					dialog.close();
				}));
				dialog.show();
			})
			.addTo(stage);
		
		new ButtonWidget(ButtonWidget.Style.BULLET)
			.setText("Eraser", game.assets.get("bulletButton.ttf"))
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 49, 1, 14, 14))
			.onClick(() -> {
				EditorManager.current = "ERASER";
				selectTile(null);
			})
			.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 400, Gdx.graphics.getHeight() - ButtonWidget.BULLET_HEIGHT - game.settings.screenInset)
			.addTo(stage);
			
		new ButtonWidget(ButtonWidget.Style.BULLET)
			.setText("Select", game.assets.get("bulletButton.ttf"))
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 49, 33, 14, 14))
			.onClick(() -> {
				EditorManager.current = "SELECT";
				selectTile(null);
			})
			.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 400, Gdx.graphics.getHeight() - ButtonWidget.BULLET_HEIGHT * 2 - 20 - game.settings.screenInset)
			.addTo(stage);
	}
}