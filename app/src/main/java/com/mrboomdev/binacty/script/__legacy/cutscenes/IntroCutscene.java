package com.mrboomdev.binacty.script.__legacy.cutscenes;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.binacty.script.__legacy.LegacyFnafBridge;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.CameraUtil;

public class IntroCutscene {

	public static void start(@NonNull LegacyFnafBridge bridge) {
		var game = GameHolder.getInstance();

		CameraUtil.setCameraZoom(.1f, 1);
		CameraUtil.setCameraPosition(36, 46);
		CameraUtil.setCameraOffsetForce(-1, .2f);

		var player = game.environment.map.getCharacter("klarrie");
		player.setPosition(36, 46);
		player.skin.setAnimationForce("damage");
		player.wasPower = new Vector2(-1, 0);

		var vanessa = game.environment.map.getCharacter("vanessa");
		vanessa.setPosition(35, 46);
		vanessa.stats.maxHealth = 9999999;
		vanessa.stats.health = 9999999;

		player.lookAt(vanessa);
		vanessa.lookAt(player);

		setWidgetVisibility("inventory", false);
		setWidgetVisibility("use", false);
		setWidgetVisibility("joystick", false);
		setWidgetVisibility("dash", false);
		setWidgetVisibility("stats_health", false);
		setWidgetVisibility("aim", false);

		bridge.isReady = true;
	}

	private static void setWidgetVisibility(String name, boolean isVisible) {
		var game = GameHolder.getInstance();

		var widgets = game.environment.ui.widgets;
		if(!widgets.containsKey(name)) return;

		var widget = widgets.get(name);
		widget.setVisible(isVisible);
	}
}