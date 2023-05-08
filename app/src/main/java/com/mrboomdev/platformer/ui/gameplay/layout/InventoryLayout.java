package com.mrboomdev.platformer.ui.gameplay.layout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.ui.gameplay.widgets.ButtonWidget;
import com.mrboomdev.platformer.util.ActorUtil;
import com.mrboomdev.platformer.util.io.FileUtil;

public class InventoryLayout extends ActorUtil {
	private ObjectMap<Integer, ButtonWidget> buttons = new ObjectMap<>();
	private ShapeRenderer shape = new ShapeRenderer();

    @Override
    public void draw(Batch batch, float alpha) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shape.begin(ShapeRenderer.ShapeType.Filled); {
			shape.setColor(.8f, .8f, .8f, .1f);
			for(int i = 0; i <= 5; i++) {
				shape.rect((getX() + i * 80) - (3 * 80), getY(), 75, 75);
			}
			
			Gdx.gl.glLineWidth(3);
			shape.setColor(1, 1, 1, .8f);
			shape.set(ShapeRenderer.ShapeType.Line);
			shape.rect((getX() + connectedEntity.inventory.current * 80) - (3 * 80), getY(), 75, 75);
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
				//.setBackgroundImage(new Sprite(new Texture(FileUtil.internal("packs/fnaf/icon.jpg").getFileHandle())))
				.toSize(75, 75)
				.toPosition((getX() + i * 80) - (3 * 80), getY())
				.onClick(() -> {
					connectedEntity.inventory.current = id;
				}).addTo(stage);
			
			button.setColor(0, 0, 0, 0);
			buttons.put(i, (ButtonWidget)button);
		}
        return super.addTo(stage);
    }
}