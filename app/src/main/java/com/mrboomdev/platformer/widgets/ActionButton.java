package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

public class ActionButton extends ActorUtil {
	public static final int size = 115;
	public static final float defaultOpacity = .1f;
	public static final float activeOpacity = .07f;
	public static final float iconDefaultOpacity = .8f;
	public static final float iconActiveOpacity = .5f;
	public boolean isActive = true;
	private final Sprite sprite;
	private final Sprite icon;
	
	public ActionButton(Sprite spriteInput) {
		AssetManager asset = GameHolder.getInstance().assets;
		Texture bigCircles = asset.get("ui/overlay/big_elements.png", Texture.class);
		sprite = new Sprite(new TextureRegion(bigCircles, 400, 0, 200, 200));
		this.setSize(size, size);
		sprite.setSize(size, size);
		icon = new Sprite(spriteInput);
		icon.setSize(size / 1.8f, size / 1.8f);
		
		this.update(false);
		this.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				update(true);
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				update(false);
			}
		});
	}
	
	public ActionButton setActive(boolean isActive) {
		this.isActive = isActive;
		this.setTouchable(isActive ? Touchable.enabled : Touchable.disabled);
		this.update(false);
		return this;
	}
	
	private void update(boolean isPressed) {
		sprite.setAlpha((isPressed ? activeOpacity : defaultOpacity) * (isActive ? 1 : .5f));
		icon.setAlpha((isPressed ? iconActiveOpacity : iconDefaultOpacity) * (isActive ? 1 : .5f));
	}
	
	@Override
	public void act(float delta) {
		sprite.setPosition(getX(), getY());
		icon.setCenter(getX() + (size / 2f), getY() + (size / 2f));
	}

	@Override
	public void draw(Batch batch, float alpha) {
		sprite.draw(batch);
		icon.draw(batch);
	}
}