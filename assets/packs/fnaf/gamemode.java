/* ----------
	LOAD RESOURCES
----------*/

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
game.load("music", "music/party_song.ogg");
game.load("music", "music/6am.wav");
game.load("music", "music/light.wav");

for(int i = 1; i < 5; i++) {
	game.load("music", "music/dark_ambience_" + i + ".ogg");
}

/* ----------
	INIT VALUES
----------*/

var startups = new int[][]{{25, 25}, {30, 30}, {35, 35}, {60, 60}};
int nightId = 1;

switch(game.getEnvString("levelId", "night_0")) {
	case "night_1": {
		startups = new int[][]{
			{ 60,  60  }, /* Bonnie */
			{ 85,  85  }, /* Chica  */
			{ 200, 200 }, /* Freddy */
			{ 150, 150 }  /* Foxy   */
		};
		
		nightId = 1;
	} break;

	case "night_2": {
		startups = new int[][]{{35, 35}, {60, 60}, {175, 175}, {120, 120}};
		nightId = 2;
	} break;

	case "night_3": {
		startups = new int[][]{{25, 25}, {35, 35}, {140, 140}, {90, 90}};
		nightId = 3;
	} break;

	case "night_4": {
		startups = new int[][]{{10, 10}, {20, 20}, {100, 100}, {50, 50}};
		nightId = 4;
	} break;

	default: {
		startups = new int[][]{{1, 1}, {5, 5}, {25, 25}, {10, 10}};
		nightId = 1;
	} break;
}

game.load("music", "music/phone_" + nightId + ".wav");

String[] waypoints = new String[]{"6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerAi", "6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerSpawn"};
boolean isGameEnded = false, isFreddyActive = false, isPartySongStarted = false, didPlayerEnteredOffice = false;
int power = 100, usage = 1;
var powerWidget, usageWidget;
var fanSound, lightSound, phoneSound, partySong;

/* ----------
	CREATE ENTITIES
----------*/

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

/* ----------
	MAKE MAP INTERACTABLE
----------*/

var staticLights = new ArrayList();
for(int i = 1; i <= 20; i++) {
	staticLights.add(map.getById("staticLight" + i));
}

var doorRight = map.getById("doorRight"), doorLeft = map.getById("doorLeft");
boolean isDoorRightOpened = true, isDoorLeftOpened = true;

var lightRight = map.getById("lightRight"), lightLeft = map.getById("lightLeft");
boolean isLightRightOn = false, isLightLeftOn = false;

map.getById("freddyNose").setListener(new InteractionListener() { use() {
	audio.playSound("sounds/freddy_nose.wav", 0.5f, 10, map.getById("freddyNose").getPosition(false));
}});

map.getById("buttonLightRight").setListener(new InteractionListener() { use() {
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

map.getById("buttonLightLeft").setListener(new InteractionListener() { use() {
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

map.getById("buttonDoorRight").setListener(new InteractionListener() { use() {
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

map.getById("buttonDoorLeft").setListener(new InteractionListener() { use() {
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

/* ----------
	GAMEPLAY
----------*/

var officeTrigger = new Trigger(24, -15, 5, new TriggerCallback() { triggered(var character) {
	if(character != core.settings.mainPlayer || didPlayerEnteredOffice) return;

	didPlayerEnteredOffice = true;
	startNight();
	officeTrigger.remove();
}});

if(nightId == 1) {
	foxy.entity.body.setTransform(38.5f, 31, 0);

	var foxyTrigger = new Trigger(35, 32, 4, new TriggerCallback() { triggered(var character) {
		if(character != core.settings.mainPlayer) return;

		foxyCutscene();
		foxyTrigger.remove();
	}});
}

void foxyCutscene() {
	ui.setVisibility(false);
	game.setControlsEnabled(false);

	game.setTimer(new Runnable() { run() {
		CameraUtil.setCameraMoveSpeed(.01f);
		CameraUtil.setCameraOffset(1, -5);
		CameraUtil.setCameraZoom(0.45f, .04f);

		game.setTimer(new Runnable() { run() {
			var brain = new BotFollower();
			brain.setWaypoints(waypoints);
			brain.goTo(12, 8);

			brain.onCompleted(new Runnable() { run() {
				CameraUtil.reset();
				ui.setVisibility(true);
				game.setControlsEnabled(true);
			}});

			foxy.entity.setBrain(brain);

//			foxy.entity.body.setTransform(12, 8, 0);

//			game.setTimer(new Runnable() { run() {
//				CameraUtil.reset();
//				ui.setVisibility(true);
//				game.setControlsEnabled(true);
//			}}, 3);
		}}, 6);
	}}, .5f);
}

void checkIfNoPower() {
	if(isGameEnded || power > 0) return;
	
	var mainLight = core.environment.entities.mainLight;
	var rayHandler = core.environment.rayHandler;
	mainLight.setColor(0.25f, 0.25f, 0.5f, 0.4f);
	
	doorLeft.style.selectStyle("default");
	doorRight.style.selectStyle("default");
	lightLeft.pointLight.setActive(false);
	lightRight.pointLight.setActive(false);
	
	phoneSound.stop();
	fanSound.stop();
	lightSound.stop();
	
	if(isPartySongStarted) partySong.stop();
	
	audio.clear();
	audio.playSound("sounds/power_end.wav", 1);
	
	if(!isDoorLeftOpened) audio.playSound("sounds/door_close.wav", 0.5f, 15, doorLeft.getPosition(false));
	if(!isDoorRightOpened) audio.playSound("sounds/door_close.wav", 0.5f, 15, doorRight.getPosition(false));
	
	for(var light : staticLights) {
		light.pointLight.setActive(false);
	}
	
	bonnie.entity.gainDamage(-99999);
	chica.entity.gainDamage(-99999);
	foxy.entity.gainDamage(-99999);
	
	freddy.entity.stats.speed = 99999;
	freddy.entity.stats.damage = 99999;
	freddy.entity.stats.maxHealth = 99999;
	freddy.entity.stats.maxStamina = 99999;
	freddy.entity.stats.stamina = 99999;
	freddy.entity.gainDamage(-99999);
	
	game.setTimer(new Runnable() { run() {
		if(isGameEnded) return;
		var musicBox = createMusic("music/music_box.wav");
		musicBox.setVolume(0.5f);
		musicBox.play();
		
		game.setTimer(new Runnable() { run() {
			if(isGameEnded) return;
			musicBox.stop();
			mainLight.setColor(0, 0, 0, 0);
			rayHandler.setAmbientLight(0, 0, 0, 0);
			
			game.setTimer(new Runnable() { run() {
				if(isGameEnded) return;
				if(!isFreddyActive) {
					freddy.setBot(freddyBrain);
					isFreddyActive = true;
				}
				
				freddy.entity.body.setTransform(core.settings.mainPlayer.getPosition(), 0);
			}}, (float)(Math.random() * 10 + 3));
		}}, (float)(Math.random() * 10 + 3));
	}}, (float)(Math.random() * 10 + 3));
}

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
		
		game.setTimer(new Runnable() { run() {
			game.over(entities.getCharacter(Target.MAIN_PLAYER), true);
		}}, 8);
	}
});

entities.setListener(new EntityListener() {
	died(entity) {
		if(isGameEnded) return;
		if(entity.isTarget(Target.MAIN_PLAYER)) {
			phoneSound.stop();
			audio.playSound("sounds/scream.wav", 0.275f);
			isGameEnded = true;
			game.over(entity, false);
		} else {
			audio.playSound("sounds/scream.wav", 0.05f, 10, entity.getPosition());
		}
	}
});

game.setListener(new GameListener() {
	start() {
		setCameraZoom(1, 1);
		ui.createFade(1).start(1, 0, .1f);
		
		game.setTimer(new Runnable() { run() {
			setCameraZoom(0.5f, .01f);
		}}, .1f);

		fanSound = createMusic("sounds/fan.wav");
		fanSound.setPosition(24, -14);
		fanSound.setDistance(12);
		fanSound.setLooping(true);
		fanSound.setVolume(0.075f);
		fanSound.play();
		
		phoneSound = createMusic("music/phone_" + nightId + ".wav");
		phoneSound.setPosition(24, -14);
		phoneSound.setDistance(12);
		phoneSound.setVolume(0.5f);

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
		
		lightLeft.pointLight.setActive(false);
		lightRight.pointLight.setActive(false);

		var me = core.settings.mainPlayer;
		me.giveItem(entities.createItem("items/flashlight"));
		me.giveItem(entities.createItem("$a7739b9c-e7df-11ed-a05b-0242ac120003/src/items/pistol"));
		
		if(nightId == 1) {
			setCameraPosition(36, 42);
			game.setPlayerPosition(36, 42);
		}

		game.setControlsEnabled(true);
		ui.setVisibility(true);
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
	}}, 4f / usage);
}

void uiUpdate() {
	if(!didPlayerEnteredOffice) return;

	if(power > 0) {
		powerWidget.setText(power + "%").setOpacity(1);
		usageWidget.setText("Usage: " + usage);
		return;
	}
	
	powerWidget.setOpacity(0);
	usageWidget.setOpacity(0);
}

void startNight() {
	game.setTimer(new Runnable() { run() {
		bonnie.setBot(bonnieBrain);
	}}, Math.round(Math.random() * startups[0][0] + startups[0][1]));

	game.setTimer(new Runnable() { run() {
		chica.setBot(chicaBrain);
	}}, Math.round(Math.random() * startups[1][0] + startups[1][1]));

	game.setTimer(new Runnable() { run() {
		if(isFreddyActive) return;

		freddy.setBot(freddyBrain);
		isFreddyActive = true;
	}}, Math.round(Math.random() * startups[2][0] + startups[2][1]));

	game.setTimer(new Runnable() { run() {
		foxy.setBot(foxyBrain);
	}}, Math.round(Math.random() * startups[3][0] + startups[3][1]));

	boolean isPartySongEnabled = Math.random() > .75f;
	if(isPartySongEnabled) {
		game.setTimer(new Runnable() { run() {
			if(power <= 0) return;

			isPartySongStarted = true;
			partySong = createMusic("music/party_song.ogg");
			partySong.setPosition(22, 21);
			partySong.setDistance(35);
			partySong.setVolume(0.5f);
			partySong.play();
		}}, (float)(Math.random() * 300 + 60));
	}

	ui.createTimer(360, 1.2f);
	ui.createTitle("Survive the Night", 4);

	phoneSound.play();

	usageWidget = ui.createText("statBarWidget.ttf", "Usage: 1").setAlign(Align.LEFT, Align.BOTTOM).toPosition(25, 25);
	powerWidget = ui.createText("statBarWidget.ttf", "100%").setAlign(Align.LEFT, Align.BOTTOM).toPosition(25, 60);
	powerUpdate();

	game.setTimer(new Runnable() { run() {
		if(isGameEnded || foxy.entity.isDead) return;
		audio.playSound("sounds/foxy_song.wav", 0.1f);
	}}, (float)(Math.random() * 600 + 30));

	game.setTimer(new Runnable() { run() {
		setCameraZoom(.7f, .025f);
	}}, .25f);
}