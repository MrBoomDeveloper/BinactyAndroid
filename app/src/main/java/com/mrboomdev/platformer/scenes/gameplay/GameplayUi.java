package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.mrboomdev.platformer.widgets.StatBarWidget;
import com.mrboomdev.platformer.widgets.StatBarWidget.Track;

public class GameplayUi extends CoreUi {
	private float cameraZoom;
	public GameHolder game = GameHolder.getInstance();
	
	public GameplayUi(GameplayScreen screen, CharacterEntity entity) {
		stage = new Stage();
		
		for(int i = 0; i < 2; i++) {
			if(game.settings.enableEditor) break;
			new StatBarWidget(i == 0 ? Track.HEALTH : Track.STAMINA)
				.toPosition(
						game.settings.screenInset,
						Gdx.graphics.getHeight() - game.settings.screenInset - (i == 0 ? StatBarWidget.SIZE : StatBarWidget.SIZE * 2.5f))
				.connectToEntity(entity)
				.addTo(stage);
		}
		
		cameraZoom = game.environment.camera.zoom;
		stage.addListener(new ActorGestureListener() {
			@Override
			public void zoom(InputEvent event, float from, float to) {
				try {
					if(((JoystickWidget)game.environment.ui.widgets.get("joystick")).isActive) return;
					float willZoomTo = cameraZoom + ((from - to) / 1000);
					if(willZoomTo < 0.4f || willZoomTo > (game.settings.enableEditor ? 10 : 1.2f)) return;
					game.environment.camera.zoom = willZoomTo;
					CameraUtil.isZoomedForce = true;
				} catch(NullPointerException e) {
					e.printStackTrace();
				}
			}
				
			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
				try {
					if(((JoystickWidget)game.environment.ui.widgets.get("joystick")).isActive) return;
					cameraZoom = game.environment.camera.zoom;
				} catch(NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.setDebugAll(game.settings.debugStage);
		stage.draw();
		super.render(delta);
	}
	
	public void dispose() {
		stage.dispose();
	}
}