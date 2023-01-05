package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.util.ActorUtil;

public class ActionButton extends ActorUtil {
    public static final int size = 125;
  private Sprite sprite;

  public ActionButton(Texture texture) {
    AssetManager asset = MainGame.getInstance().asset;
    sprite = new Sprite(asset.get("ui/buttons/button.png", Texture.class));
    //sprite = new Sprite(new Texture(Gdx.files.internal("ui/buttons/button.png")));
    setSize(size, size);
    sprite.setSize(getWidth(), getHeight());
    sprite.setAlpha(.3f);
    addListener(new ClickListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            sprite.setAlpha(.2f);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            sprite.setAlpha(.3f);
        }
    });
  }
  
  public ActionButton() {
    this(new Texture(Gdx.files.internal("ui/other/blank.png")));
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