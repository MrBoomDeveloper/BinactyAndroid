package com.mrboomdev.platformer.ui.gameplay.layout;

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
		shape.begin(ShapeRenderer.ShapeType.Line); {
			shape.setColor(1, 1, 1, 1);
			int i = connectedEntity.inventory.current;
			shape.rect((getX() + i * 80) - (3 * 80), getY(), 75, 75);
		} shape.end();
		batch.begin();
	}

    @Override
    public <T extends ActorUtil> T addTo(Stage stage) {
		for(int i = 0; i < 6; i++) {
			final int id = i;
			buttons.put(i, new ButtonWidget(ButtonWidget.Style.CARD)
				.setBackgroundImage(new Sprite(new Texture(FileUtil.internal("packs/fnaf/icon.jpg").getFileHandle())))
				.onClick(() -> {
					connectedEntity.inventory.current = id;
				})
				.toSize(75, 75)
				.toPosition((getX() + i * 80) - (3 * 80), getY())
				.addTo(stage));
		}
        return super.addTo(stage);
    }
}