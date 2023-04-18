game.load("sound", "sounds/power_end.wav");
game.load("sound", "sounds/door_close.wav");
game.load("character", "characters/freddy");
game.load("character", "characters/bonnie");
game.load("character", "characters/chica");
game.load("character", "characters/foxy");
for(int i = 1; i < 5; i++) {
	game.load("music", "music/dark_ambience_" + i + ".ogg");
}

int power = 100;

/*teams.addTeam("Guard")
	.setSpawnTiles("playerSpawn")
	.fillWithPlayers(1);

teams.addTeam("Animatronics")
	.setShowNames(false);*/
	
/*
botBrain = new BotBrainBuilder()
	.setTiles("triggerAi", "triggerSpawn")
	.setFamily("Animatronics");
*/

var bots = new CharacterCreator[4];
bots[0] = entities.createCharacter("characters/freddy").setSpawnTiles(new String[]{"#id:freddySpawn"});
bots[1] = entities.createCharacter("characters/bonnie").setSpawnTiles(new String[]{"#id:bonnieSpawn"});
bots[2] = entities.createCharacter("characters/chica").setSpawnTiles(new String[]{"#id:chicaSpawn"});
bots[3] = entities.createCharacter("characters/foxy").setSpawnTiles(new String[]{"#id:foxySpawn"});
for(var bot : bots) { bot.create(); }

game.setTimer(new Runnable() {
	void run() {
		for(var bot : bots) {
			bot.setBot();
		}
	}
}, 15);

var doorRight = map.getById("doorRight");
var doorLeft = map.getById("doorLeft");
boolean isDoorRightOpened = true;
boolean isDoorLeftOpened = true;

map.getById("buttonDoorRight").setListener(new InteractionListener() {
	void use() {
		isDoorRightOpened = !isDoorRightOpened;
		doorRight.style.selectStyle(isDoorRightOpened ? "default" : "close");
		audio.playSound("sounds/door_close.wav", 0.5f, 15, doorRight.getPosition(false));
	}
});

map.getById("buttonDoorLeft").setListener(new InteractionListener() {
	void use() {
		isDoorLeftOpened = !isDoorLeftOpened;
		doorLeft.style.selectStyle(isDoorLeftOpened ? "default" : "close");
		audio.playSound("sounds/door_close.wav", 0.5f, 15, doorLeft.getPosition(false));
	}
});
	
/*
ui.setFade(1, 0, 4);
ui.setTitle("SURVIVE THE NIGHT", 4);
ui.setTimer(360, 1.4, true);*/

ui.setListener(new UiListener() {
	void timerEnd() {
		game.over(Target.MAIN_PLAYER, true);
	}
	
	void timerNextSecond() {
		power--;
		if(power <= 0) {
			environment.setEnvironmentColor(0, 0, 0, 0.1);
			audio.clearMusic();
			audio.playSound("sounds/power_end.wav", 1);
		}
	}
});

entities.setListener(new EntityListener() {
	void died(entity) {
		if(entity.isTarget(Target.MAIN_PLAYER)) {
			game.over(entity, false);
		}
	}
});

game.setListener(new GameListener() {
	void start() {
		audio.playMusic(new String[]{
			"music/dark_ambience_1.ogg",
			"music/dark_ambience_2.ogg",
			"music/dark_ambience_3.ogg",
			"music/dark_ambience_4.ogg"},
		999);
	}
});

game.ready();