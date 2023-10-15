

void setWidgetVisibility(String name, boolean isVisible) {
	var widgets = core.environment.ui.widgets;
	if(!widgets.containsKey(name)) return;

	var widget = widgets.get(name);
	widget.setVisible(isVisible);
}

void setAudioTarget(var target) {
	AudioUtil.setTarget(target);
}

float getDeltaTime() {
	return Gdx.graphics.getDeltaTime();
}

createCharacter(String name) {
	return entities.createCharacter(name).create();
}

SubtitlesLayout createSubtitles() {
	var widget = new SubtitlesLayout();
	core.environment.stage.addActor(widget);
	return widget;
}

FileUtil getSource() {
	return this.__source;
}

createLight(String type) {
	return createLight(type, 8);
}

createLight(String type, int raysCount) {
	return game.createLight(type, raysCount);
}

Audio createMusic(String path) {
	return new Audio(getSource(), path, true);
}

Audio createSound(String path) {
	return new Audio(getSource(), path, false);
}

void setCameraZoom(float size, float speed) {
	CameraUtil.setCameraZoom(size, speed);
}

void setCameraOffset(float x, float y) {
	CameraUtil.setCameraOffsetForce(x, y);
}

void setCameraMoveSpeed(float speed) {
	CameraUtil.setCameraMoveSpeed(speed);
}

void setCameraPosition(float x, float y) {
	CameraUtil.setCameraPosition(x, y);
}