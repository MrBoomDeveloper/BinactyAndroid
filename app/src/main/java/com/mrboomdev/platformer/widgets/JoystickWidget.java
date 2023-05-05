package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

@SuppressWarnings("unchecked")
public class JoystickWidget extends ActorUtil {
    public boolean isActive;
    private Sprite holder, point;
    private int usedPointer = 999;
    private float pointOpacity = .5f;
    private Vector2 offset;
    private UpdateListener listener;
    private GameHolder game = GameHolder.getInstance();

    public JoystickWidget() {
        setTouchable(Touchable.enabled);
        addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(
                            InputEvent event, float x, float y, int pointer, int button) {
                        if (usedPointer == 999) {
                            usedPointer = pointer;
                            updatePointer(new Vector2(x, y));
                            isActive = true;
                        }
                        return true;
                    }

                    @Override
                    public void touchDragged(InputEvent event, float x, float y, int pointer) {
                        if (pointer != usedPointer) return;
                        updatePointer(new Vector2(x, y));
                    }

                    @Override
                    public void touchUp(
                            InputEvent event, float x, float y, int pointer, int button) {
                        if (pointer == usedPointer) {
                            usedPointer = 999;
                            updatePointer(null);
                            isActive = false;
                        }
                    }
                });

        Texture bigCircles = game.assets.get("ui/overlay/big_elements.png");
        holder = new Sprite(bigCircles, 0, 0, 200, 200);
        point = new Sprite(bigCircles, 200, 0, 200, 200);
        holder.setAlpha(.4f);
        point.setAlpha(.5f);
    }

    public <T extends JoystickWidget> T onUpdate(UpdateListener listener) {
        this.listener = listener;
        return (T) this;
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

    @Override
    public void draw(Batch batch, float alpha) {
        if (listener != null) {
            listener.update(
                    isActive
                            ? offset.cpy().add(point.getX(), point.getY()).scl(.1f)
                            : Vector2.Zero);
        }
        holder.draw(batch);
        point.draw(batch);
    }

    private void updatePointer(Vector2 position) {
        if (position == null) {
            point.setCenter(getX() + getWidth() / 2, getY() + getHeight() / 2);
            return;
        }
		
		position.x = Math.max(Math.min(position.x, getWidth()), 0);
		position.y = Math.max(Math.min(position.y, getHeight()), 0);
        Vector2 screenPosition = localToStageCoordinates(position);
        point.setCenter(screenPosition.x, screenPosition.y);
    }

    @Override
    public <T extends ActorUtil> T addTo(Stage stage) {
        holder.setBounds(getX(), getY(), getWidth(), getHeight());
		point.setSize(getWidth() / 3, getHeight() / 3);
        offset =
                new Vector2(
                        -getX() - getWidth() / 2 + point.getWidth() / 2,
                        -getY() - getHeight() / 2 + point.getHeight() / 2);
        updatePointer(null);
        return super.addTo(stage);
    }

    public interface UpdateListener {
        void update(Vector2 power);
    }
}