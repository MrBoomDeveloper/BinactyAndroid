import com.badlogic.gdx.math.Vector2;

/* ----------
	INIT VALUES
----------*/

var startups = new int[][]{{ 1, 1 }, { 5, 5 }, { 25, 25 }, { 10, 10 }};
String[] waypoints = new String[]{"6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerAi", "6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerSpawn"};

var powerWidget, usageWidget, subtitles;
var fanSound, lightSound, phoneSound, partySong;

boolean didFoxyCutsceneEnded;
boolean isGameEnded, isFreddyActive, isPartySongStarted, didPlayerEnteredOffice;
int nightId = 1, power = 100, usage = 1;

switch(game.getEnvString("levelId", "night_0")) {
	case "night_1": {
		startups = new int[][] {
			{ 60, 60   }, /* Bonnie */
			{ 85, 85   }, /* Chica  */
			{  1, 9999 }, /* Freddy */
			{  1, 9999 }  /* Foxy   */
		};

		nightId = 1;
	} break;

	case "night_2": {
		startups = new int[][] {
			{  60, 35   },
			{  60, 35   },
			{   1, 9999 },
			{ 120, 120  }
		};

		nightId = 2;
	} break;

	case "night_3": {
		startups = new int[][] {
			{  25, 25  },
			{  35, 35  },
			{ 140, 140 },
			{  90, 90  }
		};

		nightId = 3;
	} break;

	case "night_4": {
		startups = new int[][] {
			{  10, 10  },
			{  20, 20  },
			{ 100, 100 },
			{  50, 50  }
		};

		nightId = 4;
	} break;

	case "night_5": {
		startups = new int[][] {
			{  1, 1  },
			{  5, 5  },
			{ 25, 25 },
			{ 10, 10 }
		};

		nightId = 5;
	} break;

	default: {
		startups = new int[][] {
			{ 1, 1 },
			{ 1, 1 },
			{ 1, 1 },
			{ 1, 1 }
		};

		nightId = 1;
	} break;
}

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

for(int i = 1; i <= 3; i++) {
	game.load("sound", "sounds/freddy_giggle_" + i + ".wav");
}

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

if(nightId == 1) {
	//game.load("character", "characters/vanessa");
}

game.load("music", "music/phone_" + nightId + ".wav");

/* ----------
	CREATE ENTITIES
----------*/

createAnimatronic(String name) {
	var character = entities.createCharacter("characters/" + name);
	character.setSpawnTiles(new String[]{ "#id:" + name + "Spawn" });
	character.create();

	return character;
}

createBrain() {
	return entities.createBrain()
		.setStates(null)
		.setResponder(new BotBrain.Responder() {
			getWaypoints() { return waypoints; }
		}).build();
}

void wakeUpOnDamage(var character, var brain) {
	int attacksCount;

	character.entity.setDamagedListener(new DamagedListener() { damaged(attacker, damage) {
		if(character.entity.brain == brain) return;

		attacksCount++;

		if(attacksCount >= 5) {
			character.setBot(brain);
		}
	}});
}

var freddy = createAnimatronic("freddy");
var bonnie = createAnimatronic("bonnie");
var chica = createAnimatronic("chica");
var foxy = createAnimatronic("foxy");

var freddyBrain = createBrain();
var bonnieBrain = createBrain();
var chicaBrain = createBrain();
var foxyBrain = createBrain();

wakeUpOnDamage(freddy, freddyBrain);
wakeUpOnDamage(bonnie, bonnieBrain);
wakeUpOnDamage(chica, chicaBrain);
wakeUpOnDamage(foxy, foxyBrain);

/* ----------
	MAKE MAP INTERACTABLE
----------*/

var staticLights = new ArrayList();
for(int i = 1; i < 20; i++) {
	var light = map.getById("staticLight" + i);
	if(light == null) continue;
	staticLights.add(light);
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
	foxy.entity.body.setTransform(38.5f, 31.2f, 0);

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
		CameraUtil.setTarget(foxy.entity);
		CameraUtil.setCameraZoom(0.45f, .04f);

		foxy.entity.attack(Vector2.Zero);

		game.setTimer(new Runnable() { run() {
			foxy.entity.attack(Vector2.Zero);

			game.setTimer(new Runnable() { run() {
				foxy.entity.attack(Vector2.Zero);

				game.setTimer(new Runnable() { run() {
					foxy.entity.attack(Vector2.Zero);

					game.setTimer(new Runnable() { run() {
						foxy.entity.attack(Vector2.Zero);
					}}, .5f);
				}}, .5f);
			}}, .5f);
		}}, .5f);

		game.setTimer(new Runnable() { run() {
			var brain = new BotFollower();
			brain.setWaypoints(waypoints);

			brain.setTarget(map.getById("foxySpawn"));

			game.setTimer(new Runnable() { run() {
				CameraUtil.reset();
				CameraUtil.setTarget(core.settings.mainPlayer);
				ui.setVisibility(true);
				game.setControlsEnabled(true);
			}}, 3);

			brain.onCompleted(new Runnable() { run() {
				foxy.entity.wasPower = new Vector2(1, 0);
				foxy.entity.setBrain(null);
			}});

			foxy.setBot(brain);
		}}, 3);
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
	
	if(!isDoorLeftOpened) audio.playSound("sounds/door_close.wav", 0.5f, 15, doorLeft.getPosition());
	if(!isDoorRightOpened) audio.playSound("sounds/door_close.wav", 0.5f, 15, doorRight.getPosition());
	
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
		if(entity == core.settings.mainPlayer) {
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
		var fade = ui.createFade(1);
		
		lightLeft.pointLight.setActive(false);
		lightRight.pointLight.setActive(false);

		var me = core.settings.mainPlayer;
		me.giveItem(entities.createItem("items/flashlight"));
		me.giveItem(entities.createItem("$a7739b9c-e7df-11ed-a05b-0242ac120003/src/items/pistol"));
		
		if(nightId == 1) {
			setCameraPosition(36, 46);
			game.setPlayerPosition(36, 46);
		}

		game.setControlsEnabled(true);
		ui.setVisibility(true);

		if(nightId == 1) {
			core.settings.mainPlayer.wasPower = new Vector2(-1, 0);
			setCameraZoom(.2f, 1);

			setWidgetVisibility("inventory", false);
			setWidgetVisibility("use", false);
			setWidgetVisibility("dash", false);
			setWidgetVisibility("stats_health", false);
			setWidgetVisibility("stats_stamina", false);
			setWidgetVisibility("aim", false);

			game.setTimer(new Runnable() { run() {
				fade.start(1, 0, 10);

				game.setTimer(new Runnable() { run() {
					setCameraZoom(.5f, .15f);
				}}, .75f);

				float fadeDuration = .25f;
				subtitles = createSubtitles();
				subtitles.addLine("Hello!", fadeDuration);
				subtitles.addLine("You're awake!", fadeDuration);
				subtitles.addLine("Are you a new guard!? Aren't you???", fadeDuration);

				subtitles.addLine("Come here! You'll like this place.", fadeDuration, new Runnable() { run() {
					setCameraZoom(.4f, .1f);
					setWidgetVisibility("dash", true);
				}});

				subtitles.addLine("Welcome to the Freddy Fazbear's Pizzeria!", .5f);
				subtitles.addLine("A fantastic place, where party NEVER ends!", .5f, new Runnable() { run() {
					setWidgetVisibility("inventory", true);
					setWidgetVisibility("use", true);
					setWidgetVisibility("stats_health", true);
					setWidgetVisibility("stats_stamina", true);
					setWidgetVisibility("aim", true);
				}});
			}}, 3);
		} else {
			startDarkMusic();
			fade.start(1, 0, .1f);
			setCameraZoom(1, 1);

			game.setTimer(new Runnable() { run() {
				setCameraZoom(0.5f, .01f);
			}}, .1f);
		}

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
	}
	
	build() {}
	end() {}
});

void startDarkMusic() {
	audio.playMusic(new String[] {
		"music/dark_ambience_1.ogg",
		"music/dark_ambience_2.ogg",
		"music/dark_ambience_3.ogg",
		"music/dark_ambience_4.ogg"},
	999);
}

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
		if(bonnie.entity.brain == bonnieBrain) return;

		bonnie.setBot(bonnieBrain);
	}}, Math.round(Math.random() * startups[0][0] + startups[0][1]));

	game.setTimer(new Runnable() { run() {
		if(chica.entity.brain == chicaBrain) return;

		chica.setBot(chicaBrain);
	}}, Math.round(Math.random() * startups[1][0] + startups[1][1]));

	game.setTimer(new Runnable() { run() {
		if(isFreddyActive || freddy.entity.brain == freddyBrain) return;

		freddy.setBot(freddyBrain);
		isFreddyActive = true;
	}}, Math.round(Math.random() * startups[2][0] + startups[2][1]));

	game.setTimer(new Runnable() { run() {
		if(foxy.entity.brain == foxyBrain) return;

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
		if(isGameEnded || foxy.entity.isDead || nightId < 2) return;
		audio.playSound("sounds/foxy_song.wav", 0.1f);
	}}, (float)(Math.random() * 600 + 30));

	game.setTimer(new Runnable() { run() {
		setCameraZoom(.7f, .025f);
	}}, .25f);

	if(nightId > 2) freddyGiggleTimer();
}

void freddyGiggleTimer() {
	game.setTimer(new Runnable() { run() {
		audio.playSound("sounds/freddy_giggle_" + Math.round(Math.random() * 2 + 1) + ".wav", 1);
		freddyGiggleTimer();
	}}, (float)(Math.random() * 200 + 25));
}