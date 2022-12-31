package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

public class PlayerEntity extends Entity {
    public String nick;
    private Controller controller;
    private Sprite boom;
    private float boomAnimation;
    private Sound boomSound;
    private Vector2 was_position;
    private BitmapFont font;

    public PlayerEntity(String nick, World world) {
        super(world);
        this.nick = nick;
        this.boom = new Sprite(new Texture(Gdx.files.internal("effects/boom.png")));
        this.boomSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/boom.mp3"));
        body.setUserData(this);
        was_position = body.getPosition();
        
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/roboto-medium.ttf"));
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = 12;
        fontParam.borderColor = Color.BLACK;
        fontParam.borderWidth = 1;
        fontParam.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParam);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void draw(SpriteBatch batch) {
        if(Math.abs(body.getLinearVelocity().x) > 0) {
            sprite.setSize(was_position.x > body.getPosition().x ? -1 : 1, 1.8f);
        }
        
        was_position = body.getPosition();
    
        /*weapon.setPosition(
            body.getPosition().x,
            body.getPosition().y - .3f);*/
        sprite.setPosition(
            body.getPosition().x - sprite.getWidth() / 2,
            body.getPosition().y - sprite.getHeight() / 2);
        sprite.draw(batch);
        //weapon.draw(batch);
        
        boomAnimation += Gdx.graphics.getDeltaTime();
        if(boomAnimation < 1) {
            boom.setCenter(body.getPosition().x, body.getPosition().y);
            boom.draw(batch);
        }

        if (controller != null) usePower(controller.getPower());
    }
    
    public void drawNick(SpriteBatch batch) {
        font.draw(batch, nick, body.getPosition().x * 40 - 50, body.getPosition().y * 40 + 56, 100, Align.center, true);
    }
    
    public void die() {
        boomSound.play(.2f);
        boom.setSize(2, 2);
        boomAnimation = 0;
    }

    public void usePower(Vector2 power) {
        body.setLinearVelocity(power);
    }
}