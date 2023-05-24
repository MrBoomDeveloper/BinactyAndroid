package com.mrboomdev.platformer.ui.gameplay.layout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import com.mrboomdev.platformer.ui.gameplay.widgets.ButtonWidget;
import com.mrboomdev.platformer.util.ActorUtil;

public class InventoryLayout extends ActorUtil {
	private final Array<ButtonWidget> buttons = new Array<>();
	private final ShapeRenderer shape = new ShapeRenderer();

    @Override
    public void draw(Batch batch, float alpha) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shape.begin(ShapeRenderer.ShapeType.Filled); {
			for(int i = 0; i <= 5; i++) {
				shape.set(ShapeRenderer.ShapeType.Filled);
				shape.setColor(.5f, .5f, .5f, .1f);
				shape.rect((getX() + i * 80) - (3 * 80), getY(), 75, 75);
				
				buttons.get(i).setForegroundImage((connectedEntity.inventory.items.size > i) ? connectedEntity.inventory.items.get(i).getSprite() : null);
				
				Gdx.gl.glLineWidth(3);
				shape.setColor(1, 1, 1, connectedEntity.inventory.current == i ? .8f : .1f);
				shape.set(ShapeRenderer.ShapeType.Line);
				shape.rect((getX() + i * 80) - (3 * 80), getY(), 75, 75);
			}
		} shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

    @Override
    public <T extends ActorUtil> T addTo(Stage stage) {
		shape.setAutoShapeType(true);
		for(int i = 0; i < 6; i++) {
			final int id = i;
			var button = new ButtonWidget(ButtonWidget.Style.CARD)
				.toSize(75, 75)
				.toPosition((getX() + i * 80) - (3 * 80), getY())
				.onClick(() -> connectedEntity.inventory.current = id)
				.addTo(stage);
			
			button.setColor(0, 0, 0, .8f);
			buttons.add((ButtonWidget)button);
		}
        return super.addTo(stage);
    }
}