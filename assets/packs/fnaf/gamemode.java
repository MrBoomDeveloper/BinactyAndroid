import com.badlogic.gdx.math.Vector2;

/* ----------
	INIT VALUES
----------*/

var startups = new int[][]{{ 1, 1 }, { 5, 5 }, { 25, 25 }, { 10, 10 }};
String[] waypoints = new String[]{"6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerAi", "6a7b64fc-d6d4-11ed-afa1-0242ac120002:triggerSpawn"};

var powerWidget, usageWidget, subtitles;
var fanSound, lightSound, phoneSound, partySong;
var presentationTrigger;
var vanessa;

boolean didFoxyCutsceneEnded, didIntroEnded;
boolean isGameEnded, isFreddyActive, isPartySongStarted, didPlayerEnteredOffice, isShowcasing;
int nightId = 1, power = 100, usage = 1;

switch(game.getEnvString("levelId", "night_0")) {
	case "night_1": {
		startups = new int[][] {
			{ 25, 40   }, /* Bonnie */
			{ 85, 85   }, /* Chica  */
			{  1, 9999 }, /* Freddy */
			{  1, 9999 }  /* Foxy   */
		};

		nightId = 1;
	} break;

	case "night_2": {
		startups = new int[][] {
			{  20, 30   },
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
			{ 1, 10 },
			{ 1, 100 },
			{ 1, 100 },
			{ 1, 100 }
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
game.load("sound", "sounds/animatronic_start.mp3");
game.load("sound", "sounds/scream.wav");

for(int i = 1; i <= 3; i++) {
	game.load("sound", "sounds/freddy_giggle_" + i + ".wav");
}

for(int i = 1; i <= 5; i++) {
    game.load("sound", "sounds/robot/run_" + i + ".mp3");
}

game.load("music", "sounds/fan.wav");

game.load("character", "characters/freddy");
game.load("character", "characters/bonnie");
game.load("character", "characters/chica");
game.load("character", "characters/foxy");

game.load("item", "items/flashlight");

game.load("music", "music/music_box.wav");
game.load("music", "music/party_song.ogg");
game.load("music", "music/6am.wav");
game.load("music", "music/light.wav");

for(int i = 1; i < 5; i++) {
	game.load("music", "music/dark_ambience_" + i + ".ogg");
}

if(nightId == 1) {
	game.load("character", "characters/vanessa");
}

game.load("item", "$a7739b9c-e7df-11ed-a05b-0242ac120003/src/items/pistol");

/* ----------
	CREATE ENTITIES
----------*/

createAnimatronic(String name) {
	var character = entities.createCharacter("characters/" + name);
	character.setSpawnTiles(new String[]{ "#id:" + name + "Spawn" });
	character.create();

	if(character.entity.skin.contains("sleep")) {
		character.entity.skin.setAnimationForce("sleep");
	}

	character.entity.skin.setStepListener(new Runnable() {
		int stepNumber = 1;

		void run() {
			var player = core.settings.mainPlayer;
			if(player == null) return;

			float distance = character.entity.getPosition().dst(player.getPosition());
			float howClose = (10 - distance);

			if(distance > 10) return;

			CameraUtil.addCameraShake(howClose * .02f, .2f);
			audio.playSound("sounds/robot/run_" + stepNumber + ".mp3", .5f, 10, character.entity.getPosition());

			stepNumber++;
			if(stepNumber > 5) stepNumber = 1;
		}
	});

	return character;
}

createBrain() {
    boolean ENABLE_NEW_AI = false;

	if(!ENABLE_NEW_AI) {
		return entities.createBrain()
			.setResponder(new BotBrain.Responder() {
				getWaypoints() { return waypoints; }
			}).build();
	} else {
		print("Warning! New AI is a experimental feature and may be unstable!");
	}
    
	var brain = new BotCustom();
	brain.setWaypoints(waypoints);
	brain.addTarget(core.settings.mainPlayer);

	brain.setUpdateListener(new Runnable() {
		var target;
		float stuckIgnoreTimer = 0;
		float seeDistance = 8;

		void selectRandomTile() {
			target = brain.getRandomTarget();
		}

		void run() {
			var player = core.settings.mainPlayer;
			if(player == null) return;

			float distance = brain.getDistance(player);
			float howClose = (10 - distance);

			if(brain.isStuck()) {
				stuckIgnoreTimer = 8;
				brain.escape();
				selectRandomTile();
			}

			if(distance < seeDistance && stuckIgnoreTimer <= 0) {
				brain.goTo(player, 1.5f);

				if(distance < 2) {
				    var entity = brain.getEntity();
					entity.attack(player.getPosition().cpy().sub(entity.getPosition()));
				}
			
			    return;
			}

			stuckIgnoreTimer -= getDeltaTime();
			if(target != null) brain.goTo(target, 1);
		}
	});

	return brain;
}

void wakeUp(var entity, var brain) {
    if(entity.brain == brain || entity.skin.getCurrentAnimationName().equals("wakeup")) return;

	if(entity.skin.contains("wakeup")) {
		entity.skin.setAnimationForce("wakeup");
	}

    audio.playSound("sounds/animatronic_start.mp3", .5f, 12, entity.getPosition());
    
    game.setTimer(new Runnable() { run() {
        if(entity.brain == brain) return;
        
        entity.setBrain(brain);
        brain.start();

        entity.skin.setAnimationForce(null);
    }}, 5);
}

void wakeUpOnDamage(var character, var brain) {
	int attacksCount;

	character.entity.setDamagedListener(new DamagedListener() { damaged(attacker, damage) {
		if(character.entity.brain == brain) return;

		attacksCount++;

		if(attacksCount >= 5) {
			wakeUp(character.entity, brain);
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
for(int i = 1; i < 100; i++) {
	var light = map.getById("staticLight" + i);
	if(light == null) continue;
	
	staticLights.add(light);
}

var doorRight = map.getById("doorRight"), doorLeft = map.getById("doorLeft");
boolean isDoorRightOpened = true, isDoorLeftOpened = true;

var lightRight = map.getById("lightRight"), lightLeft = map.getById("lightLeft");
boolean isLightRightOn = false, isLightLeftOn = false;

map.getById("freddyNose").setListener(new InteractionListener() { use() {
	audio.playSound("sounds/freddy_nose.wav", 0.5f, 10, map.getById("freddyNose").getPosition());
}});

void showcase() {
    if(isShowcasing) return;
    isShowcasing = true;
    
    var freddyLight, bonnieLight, chicaLight;
    
    freddyLight = createLight("point", 8);
    freddyLight.setDistance(4);
    freddyLight.setColor(1, 1, 1, .7f);
    freddyLight.setPosition(23, 26.5f);
    
    game.setTimer(new Runnable() { run() {
        bonnieLight = createLight("point", 8);
        bonnieLight.setColor(1, 1, 1, .85f);
        bonnieLight.setDistance(4);
        bonnieLight.setPosition(21, 25.5f);
    }}, .4f);
    
    game.setTimer(new Runnable() { run() {
        chicaLight = createLight("point", 8);
        chicaLight.setColor(1, 1, 1, .85f);
        chicaLight.setDistance(4);
        chicaLight.setPosition(25, 25.5f);
    }}, .9f);
    
    game.setTimer(new Runnable() { run() {
        freddyLight.remove(true);
        bonnieLight.remove(true);
        chicaLight.remove(true);
        
        isShowcasing = false;
    }}, 25);
}

map.getById("showButton").setListener(new InteractionListener() { use() {
	showcase();
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
	if(character != core.settings.mainPlayer
		|| didPlayerEnteredOffice
		|| (nightId == 2 && !didFoxyCutsceneEnded)
		|| (nightId == 1 && !didIntroEnded)) {
			return false;
		}

	didPlayerEnteredOffice = true;
	startNight();

	return true;
}});

if(nightId == 1) {
	vanessa = createCharacter("characters/vanessa");
	vanessa.setPosition(35, 46);
}

if(nightId == 2) {
	foxy.entity.body.setTransform(38.5f, 31.2f, 0);

	var foxyTrigger = new Trigger(35, 32, 4, new TriggerCallback() { triggered(var character) {
		if(character != core.settings.mainPlayer) return false;

		foxyCutscene();
		return true;
	}});
}

void introAttackCutscene() {
    var me = core.settings.mainPlayer;
    me.inventory.setCurrentItem(1);
    
    ui.setVisibility(false);
    game.setControlsEnabled(false);
    CameraUtil.setCameraMoveSpeed(.01f);
    CameraUtil.setCameraZoom(.5f, .01f);
    CameraUtil.setTarget(vanessa);
    
    var attackerBrain = new BotFollower();
    attackerBrain.setWaypoints(waypoints);
    
    foxy.entity.setBrain(attackerBrain);
    foxy.entity.lookAt(vanessa);
    
    game.setTimer(new Runnable() { run() {
        attackerBrain.start();
        attackerBrain.setTarget(vanessa);
        attackerBrain.setSpeed(1.2f);
    }}, .5f);
    
    var vanessaBrain = new BotFollower();
    vanessaBrain.setWaypoints(waypoints);
    vanessa.setBrain(vanessaBrain);
    vanessaBrain.start();
    vanessaBrain.setSpeed(1f);
    vanessaBrain.setTarget(24, 22);
    
    attackerBrain.onCompleted(new Runnable() { run() {
        vanessa.lookAt(foxy.entity);
        subtitles.addLine("Dang it!", 2);
        
        game.setTimer(new Runnable() { run() {
            foxy.entity.attack(vanessa);
            
            game.setTimer(new Runnable() { run() {
                vanessa.dash();
                vanessaBrain.setTarget(foxy.entity);
                
                vanessaBrain.onCompleted(new Runnable() { run() {
                    vanessa.attack(foxy.entity);
                    foxy.entity.lookAt(null);
                
                    game.setTimer(new Runnable() { run() {
                        vanessa.inventory.setCurrentItem(1);
                        vanessa.attack(foxy.entity);
                        
                        vanessaBrain.setTarget(me);
                        vanessaBrain.setCompletionListener(new Runnable() { run() {
                            //vanessaBrain.setTarget(me);
                        }});
                        
                        vanessa.lookAt(me);
                        me.inventory.setCurrentItem(0);
                    
                        ui.setVisibility(true);
                        game.setControlsEnabled(true);
                        CameraUtil.reset();
                        CameraUtil.setTarget(me);
                    
                        //subtitles.addLine("Quick! Help me move him fo the salvage room!", 4);
                        //subtitles.addLine("GO TO OFFICE. CUTSCENE ISN'T DONE YET!", 5);
                        subtitles.addLine("Quick! Go to the office! It's down the hall!", 4);
                        
                        setWidgetVisibility("use", true);
                        setWidgetVisibility("stats_health", true);
                        didIntroEnded = true;
                    }}, .4f);
                }});
            }}, .5f);
        }}, .2f);
    }});
}

void presentationCutscene() {
	var me = core.settings.mainPlayer;
	
	var brain = new BotFollower();
	brain.setWaypoints(waypoints);

	game.setTimer(new Runnable() { run() {
		vanessa.lookAt(me);
		vanessa.inventory.setCurrentItem(1);
		me.inventory.setCurrentItem(1);
	}}, 1);

	subtitles.addLine("This place... it's been a nightmare for me.", 2.5f, new Runnable() { run() {
		CameraUtil.setTarget(freddy.entity);
		CameraUtil.setCameraZoom(.5f, .01f);
		CameraUtil.setCameraMoveSpeed(.01f);

		game.setControlsEnabled(false);
		ui.setVisibility(false);
		
		showcase();
	}});
	
	subtitles.addLine("I can't explain how you ended up here, so just do, what i'll say.", 3.75f, new Runnable() { run() {
	    CameraUtil.setCameraZoom(.75f, .01f);
	}});
	
	subtitles.addLine("You'll have to maintain the pizzeria", 3, new Runnable() { run() {
	    CameraUtil.setCameraZoom(.35f, .01f);
		CameraUtil.setTarget(vanessa);
		vanessa.inventory.setCurrentItem(0);
	}});
	
	subtitles.addLine("Clean up, wash the floors and keep things in order.", 4, new Runnable() { run() {
	    CameraUtil.setCameraZoom(.25f, .025f);
	}});
	
	subtitles.addLine("And don't forget about the animatronics.", 3, new Runnable() { run() {
	    CameraUtil.setCameraMoveSpeed(.01f);
	    CameraUtil.setCameraZoom(.32f, .01f);
	    
	    vanessa.setBrain(brain);
	    brain.start();
	    
	    brain.setTarget(21, 21.5f);
	    brain.onCompleted(new Runnable() { run() {
	        
	    }});
	}});

	subtitles.addLine("They may seem harmless now, but their performance must be maintained. ", 4, new Runnable() { run() {
	    brain.setTarget(23, 23);
	}});

	subtitles.addLine("Keep an eye on them, fix any glitches, and make sure they stay in their places.", 4.2f);

	subtitles.addLine("So now go to the office. I'll have to go!", 2.75f, new Runnable() { run() {
		CameraUtil.reset();
		CameraUtil.setTarget(me);
		
		me.inventory.setCurrentItem(0);

		game.setControlsEnabled(true);
		ui.setVisibility(true);
		setWidgetVisibility("use", true);
		setWidgetVisibility("stats_health", true);
		
		vanessa.lookAt(null);
		
		brain.setTarget(36, 50);
		brain.setSpeed(2);
				
		brain.onCompleted(new Runnable() { run() {
			vanessa.setBrain(null);
			vanessa.die(true);

			brain = null;
			vanessa = null;
		}});

		didIntroEnded = true;
	}});
}

void foxyCutscene() {
	ui.setVisibility(false);
	game.setControlsEnabled(false);

	game.setTimer(new Runnable() { run() {
		CameraUtil.setCameraMoveSpeed(.01f);
		CameraUtil.setTarget(foxy.entity);
		CameraUtil.setCameraZoom(0.45f, .04f);

		didFoxyCutsceneEnded = true;
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
	
	//phoneSound.stop();
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
			//phoneSound.stop();
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
		var light = createLight("cone", 16);
		light.setPosition(39, 54);
		light.setDistance(22);
		light.setConeDegree(17.5f);
		light.setDirection(-110);
		light.setColor(0.25f, 0.25f, 0.5f, .75f);
		light.setXray(true);
		light.setStaticLight(true);
		
		var stageLight = createLight("cone");
		stageLight.setColor(0.25f, 0.25f, 0.5f, .5f);
		stageLight.setConeDegree(40);
		stageLight.setDistance(15);
		stageLight.setPosition(19, 27);
		stageLight.setDirection(-75);

		var me = core.settings.mainPlayer;
		var fade = ui.createFade(1);
		game.setPlayerPosition(22, -14);

		me.setDamagedListener(new DamagedListener() { damaged(attacker, damage) {
			setWidgetVisibility("stats_health", true);
		}});
		
		lightLeft.pointLight.setActive(false);
		lightRight.pointLight.setActive(false);

		if(nightId > 1) {
			me.giveItem(entities.createItem("$a7739b9c-e7df-11ed-a05b-0242ac120003/src/items/pistol"));
			me.giveItem(entities.createItem("items/flashlight"));

			setWidgetVisibility("inventory", true);
		}

		game.setControlsEnabled(true);
		ui.setVisibility(true);

		if(nightId == 1) {
			me.wasPower = new Vector2(-1, 0);
			setCameraZoom(.1f, 1);
			setCameraOffset(-1, .2f);

			setCameraPosition(36, 46);
			game.setPlayerPosition(36, 46);

			setWidgetVisibility("inventory", false);
			setWidgetVisibility("use", false);
			setWidgetVisibility("joystick", false);
			setWidgetVisibility("dash", false);
			setWidgetVisibility("stats_health", false);
			setWidgetVisibility("aim", false);

			vanessa.stats.maxHealth = 9999999;
			vanessa.stats.health = 9999999;

			vanessa.giveItem(entities.createItem("items/flashlight"));
			vanessa.giveItem(entities.createItem("$a7739b9c-e7df-11ed-a05b-0242ac120003/src/items/pistol"));

			me.lookAt(vanessa);
			vanessa.lookAt(me);
			me.skin.setAnimationForce("damage");

			var vanessaBrain = new BotFollower();
			vanessaBrain.setWaypoints(waypoints);
			vanessa.setBrain(vanessaBrain);
			vanessaBrain.start();

			game.setTimer(new Runnable() { run() {
				fade.start(1, 0, 10);

				game.setTimer(new Runnable() { run() {
					setCameraZoom(.5f, .15f);
					setCameraOffset(0, 0);
					me.dash(1, 0, 1);
				}}, .75f);

				subtitles = createSubtitles();
				subtitles.setFadeDuration(.25f);
				
				//vanessa.skin.setAnimationForce("talk");
				subtitles.addLine("Hello!?", .4f, .8f);
				//game.setTimer(new Runnable() { run() { vanessa.skin.setAnimationForce(null); }}, .5f);

				subtitles.addLine("Oh my god, a human!", 1, .8f, new Runnable() { run() {
				    vanessa.lookAt(null);
					vanessaBrain.setSpeed(2.5f);
				    vanessaBrain.setTarget(35, 48);
				}});
				
				subtitles.addLine("Hey there, welcome to Freddy Fazbear's!", 2.5f, new Runnable() { run() {
					vanessaBrain.setTarget(37, 46);
					vanessaBrain.setSpeed(2);
					vanessa.lookAt(me);

					me.skin.setAnimationForce(null);
					me.lookAt(null);
					me.wasPower = new Vector2(-1, 0);
					
					CameraUtil.setCameraMoveSpeed(.01f);
					CameraUtil.setTarget(vanessa);
				}});
				
				//subtitles.addLine("Sorry about the cat, she's been my only company for a while now.", 3.5f);
				
				subtitles.addLine("Sorry if I'm too intrusive, I just haven't seen anyone for a while.", 3.5f, new Runnable() { run() {
				    vanessa.lookAt(null);
				    
				    vanessaBrain.setTarget(35, 45);
				    vanessaBrain.onCompleted(new Runnable() { run() {
				        vanessa.lookAt(me);
				    }});
				}});

				subtitles.addLine("Take this and follow me.", 1.5f, new Runnable() { run() {
				    game.setTimer(new Runnable() { run() {
				        vanessaBrain.setTarget(28, 22);
					    vanessaBrain.setSpeed(.6f);
				    	vanessa.lookAt(bonnie.entity);
				    	
				    	CameraUtil.reset();
				    }}, 1.5f);
				    
					setCameraZoom(.4f, .1f);
					setWidgetVisibility("dash", true);
					
					me.giveItem(entities.createItem("items/flashlight"));
					setWidgetVisibility("aim", true);
					setWidgetVisibility("joystick", true);
					
					CameraUtil.setTarget(me);
					
					new Trigger(27, 28, 4, new TriggerCallback() { triggered(var character) {
					    if(character != core.settings.mainPlayer) return false;
					    
					    introAttackCutscene();
					    return true;
					}});

					/*presentationTrigger = new Trigger(22, 22, 5, new TriggerCallback() { triggered(var character) {
						if(character != core.settings.mainPlayer) return false;

						presentationCutscene();
						return true;
					}});*/
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

		/*phoneSound = createMusic("music/phone_" + nightId + ".wav");
		phoneSound.setPosition(24, -14);
		phoneSound.setDistance(12);
		phoneSound.setVolume(0.5f);*/

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
		"music/dark_ambience_4.ogg"
	}, 999);
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

		wakeUp(bonnie.entity, bonnieBrain);
	}}, Math.round(Math.random() * startups[0][0] + startups[0][1]));

	game.setTimer(new Runnable() { run() {
		if(chica.entity.brain == chicaBrain) return;

		wakeUp(chica.entity, chicaBrain);
	}}, Math.round(Math.random() * startups[1][0] + startups[1][1]));

	game.setTimer(new Runnable() { run() {
		if(isFreddyActive || freddy.entity.brain == freddyBrain) return;

		wakeUp(freddy.entity, freddyBrain);
		isFreddyActive = true;
	}}, Math.round(Math.random() * startups[2][0] + startups[2][1]));

	game.setTimer(new Runnable() { run() {
		if(foxy.entity.brain == foxyBrain) return;

		wakeUp(foxy.entity, foxyBrain);
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

	//phoneSound.play();

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