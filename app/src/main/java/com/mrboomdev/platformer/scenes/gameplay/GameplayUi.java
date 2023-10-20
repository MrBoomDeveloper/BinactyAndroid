package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.widgets.JoystickWidget;

public class GameplayUi extends CoreUi {
	private float cameraZoom;
	public GameHolder game = GameHolder.getInstance();
	
	public GameplayUi() {
		stage = new Stage();
		
		cameraZoom = game.environment.camera.zoom;

		stage.addListener(new ActorGestureListener() {
			private boolean isJoystickActive() {
				try {
					var widget = game.environment.ui.widgets.get("joystick");
					return ((JoystickWidget)widget).isActive;
				} catch(NullPointerException e) {
					e.printStackTrace();
					return true;
				}
			}

			@Override
			public void zoom(InputEvent event, float from, float to) {
				if(isJoystickActive()) return;
				float willZoomTo = cameraZoom + ((from - to) / 1000);

				boolean isZoomLimitExceed = (willZoomTo < 0.3f || willZoomTo > (game.settings.enableEditor ? 10 : 1));
				if(isZoomLimitExceed && !game.settings.debugCamera) return;

				game.environment.camera.zoom = willZoomTo;
				CameraUtil.isZoomedForce = true;
			}
				
			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(isJoystickActive()) return;
				cameraZoom = game.environment.camera.zoom;
			}
		});
	}
	
	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.setDebugAll(game.settings.debugStage);
		stage.draw();

		super.render(delta);

		game.environment.ui.draw(stage.getBatch());
	}
	
	public void dispose() {
		stage.dispose();
	}
}