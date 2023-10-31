package com.mrboomdev.binacty.script.__legacy.cutscenes;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.binacty.script.__legacy.LegacyFnafBridge;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.widgets.FadeWidget;

public class IntroCutscene {

	public static void start(@NonNull LegacyFnafBridge bridge) {
		var game = GameHolder.getInstance();

		var fade = new FadeWidget(1);
		fade.addTo(game.environment.stage);
		game.environment.ui.dynamicWidgets.put("fade", fade);

		CameraUtil.setCameraMoveSpeed(1);
		CameraUtil.setCameraZoom(.5f, 1);

		var player = game.environment.map.getCharacter("klarrie");
		player.setPosition(36, 46);
		player.skin.setAnimationForce("damage");
		player.wasPower = new Vector2(-1, 0);

		var vanessa = game.environment.map.getCharacter("vanessa");
		vanessa.setPosition(33, 46);
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

		for(var tile : game.environment.map.tilesMap.values()) {
			if(tile.light == null) continue;

			tile.pointLight.setActive(false);
		}

		bridge.isReady = true;

		CameraUtil.setCameraMoveSpeed(.005f);
		CameraUtil.setCameraPosition(36, 42);
		CameraUtil.setCameraZoom(.3f, .005f);

		fade.start(1, 0, .05f);

		FunUtil.setTimer(() -> {
			player.skin.setAnimationForce("walk");
		}, 10);
	}

	/**
	* So this is a fix for a boilerplate code :)
	**/
	private static void setWidgetVisibility(String name, boolean isVisible) {
		var game = GameHolder.getInstance();

		var widgets = game.environment.ui.widgets;
		if(!widgets.containsKey(name)) return;

		var widget = widgets.get(name);
		widget.setVisible(isVisible);
	}
}