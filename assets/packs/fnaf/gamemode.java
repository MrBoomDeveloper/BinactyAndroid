game.load("sound", "sounds/power_end.wav");
game.load("sound", "sounds/freddy_nose.wav");
game.load("sound", "sounds/door_close.wav");
game.load("sound", "sounds/error.wav");
game.load("sound", "sounds/foxy_song.wav");
game.load("sound", "sounds/win.wav");
game.load("sound", "sounds/scream.wav");
game.load("music", "sounds/fan.wav");

game.load("character", "characters/freddy");
game.load("character", "characters/bonnie");
game.load("character", "characters/chica");
game.load("character", "characters/foxy");

game.load("item", "items/flashlight");
game.load("item", "$a7739b9c-e7df-11ed-a05b-0242ac120003/src/items/pistol");

game.load("music", "music/music_box.wav");
game.load("music", "music/6am.wav");
game.load("music", "music/light.wav");
for(int i = 1; i < 5; i++) {
	game.load("music", "music/dark_ambience_" + i + ".ogg");
}

String[] waypoints = new String[]{"6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerAi", "6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerSpawn"};
boolean isGameEnded = false;
int power = 100, usage = 1;
var powerWidget, usageWidget;
var fanSound, lightSound;

var freddy = entities.createCharacter("characters/freddy").setSpawnTiles(new String[]{"#id:freddySpawn"});
var bonnie = entities.createCharacter("characters/bonnie").setSpawnTiles(new String[]{"#id:bonnieSpawn"});
var chica = entities.createCharacter("characters/chica").setSpawnTiles(new String[]{"#id:chicaSpawn"});
var foxy = entities.createCharacter("characters/foxy").setSpawnTiles(new String[]{"#id:foxySpawn"});

var freddyBrain = entities.createBrain()
		.setStates(null)
		.setResponder(new BotBrain.Responder() {
			getWaypoints() { return waypoints; }
		}).build();

var bonnieBrain = entities.createBrain()
		.setStates(null)
		.setResponder(new BotBrain.Responder() {
			getWaypoints() { return waypoints; }
		}).build();

var chicaBrain = entities.createBrain()
		.setStates(null)
		.setResponder(new BotBrain.Responder() {
			getWaypoints() { return waypoints; }
		}).build();

var foxyBrain = entities.createBrain()
		.setStates(null)
		.setResponder(new BotBrain.Responder() {
			getWaypoints() { return waypoints; }
		}).build();

freddy.create();
bonnie.create();
chica.create();
foxy.create();

var staticLights = new ArrayList();
for(int i = 1; i <= 17; i++) {
	staticLights.add(map.getById("staticLight" + i));
}

game.setTimer(new Runnable() {run() { bonnie.setBot(bonnieBrain); }}, Math.round(Math.random() * 25 + 25));
game.setTimer(new Runnable() {run() { chica.setBot(chicaBrain); }}, Math.round(Math.random() * 25 + 25));
game.setTimer(new Runnable() {run() { freddy.setBot(freddyBrain); }}, Math.round(Math.random() * 35 + 35));
game.setTimer(new Runnable() {run() { foxy.setBot(foxyBrain); }}, Math.round(Math.random() * 60 + 60));


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
	updateLight();
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
	updateLight();
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

void updateLight() {
	lightSound.stop();
	if(isLightRightOn || isLightLeftOn) lightSound.play();
}

void checkIfNoPower() {
	if(isGameEnded) return;
	if(power == 0) {
		core.environment.entities.mainLight.setColor(0.25f, 0.25f, 0.5f, 0.4f);
		doorLeft.style.selectStyle("default");
		doorRight.style.selectStyle("default");
		lightLeft.pointLight.setActive(false);
		lightRight.pointLight.setActive(false);
		
		audio.clear();
		audio.playSound("sounds/power_end.wav", 1);

		fanSound.stop();
		lightSound.stop();
		if(!isDoorLeftOpened) audio.playSound("sounds/door_close.wav", 0.5f, 15, doorLeft.getPosition(false));
		if(!isDoorRightOpened) audio.playSound("sounds/door_close.wav", 0.5f, 15, doorRight.getPosition(false));
		
		for(var light : staticLights) {
			light.pointLight.setActive(false);
		}
		
		bonnie.entity.die(true);
		chica.entity.die(true);
		foxy.entity.die(true);
		
		freddy.entity.stats.speed = 99999;
		freddy.entity.stats.damage = 99999;
		freddy.entity.stats.maxHealth = 99999;
		freddy.entity.stats.maxStamina = 99999;
		freddy.entity.stats.stamina = 99999;
		freddy.entity.gainDamage(-99999);
		
		game.setTimer(new Runnable() {run() {
			if(isGameEnded) return;
			audio.playMusic("music/music_box.wav", 0.5f);
		}}, (float)(Math.random() * 10 + 3));
	}
}
	
/*ui.setFade(1, 0, 0.5f);
ui.setTitle("SURVIVE THE NIGHT", 4);
ui.setTimer(360, 1.5, true);*/
game.__startOldGamemodeScript();

ui.setListener(new UiListener() {
	timerEnd() {
		isGameEnded = true;
		audio.playMusic("music/6am.wav", 1);
		game.setTimer(new Runnable() {run() {
			audio.playSound("sounds/win.wav", 1);
		}}, 6);
		
		freddy.entity.die(true);
		bonnie.entity.die(true);
		chica.entity.die(true);
		foxy.entity.die(true);
		
		game.setTimer(new Runnable() {run() {
			game.over(entities.getCharacter(Target.MAIN_PLAYER), true);
		}}, 8);
	}
});

entities.setListener(new EntityListener() {
	died(entity) {
		if(entity.isTarget(Target.MAIN_PLAYER)) {
			audio.playSound("sounds/scream.wav", 0.25f);
			isGameEnded = true;
			game.over(entity, false);
		} else {
			if(isGameEnded) return;
			audio.playSound("sounds/scream.wav", 0.05f, 10, entity.getPosition());
		}
	}
});

game.setListener(new GameListener() {
	start() {
		fanSound = createMusic("sounds/fan.wav");
		fanSound.setPosition(24, -14);
		fanSound.setDistance(12);
		fanSound.setLooping(true);
		fanSound.setVolume(0.1f);
		fanSound.play();

		lightSound = createMusic("music/light.wav");
		lightSound.setPosition(24, -14);
		lightSound.setDistance(15);
		lightSound.setLooping(true);
		lightSound.setVolume(0.25f);

		audio.playMusic(new String[]{
			"music/dark_ambience_1.ogg",
			"music/dark_ambience_2.ogg",
			"music/dark_ambience_3.ogg",
			"music/dark_ambience_4.ogg"},
		999);

		usageWidget = ui.createText("statBarWidget.ttf", "Usage: 1").setAlign(Align.LEFT, Align.BOTTOM).toPosition(25, 25);
		powerWidget = ui.createText("statBarWidget.ttf", "100%").setAlign(Align.LEFT, Align.BOTTOM).toPosition(25, 60);
		powerUpdate();
		
		lightLeft.pointLight.setActive(false);
		lightRight.pointLight.setActive(false);
		
		game.setTimer(new Runnable() {run() {
			if(isGameEnded || foxy.entity.isDead) return;
			audio.playSound("sounds/foxy_song.wav", 0.1f);
		}}, (float)(Math.random() * 600 + 30));

		var me = core.settings.mainPlayer;
		me.giveItem(entities.createItem("items/flashlight"));
		me.giveItem(entities.createItem("$a7739b9c-e7df-11ed-a05b-0242ac120003/src/items/pistol"));
	}
	build() {}
	end() {}
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