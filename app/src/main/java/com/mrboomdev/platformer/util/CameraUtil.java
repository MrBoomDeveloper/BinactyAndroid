package com.mrboomdev.platformer.util;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.game.GameHolder;

@SuppressWarnings("unused")
public class CameraUtil {
	public static boolean isZoomedForce;
	private static final float DEFAULT_CAMERA_SPEED = .05f;
	private static float shakePower, shakeDuration, shakeProgress;
	private static float cameraZoomSize, cameraZoomSpeed, cameraSpeed;
	private static final Vector2 shakeCurrent = new Vector2();
	private static final Vector2 offset = new Vector2();
	
	public static void addCameraShake(float power, float duration) {
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
	
	public static void update(float delta) {
		var game = GameHolder.getInstance();
		var camera = game.environment.camera;
		var player = game.settings.mainPlayer;

		shakeProgress += delta;
		if(shakeProgress < shakeDuration) {
			shakeCurrent.set(
					(float)(Math.random() * shakePower - (shakePower / 2)),
					(float)Math.random() * shakePower - (shakePower / 2));
		} else {
			shakeCurrent.setZero();
		}

		if(!player.isDead) {
			var playerPosition = player.getPosition();

			camera.position.set(shakeCurrent.add(
					camera.position.x + ((playerPosition.x + offset.x) - camera.position.x) * (cameraSpeed / camera.zoom),
					camera.position.y + ((playerPosition.y + offset.y) - camera.position.y) * (cameraSpeed / camera.zoom)
			), 0);
		}

		if(!game.settings.enableEditor && !isZoomedForce) {
			camera.zoom = camera.zoom + (cameraZoomSize - camera.zoom) * cameraZoomSpeed;
		}
	}
}