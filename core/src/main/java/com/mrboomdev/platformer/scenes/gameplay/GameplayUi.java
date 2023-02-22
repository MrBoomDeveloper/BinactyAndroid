package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.widgets.StatBarWidget;
import java.text.SimpleDateFormat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.TextWidget;
import com.mrboomdev.platformer.widgets.StatBarWidget.Track;

public class GameplayUi implements CoreUi {
	private float time = 360000;
	private TextWidget timer;
	private GameplayScreen gameplay;
	private JoystickWidget joystick;
	private float cameraZoom;
	public Stage stage;
	public DebugValuesWidget debugValues;
	
	public GameplayUi(GameplayScreen screen, CharacterEntity entity) {
		this.gameplay = screen;
		stage = new Stage();
		
		debugValues = (DebugValuesWidget) new DebugValuesWidget()
			.toPosition(new Vector2(
				MainGame.settings.screenInset, 
				Gdx.graphics.getHeight() - 150 - MainGame.settings.screenInset))
			.connectToEntity(entity);
		if(MainGame.settings.debugValues) stage.addActor(debugValues);
		
		for(int i = 0; i < 2; i++) {
			final int a = i;
			new ActionButton(a == 0 ? "ui/overlay/attack.png" : "ui/overlay/dash.png")
				.toPosition(new Vector2(
					Gdx.graphics.getWidth() - ActionButton.size - 100,
					a * ActionButton.size * 1.4f + 150))
				.onClick(() -> {
					if(a == 0) entity.attack(Vector2.Zero);
					if(a == 1) entity.dash();
				})
				.addTo(stage);
		}
		
		for(int i = 0; i < 2; i++) {
			final int a = i;
			new ActionButton(a == 0 ? "ui/overlay/shield.png" : "ui/overlay/shoot.png")
				.toPosition(new Vector2(
					Gdx.graphics.getWidth() - (ActionButton.size * 2) - 150,
					a * ActionButton.size * 1.4f + 100))
				.onClick(() -> {
					//if(a == 0) entity.shield();
					if(a == 1) entity.shoot(Vector2.Zero);
				})
				.addTo(stage);
		}
		
		joystick = (JoystickWidget) new JoystickWidget().connectToEntity(entity);
		stage.addActor(joystick);
		
		for(int i = 0; i < 2; i++) {
			new StatBarWidget(i == 0 ? Track.HEALTH : Track.STAMINA)
				.toPosition(new Vector2(MainGame.settings.screenInset,
					Gdx.graphics.getHeight() - MainGame.settings.screenInset
						- (i == 0 ? StatBarWidget.SIZE : StatBarWidget.SIZE * 2.2f)))
				.connectToEntity(entity)
				.addTo(stage);
		}
		
		timer = new TextWidget("timer.ttf");
		timer.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - MainGame.settings.screenInset);
		stage.addActor(timer);
		
		cameraZoom = gameplay.camera.zoom;
		stage.addListener(new ActorGestureListener() {
			@Override
			public void zoom(InputEvent event, float from, float to) {
				if(joystick.isActive) return;
				float willZoomTo = cameraZoom + ((from - to) / 1000);
				debugValues.setValue("Screen Zoom Next", String.valueOf(willZoomTo));
				debugValues.setValue("Screen Zoom Current", String.valueOf(cameraZoom));
				if (willZoomTo < 0.2f || willZoomTo > 1.5f) return;
				gameplay.camera.zoom = willZoomTo;
			}
				
			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				if(joystick.isActive) return;
				cameraZoom = gameplay.camera.zoom;
			}
		});
		
		stage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(MainGame.settings.enableEditor) {
					Vector3 gamePosition = gameplay.camera.unproject(new Vector3(x, Gdx.graphics.getHeight() - y, 0));
					float[] roundPosition = {Math.round(gamePosition.x), Math.round(gamePosition.y), 0};
					screen.environment.map.addTile("wall", roundPosition, 1);
				}
			}
		});
		
		if(MainGame.settings.debugStage) stage.setDebugAll(true);
	}
	
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
		
		time -= delta * 1000;
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
		timer.setText(dateFormat.format((int) time));
		
		if(time <= 0) for(CharacterEntity entity : gameplay.entities.getAllCharacters()) {
			entity.die();
		}
	}
	
	public void dispose() {
		stage.dispose();
	}
}