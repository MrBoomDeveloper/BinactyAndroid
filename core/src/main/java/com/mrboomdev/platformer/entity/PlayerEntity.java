package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.scenes.splash.SplashScreen;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.Direction;

public class PlayerEntity extends Entity {
    public Camera camera;
    public String nick;
    private Sprite boom;
    private float boomAnimation;
    private Sound boomSound;
    private BitmapFont font;
    private float animationProgress = 0;
    private Direction moveDirection;
    private Direction animationDirection;
    private float speed;

    public PlayerEntity(String name, String character, World world) {
        this(name, character, world, new Vector2(0, 0));
    }

    public PlayerEntity(String name, String character, World world, Vector2 position) {
        super(character, world, position);
        this.nick = name;
        this.boom = new Sprite(new Texture(Gdx.files.internal("effects/boom.png")));
        this.boomSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/boom.mp3"));
        body.setUserData(this);
        moveDirection = new Direction(Direction.NONE);
        animationDirection = new Direction(Direction.FORWARD);

        FreeTypeFontGenerator fontGenerator =
                new FreeTypeFontGenerator(Gdx.files.internal("font/roboto-medium.ttf"));
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = 12;
        fontParam.borderColor = Color.BLACK;
        fontParam.borderWidth = 1;
        fontParam.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParam);
    }

    @Deprecated
    public void animate() {
        speed = Math.max(
        	Math.abs(body.getLinearVelocity().x), 
			Math.abs(body.getLinearVelocity().y));
        if (speed == 0) {
            if (animationProgress > 0) animationProgress -= .01;
            return;
        }

        moveDirection.setFrom(body.getLinearVelocity().x);

        animationProgress += (animationDirection.isForward() ? speed : -speed) / 600;
        if (animationDirection.isForward() && animationProgress > .1f) {
            animationDirection.current = Direction.BACKWARD;
        } else if (animationDirection.isBackward() && animationProgress < 0) {
            animationDirection.current = Direction.FORWARD;
        }
    }

    public void draw(SpriteBatch batch) {
		super.draw(batch);
        if (isDead) return;
        if (controller != null) usePower(controller.getPower());

        boomAnimation += Gdx.graphics.getDeltaTime();
        if (boomAnimation < 1) {
            boom.setCenter(body.getPosition().x, body.getPosition().y);
            boom.draw(batch);
        }

        this.animate();

        if (MainGame.getInstance().newCharacterAnimations) return;

        float limbGap = (speed * 165);
        float limbOffset = speed * 8;

        config.bones.arm.draw(
                batch,
                body.getPosition()
                        .add(
                                new Vector2(
                                        moveDirection.isForward() ? .27f : -.27f,
                                        animationProgress * 2)),
                -(animationProgress * -(limbGap) + limbOffset),
                moveDirection.reverse(),
                false);

        config.bones.leg.draw(
                batch,
                body.getPosition().add(new Vector2(-config.bones.legs_gap, animationProgress * 2)),
                -(animationProgress * -(limbGap) + limbOffset),
                moveDirection);

        config.bones.body.draw(
                batch,
                body.getPosition().add(new Vector2(0, animationProgress / .8f)),
                0,
                moveDirection);

        config.bones.leg.draw(
                batch,
                body.getPosition().add(new Vector2(config.bones.legs_gap, animationProgress * 2)),
                animationProgress * -(limbGap) + limbOffset,
                moveDirection);

        config.bones.arm.draw(batch,
                body.getPosition().add(new Vector2(
                	moveDirection.isBackward() ? .27f : -.27f,
                    animationProgress * 2)),
                animationProgress * -(limbGap) + limbOffset,
                moveDirection, true);

        config.bones.head.draw(
                batch, body.getPosition().add(new Vector2(0, animationProgress)), 0, moveDirection);
    }

    @Deprecated
    public void drawNick(SpriteBatch batch) {
        if (isDead) return;
        font.draw(batch, nick, body.getPosition().x * 50 - 50, body.getPosition().y * 50 + 62, 100, Align.center, true);
    }
	
	public void drawNick(SpriteBatch batch, Camera camera) {
		if (isDead) return;
		Vector2 proportion = new Vector2(
			(Gdx.graphics.getWidth() / camera.viewportWidth) / 2,
			(Gdx.graphics.getHeight() / camera.viewportHeight) / 2
		);
		font.getData().setScale(.8f, .8f);
        font.draw(batch, nick,
			body.getPosition().x * proportion.x,
			body.getPosition().y * proportion.y,
		100, Align.center, true);
	}

    @Override
    public void gainDamage(int damage) {
        super.gainDamage(damage);
        boomSound.play(.2f);
        boom.setSize(2, 2);
        boomAnimation = 0;
        if (camera != null) CameraUtil.setCameraShake(.2f, .5f);
    }

    @Override
    public void die() {
        super.die();
        MainGame game = MainGame.getInstance();
        if(nick == game.nick) {
            game.toggleGameView(false);
        }
    }
}