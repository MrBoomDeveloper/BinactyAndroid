package com.mrboomdev.binacty.script.__legacy.cutscenes;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.binacty.script.__legacy.LegacyFnafBridge;
import com.mrboomdev.platformer.entity.bot.BotFollower;
import com.mrboomdev.platformer.entity.character.CharacterProgrammable;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.gameplay.layout.SubtitlesLayout;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.widgets.FadeWidget;

import java.util.Objects;

public class IntroCutscene {

	private static void setupUi() {
		var game = GameHolder.getInstance();

		var subtitles = new SubtitlesLayout();
		subtitles.addTo(game.environment.stage);
		subtitles.setLetterByLetterEffectEnabled(false);
		game.environment.ui.dynamicWidgets.put("subtitles", subtitles);

		var fade = new FadeWidget(1);
		fade.addTo(game.environment.stage);
		game.environment.ui.dynamicWidgets.put("fade", fade);

		setWidgetVisibility("inventory", false);
		setWidgetVisibility("use", false);
		setWidgetVisibility("joystick", false);
		setWidgetVisibility("dash", false);
		setWidgetVisibility("stats_health", false);
		setWidgetVisibility("aim", false);
	}

	private static void setupCharacters() {
		var game = GameHolder.getInstance();

		var player = game.environment.map.getCharacter("klarrie");
		var vanessa = game.environment.map.getCharacter("vanessa");

		player.setPosition(37, 46);
		player.skin.setAnimationForce("sit");
		player.wasPower = new Vector2(-1, 0);
		player.lookAt(vanessa);

		vanessa.setPosition(32, 46);
		vanessa.stats.maxHealth = 9999999;
		vanessa.stats.health = 9999999;
		vanessa.lookAt(player);
	}

	public static void start(@NonNull LegacyFnafBridge bridge) {
		setupUi();
		setupCharacters();

		var game = GameHolder.getInstance();
		var subtitles = Objects.requireNonNull((SubtitlesLayout) game.environment.ui.dynamicWidgets.get("subtitles"));
		var fade = Objects.requireNonNull((FadeWidget) game.environment.ui.dynamicWidgets.get("fade"));

		CameraUtil.setCameraMoveSpeed(1);
		CameraUtil.setCameraZoom(.5f, 1);

		var player = (CharacterProgrammable) game.environment.map.getCharacter("klarrie");
		var vanessa = (CharacterProgrammable) game.environment.map.getCharacter("vanessa");
		var waypoints = new String[]{"6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerAi", "6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerSpawn"};

		for(var tile : game.environment.map.tilesMap.values()) {
			if(tile.light == null) continue;

			tile.pointLight.setActive(false);
		}

		bridge.isReady = true;

		CameraUtil.setCameraMoveSpeed(.005f);
		CameraUtil.setCameraPosition(37, 42);
		CameraUtil.setCameraZoom(.1f, .005f);
		fade.start(1, 0, .05f);

		FunUtil.setTimer(() -> {
			player.skin.setAnimationForce("sit_wakeup");

			subtitles.addLine().setText("Klarrie: Uhh...")
					.setSpeed(.7f)
					.setFadeDuration(1.5f)
					.setEndDuration(2.2f)
					.build();

			subtitles.addLine().setText("Where am i?")
					.setSpeed(.85f)
					.setFadeDuration(.75f)
					.setEndDuration(2)
					.build();

			FunUtil.setTimer(() -> subtitles.addLine().setText("Vanessa: Hey!")
					.setSpeed(3)
					.setFadeDuration(.05f)
					.setEndDuration(2)
					.setStartCallback(() -> {
						var vanessaBrain = vanessa.setBrain(new BotFollower(vanessa));
						vanessa.setPosition(33, 46);

						vanessaBrain.setWaypoints(waypoints);
						vanessaBrain.setSpeed(2.5f);
						vanessaBrain.setGoStraightToTarget(true);
						vanessaBrain.setTarget(36, 46);
						vanessaBrain.start();

						FunUtil.setTimer(() -> {
							player.skin.setAnimationForce("sit_scared");

							var klarrieBrain = player.setBrain(new BotFollower(player));
							klarrieBrain.setWaypoints(waypoints);
							klarrieBrain.setSpeed(2.5f);
							klarrieBrain.setGoStraightToTarget(true);
							klarrieBrain.setTarget(38.5f, 46);
							klarrieBrain.start();

							subtitles.addLine()
									.setText("Klarrie: HOLY MOLY!")
									.setFadeDuration(.2f)
									.setSpeed(1)
									.setEndDuration(1)
									.build();

							subtitles.addLine().setText("Vanessa: Huh, sorry")
									.setSpeed(3)
									.setEndDuration(2)
									.setStartCallback(() -> {
										CameraUtil.setCameraMoveSpeed(.001f);
										CameraUtil.setCameraOffset(-2, .1f);
									})
									.build();
						}, .75f);
					})
					.build(), 2);
		}, 12);
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