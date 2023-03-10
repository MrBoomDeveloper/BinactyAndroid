package com.mrboomdev.platformer.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.character.CharacterEntity;

public abstract class ActorUtil extends Actor {
	public CharacterEntity connectedEntity;

	public ActorUtil toPosition(Vector2 position) {
		setPosition(position.x, position.y);
		return this;
	}
	
	public ActorUtil toPosition(float x, float y) {
		setPosition(x, y);
		return this;
	}
	
	public ActorUtil toSize(float width, float height) {
		setSize(width, height);
		return this;
	}
	
	public ActorUtil connectToEntity(CharacterEntity entity) {
		connectedEntity = entity;
		return this;
	}
	
	public ActorUtil addTo(Stage stage) {
		stage.addActor(this);
		return this;
	}
	
	public ActorUtil addTo(Table table) {
		table.add(this);
		return this;
	}
	
	public ActorUtil onClick(onClickListener listener) {
		this.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				listener.clicked();
			}
		});
		return this;
	}

	public interface onClickListener {
		void clicked();
	}
}