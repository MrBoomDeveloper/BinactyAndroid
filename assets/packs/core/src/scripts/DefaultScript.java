import com.mrboomdev.platformer.entity.Entity.Target;
import com.mrboomdev.platformer.script.bridge.GameBridge.GameListener;
import com.mrboomdev.platformer.script.bridge.EntitiesBridge.EntityListener;
import com.mrboomdev.platformer.script.bridge.UiBridge.UiListener;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.environment.map.tile.TileInteraction.InteractionListener;
import com.mrboomdev.platformer.util.ui.ActorUtil.Align;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.util.io.audio.Audio;
import com.mrboomdev.platformer.util.io.FileUtil;

FileUtil getSource() {
	return this.__source;
}

Audio createMusic(String path) {
	return new Audio(getSource(), path, true);
}

Audio createSound(String path) {
	return new Audio(getSource(), path, false);
}