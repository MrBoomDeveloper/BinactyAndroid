package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ActionButton extends Actor {
  private Sprite sprite;

  public ActionButton() {
    sprite = new Sprite(new Texture(Gdx.files.internal("ui/buttons/button.png")));
    setSize(125, 125);
    sprite.setSize(getWidth(), getHeight());
    sprite.setAlpha(.4f);
    addListener(new ClickListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            sprite.setAlpha(.25f);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            super.clicked(event, x ,y);
            sprite.setAlpha(.4f);
        }
    });
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