package com.mrboomdev.binacty.script.__legacy;

import com.mrboomdev.binacty.script.__legacy.cutscenes.IntroCutscene;
import com.mrboomdev.binacty.script.__legacy.world.Lifecycle;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.CameraUtil;

public class Main {
	private final GameHolder game = GameHolder.getInstance();
	private final LegacyFnafBridge bridge;

	public Main(LegacyFnafBridge bridge) {
		this.bridge = bridge;
	}

	public void start() {
		Lifecycle.initNaturalLights();

		var saves = bridge.client.getSaves();
		var player = game.settings.mainPlayer;
		player.name = "";

		if(saves.getInt("night") == 0) {
			IntroCutscene.start(bridge);
		} else {
			player.setPosition(22, -14);
			CameraUtil.setCameraPosition(22, -14);

			game.settings.isControlsEnabled = true;
			game.settings.isUiVisible = true;

			bridge.isReady = true;
		}
	}
}