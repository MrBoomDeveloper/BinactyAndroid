package com.mrboomdev.platformer.ui.gameplay.screens;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.environment.editor.EditorManager;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.ui.gameplay.widgets.ButtonWidget;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.squareup.moshi.Moshi;

public class EditorScreen {
	public MapTile selectedTile;
	private final GameHolder game = GameHolder.getInstance();
	private final ShapeRenderer shape = new ShapeRenderer();
	private final Array<ButtonWidget> layers = new Array<>();
	private ObjectMap<String, ActorUtil> widgets;
	private Stage stage;

	public void selectTile(MapTile tile) {
		if(selectedTile != null) selectedTile.isSelected = false;
		if(tile != null) tile.isSelected = true;
		selectedTile = tile;
		if(widgets == null) {
			widgets = new ObjectMap<>();
			widgets.put("DownScaleY", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("Downscale Y")
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[1] -= .1f;
					selectedTile.update();
				})
				.toPosition(732, 48)
				.addTo(stage));
			
			widgets.put("ScaleY", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("Scale Y")
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[1] += .1f;
					selectedTile.update();
				})
				.toPosition(610, 48)
				.addTo(stage));
				
			widgets.put("DownScaleX", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("Downscale X")
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[0] -= .1f;
					selectedTile.update();
				})
				.toPosition(488, 48)
				.addTo(stage));
			
			widgets.put("ScaleX", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("Scale X")
				.onClick(() -> {
					if(selectedTile.scale == null) selectedTile.scale = new float[]{1, 1};
					selectedTile.scale[0] += .1f;
					selectedTile.update();
				})
				.toPosition(364, 48)
				.addTo(stage));
			
			widgets.put("moveUp", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("More Up")
				.onClick(() -> {
					selectedTile.offset[1] += .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 4 + 140)
				.addTo(stage));
				
			widgets.put("moveDown", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Move Down")
				.onClick(() -> {
					selectedTile.offset[1] -= .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 3 + 120)
				.addTo(stage));
				
			widgets.put("moveRight", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("More right")
				.onClick(() -> {
					selectedTile.offset[0] += .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT * 2 + 100)
				.addTo(stage));
				
			widgets.put("moveLeft", new ButtonWidget(ButtonWidget.Style.BULLET)
				.setText("Move Left")
				.onClick(() -> {
					selectedTile.offset[0] -= .2f;
					selectedTile.rebuild();
				})
				.toPosition(Gdx.graphics.getWidth() - game.settings.screenInset - 425, game.settings.screenInset + ButtonWidget.BULLET_HEIGHT + 80)
				.addTo(stage));
			
			widgets.put("setId", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("Set ID")
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
				.toPosition(Gdx.graphics.getWidth() - 350, 48)
				.addTo(stage));
				
			widgets.put("toggleStyle", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("Toggle style")
				.onClick(() -> {
					var style = selectedTile.style;
					if(style == null) return;
					var types = selectedTile.style.types.keySet();
					style.currentId = style.currentId > types.size() - 2
						? 0 : style.currentId + 1;
					style.selectStyle((String)types.toArray()[style.currentId]);
				})
				.toPosition(Gdx.graphics.getWidth() - 472, 48)
				.addTo(stage));
				
			widgets.put("flipX", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("FlipX")
				.onClick(() -> {
					selectedTile.flipX = !selectedTile.flipX;
					selectedTile.update();
				})
				.toPosition(120, 48)
				.addTo(stage));
				
			widgets.put("flipY", new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("FlipY")
				.onClick(() -> {
					selectedTile.flipY = !selectedTile.flipY;
					selectedTile.update();
				})
				.toPosition(242, 48)
				.addTo(stage));
		}
		
		widgets.forEach(widget -> widget.value.setVisible(selectedTile != null));
		widgets.get("toggleStyle").setVisible(selectedTile != null && selectedTile.style != null);
	}
	
	public void create(Stage stage) {
		this.stage = stage;
	
		new ButtonWidget(ButtonWidget.Style.COMPACT)
			.setText("Exit")
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 33, 49, 14, 14))
			.onClick(() -> game.launcher.pause())
			.toPosition(120, Gdx.graphics.getHeight() - ButtonWidget.COMPACT_HEIGHT)
			.addTo(stage);
			
			new ButtonWidget(ButtonWidget.Style.COMPACT)
			.setText("Save")
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 1, 1, 14, 14))
			.toPosition(224, Gdx.graphics.getHeight() - ButtonWidget.COMPACT_HEIGHT)
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
					dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT)
							.setTextColor("#ffffff")
							.setText(LogUtil.throwableToString(e)));

					dialog.addAction(new AndroidDialog.Action().setText("Continue").setClickListener(button -> dialog.close())).addSpace(30);
					dialog.show();
				}
				
			})
			.addTo(stage);

		for(int i = -1; i < 4; i++) {
			final int a = i;
			layers.add(new ButtonWidget(ButtonWidget.Style.COMPACT)
				.setText("Use layer " + i)
				.toPosition(0, Gdx.graphics.getHeight() - 47 * (i + 3))
				.onClick(() -> {
					EditorManager.layer = a;
					layers.forEach(button -> button.setHighlight(false));
					layers.get(a + 1).setHighlight(true);
				})
				.addTo(stage));
		}
		layers.get(2).setHighlight(true);
		
		new ButtonWidget(ButtonWidget.Style.COMPACT)
			.setText("Global Lightning")
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 17, 49, 14, 14))
			.toPosition(120, 0)
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
		
		new ButtonWidget(ButtonWidget.Style.COMPACT)
			.setText("Eraser")
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 49, 1, 14, 14))
			.onClick(() -> {
				EditorManager.current = "ERASER";
				selectTile(null);
			})
			.toPosition(Gdx.graphics.getWidth() - 475, Gdx.graphics.getHeight() - ButtonWidget.COMPACT_HEIGHT)
			.addTo(stage);
			
		new ButtonWidget(ButtonWidget.Style.COMPACT)
			.setText("Select")
			.setForegroundImage(new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 49, 33, 14, 14))
			.onClick(() -> {
				EditorManager.current = "SELECT";
				selectTile(null);
			})
			.toPosition(Gdx.graphics.getWidth() - 350, Gdx.graphics.getHeight() - ButtonWidget.COMPACT_HEIGHT)
			.addTo(stage);
	}

	public void draw(@NonNull SpriteBatch batch) {
		batch.end();
		shape.begin(ShapeRenderer.ShapeType.Filled); {
			shape.setColor(.1f, .1f, .1f, 1);
			shape.rect(0, 0, Gdx.graphics.getWidth(), 45);
			if(selectedTile != null) shape.rect(0, 45, Gdx.graphics.getWidth(), 48);
			shape.rect(0, Gdx.graphics.getHeight() - 45, Gdx.graphics.getWidth(), 45);

			shape.setColor(.15f, .2f, .2f, 1);
			Gdx.gl.glLineWidth(1);
			if(selectedTile != null) shape.line(0, 46.5f, Gdx.graphics.getWidth(), 46.5f);

			shape.setColor(.1f, .1f, .1f, 1);
			shape.rect(0, 0, 120, Gdx.graphics.getHeight());
			shape.rect(Gdx.graphics.getWidth() - 216, 0, 216, Gdx.graphics.getHeight());
		} shape.end();
		batch.begin();
	}
}