map.findById("switch1").connectTo("door1");
map.findById("switch2").connectTo("door2");

screen.fade({
	from: 0,
	to: 1,
	speed: 1
});

screen.title({
	text: "SURVIVE THE NIGHT",
	duration: 3,
	fadeSpeed: 1
});

game.timer({
	start: 6000,
	format: "mm:ss",
	speed: 1.4,
	direction: "FORWARD",
	onEnd: () => {
		game.over(true);
	}
});

players.onDie = (player) => {
	if(player.isMain()) {
		game.over(false);
	}
}