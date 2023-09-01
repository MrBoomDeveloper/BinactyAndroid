package com.mrboomdev.platformer.widgets;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
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
    private final Sprite holder, point;
    private int usedPointer = 999;
    private float pointOpacity = .3f;
    private Vector2 offset;
    private UpdateListener listener, useListener;

    public JoystickWidget() {
        super();
        setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(usedPointer == 999) {
                    usedPointer = pointer;
                    updatePointer(new Vector2(x, y));
                    isActive = true;
                }
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(pointer != usedPointer) return;
                updatePointer(new Vector2(x, y));
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(pointer == usedPointer) {
                    if(useListener != null) {
                        useListener.update(getPower());
                    }

                    usedPointer = 999;
                    updatePointer(null);
                    isActive = false;
                }
            }
        });

        GameHolder game = GameHolder.getInstance();
        Texture bigCircles = game.assets.get("ui/overlay/big_elements.png");
        holder = new Sprite(bigCircles, 0, 0, 200, 200);
        point = new Sprite(bigCircles, 200, 0, 200, 200);
    }

    public <T extends JoystickWidget> T onUpdate(UpdateListener listener) {
        this.listener = listener;
        return (T) this;
    }
	
	public <T extends JoystickWidget> T onUse(UpdateListener listener) {
        this.useListener = listener;
        return (T) this;
    }
	
	public Vector2 getPower() {
		return !isActive ? Vector2.Zero
            : offset.cpy().add(point.getX(), point.getY()).scl(.1f);
	}

    @Override
    public void draw(Batch batch, float alpha) {
        super.update();
        var delta = Gdx.graphics.getDeltaTime();
        var opacity = getOpacity();

        if(isActive && pointOpacity < 1) {
            pointOpacity += delta * 1.2f;
        } else if(pointOpacity > .3f) {
            pointOpacity -= delta * 1.2f;
        }

        point.setAlpha(Math.min(1, pointOpacity) * opacity);
        holder.setAlpha(Math.min(.5f, pointOpacity) * opacity);

        if(listener != null) listener.update(getPower());
        holder.draw(batch);
        point.draw(batch);
    }

    private void updatePointer(Vector2 position) {
        if(position == null) {
            point.setCenter(getX() + getWidth() / 2, getY() + getHeight() / 2);
            return;
        }
		
		position.x = Math.max(Math.min(position.x, getWidth()), 0);
		position.y = Math.max(Math.min(position.y, getHeight()), 0);
        Vector2 screenPosition = localToStageCoordinates(position);

        point.setCenter(screenPosition.x, screenPosition.y);
    }

    @Override
    public <T extends ActorUtil> T addTo(@NonNull Stage stage) {
        holder.setBounds(getX(), getY(), getWidth(), getHeight());
        point.setSize(getWidth() / 3, getHeight() / 3);

        offset = new Vector2(
                -getX() - getWidth() / 2 + point.getWidth() / 2,
                -getY() - getHeight() / 2 + point.getHeight() / 2);

        updatePointer(null);
        return super.addTo(stage);
    }

    public interface UpdateListener {
        void update(Vector2 power);
    }
}