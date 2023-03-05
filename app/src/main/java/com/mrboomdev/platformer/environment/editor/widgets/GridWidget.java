package com.mrboomdev.platformer.environment.editor.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mrboomdev.platformer.environment.editor.widgets.GridWidget;
import com.mrboomdev.platformer.util.ActorUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.microedition.khronos.opengles.GL10;

public class GridWidget extends ActorUtil {
    public LinkedList<ActorUtil> widgets = new LinkedList<>();
    private boolean isScrollable = false;
    private float gap;
	private float currentScroll, maxScroll;
	private float r, g, b, a = 0;
	private int rows, columns, lastRow, lastColumn;
	private boolean isHorizontal = false;
	private ShapeRenderer shape;

    public GridWidget(float gap) {
        this.gap = gap;
		this.shape = new ShapeRenderer();
        this.addListener(new InputListener() {
			@Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
				
			}

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				
			}
        });
    }

    public GridWidget setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
        return this;
    }
	
	public GridWidget setBackground(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

    public GridWidget add(ActorUtil widget) {
        widgets.addLast(widget);
        return this;
    }

    @Override
    public void act(float delta) {
		for(int i = 0; i < widgets.size(); i++) {
			widgets.get(i).setPosition(0, 0);
		}
	}

    @Override
    public void draw(Batch batch, float alpha) {
        batch.end();
		Gdx.gl.glEnable(GL10.GL_BLEND);
		{
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.setColor(r, g, b, a);
			shape.rect(getX(), getY(), getWidth(), getHeight());
			shape.end();
		}
		Gdx.gl.glDisable(GL10.GL_BLEND);
		batch.begin();
    }
}