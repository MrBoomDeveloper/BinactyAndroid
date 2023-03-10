package com.mrboomdev.platformer.environment.editor.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mrboomdev.platformer.util.ActorUtil;
import java.util.LinkedList;
import javax.microedition.khronos.opengles.GL10;

public class GridWidget extends ActorUtil {
    public LinkedList<ActorUtil> widgets = new LinkedList<>();
    private boolean isScrollable = false;
    private float gap;
	private float currentScroll, maxScroll;
	private float r, g, b, a = 0;
	private int rows, columns;
	private boolean isHorizontal = true;
	private ShapeRenderer shape;

    public GridWidget(float gap, boolean isHorizontal) {
        this.gap = gap;
		this.isHorizontal = isHorizontal;
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
	
	public GridWidget setCount(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		return this;
	}
	
	public GridWidget setBackground(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

    public GridWidget add(ActorUtil... widgets) {
		for(var widget : widgets) {
			this.widgets.addLast(widget);
		}
        return this;
    }

    @Override
    public void act(float delta) {
		int lastColumn = 0, lastRow = 0;
		
		for(int i = 0; i < widgets.size(); i++) {
			var widget = widgets.get(i);
			float x = 0, y = 0;
			
			if(isHorizontal) {
				x = widget.getWidth() * lastColumn + gap * lastColumn;
				y = widget.getHeight() * lastRow + gap * lastRow;
				
				if(lastColumn < (columns - 1)) {
					lastColumn++;
				} else {
					lastColumn = 0;
					lastRow++;
				}
			} else {
				throw new RuntimeException("Vertical GridWidget isn't supported yet.");
			}
			
			widget.setPosition(getX() + x, getY() + getHeight() - widget.getHeight() - y);
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