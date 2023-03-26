function onPrepare() {
	assets.load({
		{ path: "music/dark_ambience_1.ogg", type: "music" },
		{ path: "music/dark_ambience_2.ogg", type: "music" },
		{ path: "music/dark_ambience_3.ogg", type: "music" },
		{ path: "music/dark_ambience_4.ogg", type: "music" }
	});
	
	game.settings.initial = {
		fade: 1
	}
}

function onMapBuild() {
	map.findById("switch1").connectTo("door1");
	map.findById("switch2").connectTo("door2");
}

function onStart() {
	screen.fade({
		from: 0,
		to: 1,
		speed: 0.5,
		duration: 2,
		fromTo: [1, 0]
	});
	
	game.timer({
		start: 360,
		format: "mm:ss",
		speed: 1.4,
		direction: "forward",
		onEnd: () => {
			game.over(true);
		}
	});

	screen.title({
		text: "SURVIVE THE NIGHT",
		duration: 4,
		fadeSpeed: 1
	});

	audio.playMusic({
		random: true,
		mode: "loop_all",
		queue: [
			{ path: "music/dark_ambience_1.ogg", volume: 1 },
			{ path: "music/dark_ambience_2.ogg", volume: 1 },
			{ path: "music/dark_ambience_3.ogg", volume: 1 },
			{ path: "music/dark_ambience_4.ogg", volume: 1 }
		]
	});
}

function onDie(entity) {
	if(entity == game.settings.mainPlayer) {
		audio.clear();
		game.over(false);
	}
}