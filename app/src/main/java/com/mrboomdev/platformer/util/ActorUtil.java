package com.mrboomdev.platformer.util;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;

@SuppressWarnings("unchecked")
public abstract class ActorUtil extends Actor {
	public CharacterEntity connectedEntity;
	private float opacity = 1, masterOpacity;

	public void update() {
		var game = GameHolder.getInstance();

		if(game.settings.enableEditor) {
			masterOpacity = 1;
			return;
		}

		if(game.settings.isUiVisible && masterOpacity < 1) {
			masterOpacity += (1 - masterOpacity) * .05f;
		} else if(masterOpacity > 0) {
			masterOpacity += (0 - masterOpacity) * .05f;
		}
	}

	public float getOpacity() {
		return opacity * masterOpacity;
	}

	public <T extends ActorUtil> T toOpacity(float opacity) {
		this.opacity = opacity;
		return (T)this;
	}

	public ActorUtil toPosition(@NonNull Vector2 position) {
		setPosition(position.x, position.y);
		return this;
	}
	
	public <T extends ActorUtil> T toPosition(float x, float y) {
		setPosition(x, y);
		return (T)this;
	}
	
	public <T extends ActorUtil> T toSize(float width, float height) {
		setSize(width, height);
		return (T)this;
	}
	
	public <T extends ActorUtil> T connectToScroller(Scrollable scroller) {
		this.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				scroller.startScroll(x, y);
				return true;
			}
				
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				scroller.handleScroll(x, y);
			}
		});
		return (T)this;
	}
	
	public <T extends ActorUtil> T connectToEntity(CharacterEntity entity) {
		connectedEntity = entity;
		return (T)this;
	}
	
	public <T extends ActorUtil> T addTo(@NonNull Stage stage) {
		stage.addActor(this);
		return (T)this;
	}
	
	public <T extends ActorUtil> T addTo(@NonNull Table table) {
		table.add(this);
		return (T)this;
	}
	
	public <T extends ActorUtil> T onClick(onClickListener listener) {
		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.clicked();
			}
		});
		return (T)this;
	}

	public interface onClickListener {
		void clicked();
	}
	
	public interface Scrollable {
		void startScroll(float x, float y);
		void handleScroll(float x, float y);
	}
}