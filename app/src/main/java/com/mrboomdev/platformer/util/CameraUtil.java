package com.mrboomdev.platformer.util;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.BotTarget;
import com.mrboomdev.platformer.game.GameHolder;

@SuppressWarnings("unused")
public class CameraUtil {
	public static boolean isZoomedForce;
	private static BotTarget target;
	private static final float DEFAULT_CAMERA_SPEED = .05f;
	private static float shakePower, shakeDuration, shakeProgress;
	private static float cameraZoomSize, cameraZoomSpeed, cameraSpeed;
	private static final Vector2 shakeCurrent = new Vector2();
	private static final Vector2 offset = new Vector2();
	private static final Vector2 cameraPosition = new Vector2();
	
	public static void addCameraShake(float power, float duration) {
		if(GameHolder.getInstance().settings.debugCamera) return;

		if(shakeProgress < shakeDuration && shakePower < .5f) {
			shakePower += power;
			shakeDuration += duration;
			return;
		}

		shakePower = power;
		shakeDuration = duration;
		shakeProgress = 0;
		shakeCurrent.setZero();
	}

	public static void setCameraOffset(float x, float y) {
		offset.set(x, y);
	}

	public static void setCameraMoveSpeed(float speed) {
		cameraSpeed = speed;
	}

	public static void setCameraZoom(float size, float speed) {
		cameraZoomSize = size;
		cameraZoomSpeed = speed;
		isZoomedForce = false;
	}

	public static void setCameraPosition(float x, float y) {
		var camera = GameHolder.getInstance().environment.camera;
		camera.position.set(x, y, 0);

		cameraPosition.set(x, y);
	}

	public static void reset() {
		isZoomedForce = false;
		shakePower = 0;
		shakeDuration = 0;
		shakeProgress = 0;
		cameraSpeed = DEFAULT_CAMERA_SPEED;

		setCameraZoom(.5f, .1f);
		offset.setZero();
		shakeCurrent.setZero();
	}

	public static void setTarget(BotTarget target) {
		if(target == null) {
			var camera = GameHolder.getInstance().environment.camera;

			if(CameraUtil.target == null) {
				cameraPosition.set(camera.position.x, camera.position.y);
			} else {
				var position = CameraUtil.target.getPosition();
				cameraPosition.set(position);
			}
		}

		CameraUtil.target = target;
	}
	
	public static void update(float delta) {
		var game = GameHolder.getInstance();
		var camera = game.environment.camera;

		if(game.settings.debugCamera) cameraSpeed = DEFAULT_CAMERA_SPEED;

		shakeProgress += delta;
		if(shakeProgress < shakeDuration && !game.settings.debugCamera) {
			shakeCurrent.set(
					(float)(Math.random() * shakePower - (shakePower / 2)),
					(float)Math.random() * shakePower - (shakePower / 2));
		} else {
			shakeCurrent.setZero();
		}

		var newPosition = target != null ? target.getPosition() : cameraPosition;

		camera.position.set(shakeCurrent.add(
				camera.position.x + ((newPosition.x + offset.x) - camera.position.x) * (cameraSpeed / camera.zoom),
				camera.position.y + ((newPosition.y + offset.y) - camera.position.y) * (cameraSpeed / camera.zoom)
		), 0);

		if(!game.settings.enableEditor && !isZoomedForce && !game.settings.debugCamera) {
			camera.zoom = camera.zoom + (cameraZoomSize - camera.zoom) * cameraZoomSpeed;
		}
	}
}