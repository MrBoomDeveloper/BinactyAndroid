import com.mrboomdev.platformer.entity.Entity.Target;
import com.mrboomdev.platformer.script.bridge.GameBridge.GameListener;
import com.mrboomdev.platformer.script.bridge.EntitiesBridge.EntityListener;
import com.mrboomdev.platformer.script.bridge.UiBridge.UiListener;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.entity.character.CharacterEntity.DamagedListener;
import com.mrboomdev.platformer.environment.map.tile.TileInteraction.InteractionListener;
import com.mrboomdev.platformer.util.ui.ActorUtil.Align;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.util.io.audio.Audio;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.environment.logic.Trigger;
import com.mrboomdev.platformer.environment.logic.Trigger.TriggerCallback;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.entity.bot.BotFollower;
import com.mrboomdev.platformer.ui.gameplay.layout.SubtitlesLayout;
import com.mrboomdev.platformer.game.pack.PackLoader;

void setWidgetVisibility(String name, boolean isVisible) {
	var widgets = core.environment.ui.widgets;
	if(!widgets.containsKey(name)) return;

	var widget = widgets.get(name);
	widget.setVisible(isVisible);
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