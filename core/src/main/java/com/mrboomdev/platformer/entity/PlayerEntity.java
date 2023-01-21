package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.util.Direction;

@Deprecated
public class PlayerEntity extends Entity {
    public String nick;
    private Sprite boom;
    private float boomAnimation;
    private Sound boomSound;
    private Vector2 was_position;
    private BitmapFont font;
    private float animationProgress = 0;
    private Direction moveDirection;
    private Direction animationDirection;
    private float speed;
    
    @Deprecated
    public PlayerEntity(String name, String character, World world) {
        this(name, character, world, new Vector2(0, 0));
    }
    
    @Deprecated
    public PlayerEntity(String name, String character, World world, Vector2 position) {
        super(character, world, position);
        this.nick = name;
        this.boom = new Sprite(new Texture(Gdx.files.internal("effects/boom.png")));
        this.boomSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/boom.mp3"));
        body.setUserData(this);
        was_position = body.getPosition();
        moveDirection = new Direction(Direction.NONE);
        animationDirection = new Direction(Direction.FORWARD);
        
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/roboto-medium.ttf"));
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = 12;
        fontParam.borderColor = Color.BLACK;
        fontParam.borderWidth = 1;
        fontParam.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParam);
    }
    
    @Deprecated
    public void animate() {
        speed = Math.max(Math.abs(body.getLinearVelocity().x), Math.abs(body.getLinearVelocity().y));
        if(speed == 0) {
            if(animationProgress > 0) animationProgress -= .01;
            return;
        }
        
        was_position = body.getPosition();
        moveDirection.setFrom(body.getLinearVelocity().x);
        
        animationProgress += (animationDirection.isForward() ? speed : -speed) / 600;
        if(animationDirection.isForward() && animationProgress > .1f) {
            animationDirection.current = Direction.BACKWARD;
        } else if(animationDirection.isBackward() && animationProgress < 0) {
            animationDirection.current = Direction.FORWARD;
        }
    }
    
    @Deprecated
    public void draw(SpriteBatch batch) {
        if(isDead) return;
        boomAnimation += Gdx.graphics.getDeltaTime();
        if(boomAnimation < 1) {
            boom.setCenter(body.getPosition().x, body.getPosition().y);
            boom.draw(batch);
        }
        
        this.animate();
        float limbGap = (speed * 165);
        float limbOffset = speed * 8;
        
        config.bones.arm.draw(batch, body.getPosition().add(
            new Vector2(moveDirection.isForward() ? .27f : -.27f, animationProgress * 2)), 
            -(animationProgress * -(limbGap) + limbOffset), moveDirection.reverse(), false);
            
        config.bones.leg.draw(batch, body.getPosition().add(
            new Vector2(-config.bones.legs_gap, animationProgress * 2)),
            -(animationProgress * -(limbGap) + limbOffset), moveDirection);
            
        config.bones.body.draw(batch, body.getPosition().add(
            new Vector2(0, animationProgress / .8f)), 
            0, moveDirection);
            
        config.bones.leg.draw(batch, body.getPosition().add(
            new Vector2(config.bones.legs_gap, animationProgress * 2)), 
            animationProgress * -(limbGap) + limbOffset, moveDirection);
            
        config.bones.arm.draw(batch, body.getPosition().add(
            new Vector2(moveDirection.isBackward() ? .27f : -.27f, animationProgress * 2)), 
            animationProgress * -(limbGap) + limbOffset, moveDirection, true);
            
        config.bones.head.draw(batch, body.getPosition().add(
            new Vector2(0, animationProgress)),
            0, moveDirection);
            
        if(controller != null) usePower(controller.getPower());
        super.draw(batch);
    }
    
    @Deprecated
    public void drawNick(SpriteBatch batch) {
        if(isDead) return;
        font.draw(batch, nick, body.getPosition().x * 50 - 50, body.getPosition().y * 50 + 62, 100, Align.center, true);
    }
    
    @Deprecated
    @Override
    public void gainDamage(int damage) {
        super.gainDamage(damage);
        boomSound.play(.2f);
        boom.setSize(2, 2);
        boomAnimation = 0;
    }
}