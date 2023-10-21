package com.mrboomdev.binacty.script.__legacy.world;

import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

public class Characters {

	public static void loadForNightId(int id) {
		var game = GameHolder.getInstance();

		if(id == 0 || id == 1) {
			var vanessaPath = BoomFile.internal("packs/fnaf/characters/vanessa").toFileUtil();
			game.environment.entities.loadCharacter(vanessaPath, "vanessa");

			var vanessa = game.environment.entities.cloneCharacter("vanessa");
			new CharacterCreator(vanessa).create("vanessa", game.environment.map);
		}
	}

	public static void create() {
		var game = GameHolder.getInstance();
		var path = game.settings.playerCharacter;

		game.environment.entities = new EntityManager();
		game.environment.entities.loadCharacter(path, "klarrie");

		var player = game.environment.entities.cloneCharacter("klarrie");
		new CharacterCreator(player).create("klarrie", game.environment.map);
		player.setName(game.settings.playerName);

		game.settings.mainPlayer = player;
		game.environment.setupRayHandler();
		game.environment.entities.setMain(player);

		CameraUtil.setCameraPosition(player.getPosition().x, player.getPosition().y);
		CameraUtil.setTarget(player);
		AudioUtil.setTarget(player);
	}
}