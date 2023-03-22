package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.widgets.StatBarWidget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.ActionButton;
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
		debugValues = (DebugValuesWidget) new DebugValuesWidget().connectToEntity(entity);
		
		for(int i = 0; i < 2; i++) {
			final int a = i;
			new ActionButton(i == 0 ? "ui/overlay/attack.png" : "ui/overlay/dash.png")
				.toPosition(
					Gdx.graphics.getWidth() - ActionButton.size - game.settings.screenInset - 25,
					ActionButton.size * i + 50 * i + game.settings.screenInset + 25)
				.onClick(() -> {
					if(a == 0) entity.attack(Vector2.Zero);
					if(a == 1) entity.dash();
				}).addTo(stage);
		}
		
		joystick = (JoystickWidget) new JoystickWidget().connectToEntity(entity);
		stage.addActor(joystick);
		
		for(int i = 0; i < 2; i++) {
			if(game.settings.enableEditor) break;
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
				if (willZoomTo < 0.4f || willZoomTo > (GameHolder.getInstance().settings.enableEditor ? 10 : 1.2f)) return;
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