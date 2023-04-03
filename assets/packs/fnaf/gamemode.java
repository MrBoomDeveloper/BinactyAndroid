//game.load("sound", "sounds/powerEnd.mp3");
game.load("character", "characters/freddy");
//game.load("character", "characters/bonnie");
//game.load("character", "characters/chica");
//game.load("character", "characters/foxy");
for(int i = 1; i < 5; i++) {
	game.load("music", "music/dark_ambience_" + i + ".ogg");
}

int power = 100;

/*teams.addTeam("Guard")
	.setSpawnTiles("playerSpawn")
	.fillWithPlayers(1);

teams.addTeam("Animatronics")
	.setShowNames(false);*/

entities.createCharacter("characters/freddy");
	//.setSpawnTiles("id:freddySpawn")
	//.setBot("characters/bot.json", "aiTiles");
	
/*entities.createCharacter("bonnie")
	.setSpawnTiles("id:bonnieSpawn")
	.setBot("characters/bot.json", "aiTiles");
	
entities.createCharacter("chica")
	.setSpawnTiles("id:chicaSpawn")
	.setBot("characters/bot.json", "aiTiles");
	
entities.createCharacter("foxy")
	.setSpawnTiles("id:foxySpawn")
	.setBot("characters/bot.json", "aiTiles");
	
tiles.getById("buttonDoorLeft")
	.connectTo(tiles.getById("doorLeft"));
	
tiles.getById("buttonDoorRight")
	.connectTo(tiles.getById("doorRight"));
	
tiles.getById("buttonLightLeft")
	.connectTo(tiles.getById("lightLeft"));
	
tiles.getById("buttonLightRight")
	.connectTo(tiles.getById("lightRight"));

ui.setFade(1, 0, 4);
ui.setTmer(360, 1.4, true);
ui.addListener(new TimerListener() {
	void end() {
		game.over(Target.MAIN_PLAYER, true);
		game.over(Target.EVERYONE, false);
	}
	
	void nextSecond(timer) {
		power = power - 1;
		if(power <= 0) {
			timer.reset();
			environment.setEnvironmentColor(0, 0, 0, 0.1);
			audio.clearMusic();
			audio.playSound("sounds/powerEnd.mp3", 1);
		}
	}
});

entities.setListener(new EntityListener(
	void died(entity) {
		if(entity.isTarget(Target.MAIN_PLAYER)) {
			game.over(Target.MAIN_PLAYER, false);
			game.over(Target.EVERYONE, true);
		}
	}
));

game.setListener(new GameListener() {
	void start() {
		audio.startMusic();
	}
});*/

game.ready();