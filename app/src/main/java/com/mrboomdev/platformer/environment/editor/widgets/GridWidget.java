package com.mrboomdev.platformer.environment.editor.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mrboomdev.platformer.util.ActorUtil;
import java.util.LinkedList;
import javax.microedition.khronos.opengles.GL10;

public class GridWidget extends ActorUtil implements ActorUtil.Scrollable {
    public LinkedList<ActorUtil> widgets = new LinkedList<>();
    private boolean isScrollable = false;
    private float gap;
	private float scrollX, scrollY, maxScroll;
	private float lastX, lastY;
	private float r, g, b, a = 0;
	private int rows, columns;
	private boolean isHorizontal = true;
	private ShapeRenderer shape;

    public GridWidget(float gap, boolean isHorizontal) {
        this.gap = gap;
		this.isHorizontal = isHorizontal;
		this.shape = new ShapeRenderer();
		this.connectToScroller(this);
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
	public void startScroll(float x, float y) {
		if(!isScrollable) return;
		lastX = x;
		lastY = y;
	}
	
	@Override
	public void handleScroll(float x, float y) {
		if(!isHorizontal) scrollX = Math.max(scrollX + x - lastX, 0);
		if(isHorizontal) scrollY = Math.max(scrollY + y - lastY, 0);
					
		lastX = x;
		lastY = y;
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
			
			widget.setPosition(getX() + x + scrollX, getY() + getHeight() - widget.getHeight() - y + scrollY);
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