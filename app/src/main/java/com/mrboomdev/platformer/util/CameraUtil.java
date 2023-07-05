package com.mrboomdev.platformer.util;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.game.GameHolder;

@SuppressWarnings("unused")
public class CameraUtil {
	private static final float CAMERA_SPEED = .05f;
	private static float shakePower, shakeDuration, shakeProgress;
	private static float cameraZoomSize, cameraZoomSpeed;
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

	public static void setCameraZoom(float size, float speed) {
		cameraZoomSize = size;
		cameraZoomSpeed = speed;
	}
	
	public static void update(float delta) {
		shakeProgress += delta;
		if(shakeProgress < shakeDuration) {
			shakeCurrent.set(
					(float)(Math.random() * shakePower - (shakePower / 2)), 
					(float)Math.random() * shakePower - (shakePower / 2));
		} else {
			shakeCurrent.setZero();
		}

		var player = GameHolder.getInstance().settings.mainPlayer;
		if(!player.isDead) {
			var playerPosition = player.getPosition();
			var camera = GameHolder.getInstance().environment.camera;

			camera.position.set(shakeCurrent.add(
					camera.position.x + ((playerPosition.x + offset.x) - camera.position.x) * (CAMERA_SPEED / camera.zoom),
					camera.position.y + ((playerPosition.y + offset.y) - camera.position.y) * (CAMERA_SPEED / camera.zoom)
			), 0);

			camera.zoom = camera.zoom + (cameraZoomSize - camera.zoom) * cameraZoomSpeed;
		}
	}
}