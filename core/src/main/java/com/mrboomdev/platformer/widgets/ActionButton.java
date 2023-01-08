package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.util.ActorUtil;

public class ActionButton extends ActorUtil {
  public static final int size = 115;
  public static final float defaultOpacity = .1f;
  public static final float activeOpacity = .07f;
  private Sprite sprite;

  public ActionButton(Texture texture) {
    AssetManager asset = MainGame.getInstance().asset;
    Texture bigCircles = asset.get("ui/overlay/big_elements.png", Texture.class);
    sprite = new Sprite(new TextureRegion(bigCircles, 400, 0, 200, 200));
    setSize(size, size);
    sprite.setSize(getWidth(), getHeight());
    sprite.setAlpha(defaultOpacity);
    addListener(new ClickListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            sprite.setAlpha(activeOpacity);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            sprite.setAlpha(defaultOpacity);
        }
    });
  }
  
  public ActionButton() {
    this(new Texture(Gdx.files.internal("etc/blank.png")));
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    sprite.setPosition(getX(), getY());
  }

  @Override
  public void draw(Batch batch, float opacity) {
    super.draw(batch, opacity);
    sprite.draw(batch);
  }
}