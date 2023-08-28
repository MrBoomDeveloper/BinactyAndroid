package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.widgets.JoystickWidget;

public class GameplayUi extends CoreUi {
	private float cameraZoom;
	public GameHolder game = GameHolder.getInstance();
	
	public GameplayUi(GameplayScreen screen, CharacterEntity entity) {
		stage = new Stage();
		
		cameraZoom = game.environment.camera.zoom;

		stage.addListener(new ActorGestureListener() {
			@Override
			public void zoom(InputEvent event, float from, float to) {
				try {
					if(((JoystickWidget)game.environment.ui.widgets.get("joystick")).isActive) return;
					float willZoomTo = cameraZoom + ((from - to) / 1000);

					boolean isZoomLimitExceed = (willZoomTo < 0.4f || willZoomTo > (game.settings.enableEditor ? 10 : 1.2f));
					if(isZoomLimitExceed && !game.settings.debugCamera) return;

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