package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.mrboomdev.platformer.entity.Entity;
import java.text.SimpleDateFormat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.TextWidget;

public class GameplayUi implements CoreUi {
	private float time = 360000;
	private TextWidget timer;
	private GameplayScreen gameplay;
	private float cameraZoom;
	public Stage stage;
	public JoystickWidget joystick;
	public DebugValuesWidget debugValues;
	
	public GameplayUi(GameplayScreen screen, Entity entity) {
		this.gameplay = screen;
		stage = new Stage();
		debugValues = (DebugValuesWidget) new DebugValuesWidget()
			.toPosition(new Vector2(25, Gdx.graphics.getHeight() - 150))
			.connectToEntity(entity).addTo(stage);
		
		for(int i = 0; i < 2; i++) {
			final int a = i;
			new ActionButton(a == 0 ? "ui/overlay/attack.png" : "ui/overlay/dash.png")
				.toPosition(new Vector2(
					Gdx.graphics.getWidth() - ActionButton.size - 100,
					a * ActionButton.size * 1.4f + 150))
				.onClick(() -> {
					if(a == 0) entity.attack();
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
					if(a == 0) entity.shield();
					if(a == 1) entity.shoot();
				})
				.addTo(stage);
		}
		
		joystick = (JoystickWidget) new JoystickWidget().connectToEntity(entity);
		stage.addActor(joystick);
		
		timer = new TextWidget("font/gilroyBold.ttf");
		timer.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 75);
		stage.addActor(timer);
		
		cameraZoom = gameplay.camera.zoom;
		stage.addListener(new ActorGestureListener() {
			@Override
			public void zoom(InputEvent event, float from, float to) {
				float willZoomTo = cameraZoom + ((from - to) / 1000);
				debugValues.setValue("Screen Zoom Next", String.valueOf(willZoomTo));
				debugValues.setValue("Screen Zoom Current", String.valueOf(cameraZoom));
				if (willZoomTo < 0.2f || willZoomTo > 1.5f) return;
				gameplay.camera.zoom = willZoomTo;
			}
				
			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				cameraZoom = gameplay.camera.zoom;
			}
		});
	// stage.setDebugAll(true);
	}
	
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
		
		time -= delta * 1000;
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
		timer.setText(dateFormat.format((int) time));
		debugValues.setValue("Session Time Left", String.valueOf(time));
		
		if(time <= 0) {
			for(Entity entity : gameplay.entities.getAll()) {
				entity.die();
			}
		}
	}
	
	public void dispose() {
		stage.dispose();
	}
}