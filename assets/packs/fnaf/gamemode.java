game.load("sound", "sounds/power_end.wav");
game.load("sound", "sounds/freddy_nose.wav");
game.load("sound", "sounds/door_close.wav");
game.load("sound", "sounds/error.wav");
game.load("sound", "sounds/foxy_song.wav");
game.load("sound", "sounds/win.wav");
game.load("character", "characters/freddy");
game.load("character", "characters/bonnie");
game.load("character", "characters/chica");
game.load("character", "characters/foxy");
game.load("music", "music/music_box.wav");
game.load("music", "music/6am.wav");
game.load("music", "music/light.wav");
for(int i = 1; i < 5; i++) {
	game.load("music", "music/dark_ambience_" + i + ".ogg");
}

boolean isGameEnded = false;
int power = 100, usage = 1;
var powerWidget, usageWidget;

var botBrain = entities.createBrain().build();

var bots = new ArrayList();
bots.add(entities.createCharacter("characters/freddy").setSpawnTiles(new String[]{"#id:freddySpawn"}));
bots.add(entities.createCharacter("characters/bonnie").setSpawnTiles(new String[]{"#id:bonnieSpawn"}));
bots.add(entities.createCharacter("characters/chica").setSpawnTiles(new String[]{"#id:chicaSpawn"}));
bots.add(entities.createCharacter("characters/foxy").setSpawnTiles(new String[]{"#id:foxySpawn"}));
for(var bot : bots) { bot.create(); }

var staticLights = new ArrayList();
for(int i = 1; i <= 13; i++) {
	staticLights.add(map.getById("staticLight" + i));
}

game.setTimer(new Runnable() {run() {
	for(var bot : bots) {
		bot.setBot(botBrain);
	}
}}, 15);

var doorRight = map.getById("doorRight"), doorLeft = map.getById("doorLeft");
boolean isDoorRightOpened = true, isDoorLeftOpened = true;

var lightRight = map.getById("lightRight"), lightLeft = map.getById("lightLeft");
boolean isLightRightOn, isLightLeftOn;

map.getById("freddyNose").setListener(new InteractionListener() {use() {
	audio.playSound("sounds/freddy_nose.wav", 0.5f, 10, map.getById("freddyNose").getPosition(false));
}});

map.getById("buttonLightRight").setListener(new InteractionListener() {use() {
	if(power <= 0) {
		audio.playSound("sounds/error.wav", 0.5f, 15, lightRight.getPosition(false));
		return;
	}
	isLightRightOn = !isLightRightOn;
	lightRight.pointLight.setActive(isLightRightOn);
	usage += (isLightRightOn ? 1 : -1);
	uiUpdate();
}});

map.getById("buttonLightLeft").setListener(new InteractionListener() {use() {
	if(power <= 0) {
		audio.playSound("sounds/error.wav", 0.5f, 15, lightLeft.getPosition(false));
		return;
	}
	isLightLeftOn = !isLightLeftOn;
	lightLeft.pointLight.setActive(isLightLeftOn);
	usage += (isLightLeftOn ? 1 : -1);
	uiUpdate();
}});

map.getById("buttonDoorRight").setListener(new InteractionListener() {use() {
	if(power <= 0) {
		audio.playSound("sounds/error.wav", 0.5f, 15, doorRight.getPosition(false));
		return;
	}
	isDoorRightOpened = !isDoorRightOpened;
	doorRight.style.selectStyle(isDoorRightOpened ? "default" : "close");
	audio.playSound("sounds/door_close.wav", 0.5f, 15, doorRight.getPosition(false));
	usage += isDoorRightOpened ? -1 : 1;
	uiUpdate();
}});

map.getById("buttonDoorLeft").setListener(new InteractionListener() {use() {
	if(power == 0) {
		audio.playSound("sounds/error.wav", 0.5f, 15, doorLeft.getPosition(false));
		return;
	}
	isDoorLeftOpened = !isDoorLeftOpened;
	doorLeft.style.selectStyle(isDoorLeftOpened ? "default" : "close");
	audio.playSound("sounds/door_close.wav", 0.5f, 15, doorLeft.getPosition(false));
	usage += isDoorLeftOpened ? -1 : 1;
	uiUpdate();
}});

void checkIfNoPower() {
	if(isGameEnded) return;
	if(power == 0) {
		doorLeft.style.selectStyle("default");
		doorRight.style.selectStyle("default");
		if(!isDoorLeftOpened) {
			audio.playSound("sounds/door_close.wav", 0.5f, 15, doorLeft.getPosition(false));
		}
		if(!isDoorRightOpened) {
			audio.playSound("sounds/door_close.wav", 0.5f, 15, doorRight.getPosition(false));
		}
		
		lightLeft.pointLight.setActive(false);
		lightRight.pointLight.setActive(false);
		
		audio.clear();
		audio.playSound("sounds/power_end.wav", 1);
		core.environment.entities.mainLight.setColor(0.25f, 0.25f, 0.5f, 0.4f);
		
		for(var light : staticLights) {
			light.pointLight.setActive(false);
		}
		
		for(var bot : bots) {
			bot.entity.stats.speed *= 3;
			bot.entity.stats.damage *= 3;
			bot.entity.stats.maxHealth *= 3;
			bot.entity.stats.stamina *= 3;
			bot.entity.gainDamage(-999);
		}
		
		game.setTimer(new Runnable() {run() {
			audio.playMusic("music/music_box.wav", 0.5f);
		}}, (float)(Math.random() * 10 + 3));
	}
}
	
/*ui.setFade(1, 0, 0.5f);
ui.setTitle("SURVIVE THE NIGHT", 4);
ui.setTimer(360, 1.5, true);*/

ui.setListener(new UiListener() {
	timerEnd() {
		isGameEnded = true;
		audio.playMusic("music/6am.wav", 1);
		game.setTimer(new Runnable() {run() {
			audio.playSound("sounds/win.wav", 1);
		}}, 6);
		for(var bot : bots) { bot.entity.gainDamage(999); }
		game.setTimer(new Runnable() {run() {
			game.over(entities.getCharacter(Target.MAIN_PLAYER), true);
		}}, 8);
	}
});

entities.setListener(new EntityListener() {
	died(entity) {
		if(entity.isTarget(Target.MAIN_PLAYER)) {
			isGameEnded = true;
			game.over(entity, false);
		}
	}
});

game.setListener(new GameListener() {
	start() {
		audio.playMusic(new String[]{
			"music/dark_ambience_1.ogg",
			"music/dark_ambience_2.ogg",
			"music/dark_ambience_3.ogg",
			"music/dark_ambience_4.ogg"},
		999);
		usageWidget = ui.createText("statBarWidget.ttf").setText("Usage: 1").setAlign(Align.LEFT, Align.BOTTOM).toPosition(25, 25);
		powerWidget = ui.createText("statBarWidget.ttf").setText("100%").setAlign(Align.LEFT, Align.BOTTOM).toPosition(25, 60);
		powerUpdate();
		
		lightLeft.pointLight.setActive(false);
		lightRight.pointLight.setActive(false);
		
		game.setTimer(new Runnable() {run() {
			if(isGameEnded) return;
			audio.playSound("sounds/foxy_song.wav", 0.1f);
		}}, (float)(Math.random() * 600 + 30));
	}
});

void powerUpdate() {
	game.setTimer(new Runnable() {run() {
		if(power <= 0 || isGameEnded) {
			uiUpdate();
			return;
		}
		
		power -= 1;
		checkIfNoPower();
		powerUpdate();
		uiUpdate();
	}}, 6 / usage);
}

void uiUpdate() {
	if(power > 0) {
		powerWidget.setText(power + "%").setOpacity(1);
		usageWidget.setText("Usage: " + usage);
		return;
	}
	
	powerWidget.setOpacity(0);
	usageWidget.setOpacity(0);
}