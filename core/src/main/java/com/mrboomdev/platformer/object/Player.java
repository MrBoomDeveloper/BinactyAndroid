package com.mrboomdev.platformer.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class Player {
    private BitmapFont font;
    public String nick;
    public Texture texture;
    public int x = 0;
    public int y = 0;
    public Body body;
    private Sprite sprite;
    
    public Player(String nick, Sprite sprite, Body body) {
        this.nick = nick;
        this.body = body;
        this.sprite = sprite;
        this.texture = new Texture(Gdx.files.internal("img/player/player.jpg"));
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }
    
    public void render(SpriteBatch batch) {
        //batch.draw(texture, x, y, 100, 100);
        body.applyTorque(0, true);
        sprite.setPosition(body.getPosition().x - sprite.getWidth()/2, body.getPosition().y-sprite.getHeight()/2);
		sprite.draw(batch);
		
        //batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        font.draw(batch, nick, sprite.getX(), sprite.getY() + 100, 100, 1, true);
    }
}
