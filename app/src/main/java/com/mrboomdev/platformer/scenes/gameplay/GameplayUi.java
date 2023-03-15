package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
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

public class GameplayUi extends CoreUi {
	private GameplayScreen gameplay;
	private JoystickWidget joystick;
	private float cameraZoom;
	public DebugValuesWidget debugValues;
	public GameHolder game = GameHolder.getInstance();
	
	public GameplayUi(GameplayScreen screen, CharacterEntity entity) {
		this.gameplay = screen;
		stage = new Stage();
		
		//TODO: REMOVE AFTER DONE REFACTOR
		debugValues = (DebugValuesWidget) new DebugValuesWidget()
			.toPosition(new Vector2(
				game.settings.screenInset,
				Gdx.graphics.getHeight() - 150 - game.settings.screenInset))
			.connectToEntity(entity);
		if(game.settings.debugValues) //stage.addActor(debugValues);
		
		for(int i = 0; i < 2; i++) {
			final int a = i;
			new ActionButton(a == 0 ? "ui/overlay/attack.png" : "ui/overlay/dash.png")
				.toPosition(new Vector2(
					Gdx.graphics.getWidth() - ActionButton.size - 100,
					a * ActionButton.size * 1.4f + 100))
				.onClick(() -> {
					if(a == 0) entity.attack(Vector2.Zero);
					if(a == 1) entity.dash();
				})
				.addTo(stage);
		}
		
		for(int i = 1; i < 2; i++) {
			final int a = i;
			new ActionButton(a == 0 ? "ui/overlay/shield.png" : "ui/overlay/shoot.png")
				.toPosition(new Vector2(
					Gdx.graphics.getWidth() - (ActionButton.size * 2) - 150,
					a * ActionButton.size * 1.4f + 50))
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
				.toPosition(new Vector2(game.settings.screenInset,
					Gdx.graphics.getHeight() - game.settings.screenInset
						- (i == 0 ? StatBarWidget.SIZE : StatBarWidget.SIZE * 2.2f)))
				.connectToEntity(entity)
				.addTo(stage);
		}
		
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
	}
	
	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
		super.render(delta);
	}
	
	public void dispose() {
		stage.dispose();
	}
}