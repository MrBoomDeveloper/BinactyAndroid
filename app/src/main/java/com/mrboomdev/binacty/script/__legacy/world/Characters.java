package com.mrboomdev.binacty.script.__legacy.world;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

public class Characters {

	public static void createForNightId(int id) {
		if(id == 0 || id == 1) {
			createCharacters("freddy", "bonnie", "chica", "foxy", "vanessa");
			return;
		}

		createCharacters("freddy", "bonnie", "chica", "foxy");
	}

	public static void createCharacters(@NonNull String... names) {
		var game = GameHolder.getInstance();

		for(var name : names) {
			var vanessaPath = BoomFile.internal("packs/fnaf/characters/" + name).toFileUtil();
			game.environment.entities.loadCharacter(vanessaPath, name);

			var vanessa = game.environment.entities.cloneCharacter(name);
			CharacterCreator.create(vanessa, name, game.environment.map);
		}
	}

	public static void create() {
		var game = GameHolder.getInstance();
		var path = game.settings.playerCharacter;

		game.environment.entities = new EntityManager();
		game.environment.entities.loadCharacter(path, "klarrie");

		var player = game.environment.entities.cloneCharacter("klarrie");
		CharacterCreator.create(player, "klarrie", game.environment.map);
		player.setName(game.settings.playerName);

		game.settings.mainPlayer = player;
		game.environment.setupRayHandler();
		game.environment.entities.setMain(player);

		CameraUtil.setTarget(player);
		AudioUtil.setTarget(player);
	}
}