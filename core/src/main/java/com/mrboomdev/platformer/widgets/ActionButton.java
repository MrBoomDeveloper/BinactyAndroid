package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActionButton extends Actor {
    private Sprite sprite;
    
    public ActionButton() {
        this.sprite = new Sprite(new Texture(Gdx.files.internal("img/ui/button.png")));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //sprite.set
    }

    @Override
    public void draw(Batch batch, float opacity) {
        super.draw(batch, opacity);
        sprite.draw(batch);
    }
}