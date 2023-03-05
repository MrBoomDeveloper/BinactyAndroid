package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.Direction;
import javax.microedition.khronos.opengles.GL10;

public class FadeWidget extends ActorUtil {
	public float opacity = 0, speed = 1, to;
	private ShapeRenderer shapeRenderer;
	private int direction;
	
	public FadeWidget(float initial) {
		shapeRenderer = new ShapeRenderer();
		opacity = initial;
	}
	
	public FadeWidget start(float from, float to, float speed) {
		this.opacity = from;
		this.direction = from > to ? -1 : 1;
		this.to = to;
		this.speed = speed;
		return this;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		float delta = Gdx.graphics.getDeltaTime();
		if(direction > 0) opacity = Math.min(to, opacity + delta * speed);
		if(direction < 0) opacity = Math.max(to, opacity - delta * speed);
		
		batch.end();
		Gdx.gl.glEnable(GL10.GL_BLEND);
		{
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        	shapeRenderer.setColor(0, 0, 0, opacity);
      	  shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
     	   shapeRenderer.end();
		}
		Gdx.gl.glDisable(GL10.GL_BLEND);
		batch.begin();
	}
}