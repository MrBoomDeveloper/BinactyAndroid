package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.Controller;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.PlayerEntity;
import com.mrboomdev.platformer.util.ActorUtil;

public class JoystickWidget extends ActorUtil implements Controller {
    private Sprite holder, point;
    private int usedPointer = 999;
    private Vector2 touchPosition;
    private float pointOpacity = .5f;
    private boolean isActive = false;
    private final int limit = 10;

    public JoystickWidget() {
        setBounds(100, 100, 250, 250);
        setTouchable(Touchable.enabled);
        addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(
                            InputEvent event, float x, float y, int pointer, int btn) {
                        if (usedPointer == 999) {
                            usedPointer = pointer;
                            setPosition(new Vector2(x, y), true);
                            isActive = true;
                        }
                        return true;
                    }

                    @Override
                    public void touchDragged(InputEvent event, float x, float y, int pointer) {
                        if (pointer != usedPointer) return;
                        Vector2 position = new Vector2();
                        position.x =
                                (x > getWidth() - limit)
                                        ? (getWidth() - limit)
                                        : (x < limit ? limit : x);

                        position.y =
                                (y > getHeight() - limit)
                                        ? (getHeight() - limit)
                                        : (y < limit ? limit : y);
                        setPosition(position, true);
                    }

                    @Override
                    public void touchUp(
                            InputEvent event, float x, float y, int pointer, int button) {
                        if (pointer == usedPointer) {
                            usedPointer = 999;
                            setPosition(new Vector2(getX(), getY()), false);
                            isActive = false;
                        }
                    }
                });

        AssetManager asset = MainGame.getInstance().asset;
        Texture bigCircles = asset.get("ui/overlay/big_elements.png");
        holder = new Sprite(new TextureRegion(bigCircles, 0, 0, 200, 200));
        point = new Sprite(new TextureRegion(bigCircles, 200, 0, 200, 200));
        point.setSize(75, 75);
        holder.setAlpha(.4f);
        point.setAlpha(.5f);
        setPosition(new Vector2(getX(), getY()), false);
        holder.setBounds(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        if (isActive && pointOpacity < 1) {
            pointOpacity += delta * 1.2f;
        } else if (pointOpacity > .5f) {
            pointOpacity -= delta * 1.2f;
        }
        point.setAlpha(Math.min(1, pointOpacity));
    }

    public Vector2 getPower() {
        // If using keyboard
        Vector2 keyboardMovement = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) keyboardMovement.x = -5;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) keyboardMovement.x = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) keyboardMovement.y = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) keyboardMovement.y = -5;
        if (keyboardMovement.x != 0 && keyboardMovement.y != 0) return keyboardMovement;

        // If using touch controls
        int offset = isActive ? 0 : 25;
        return new Vector2(
                (touchPosition.x - (getWidth() / 2) + offset) / 10,
                (touchPosition.y - (getHeight() / 2) + offset) / 10);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        holder.draw(batch);
        point.draw(batch);
    }

    private void setPosition(Vector2 position, boolean isDown) {
        touchPosition = position;
        int offset = isDown ? 25 : 0;
        point.setCenter(
                (position.x + (getWidth() / 2) - offset),
                (position.y + (getHeight() / 2) - offset));
    }

    @Override
    public ActorUtil connectToEntity(Entity entity) {
        ((PlayerEntity) entity).controller = this;
        return super.connectToEntity(entity);
    }
}
