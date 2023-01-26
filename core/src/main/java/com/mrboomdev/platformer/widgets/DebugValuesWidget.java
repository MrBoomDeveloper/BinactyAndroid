package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import java.util.TreeMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.platformer.util.ActorUtil;
import java.util.Map.Entry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class DebugValuesWidget extends ActorUtil {
    private BitmapFont font;
    private TreeMap<String, String> values = new TreeMap<>();
    
    public DebugValuesWidget() {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/roboto-medium.ttf"));
        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = 14;
        fontParameter.color = Color.WHITE;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.borderWidth = 1;
        font = fontGenerator.generateFont(fontParameter);
    }
    
    public void setValue(String key, String value) {
        values.put(key, value);
    }

  @Override
  public void act(float delta) {
    super.act(delta);
    setValue("Fps", String.valueOf(Gdx.graphics.getFramesPerSecond()));
    setValue("Delta", String.valueOf(delta));
    Vector2 pos = connectedEntity.body.getPosition();
    setValue("MainEntityPositionX", String.valueOf(pos.x / 2));
    setValue("MainEntityPositionY", String.valueOf(pos.y / 2));
    setValue("MainEntityHealth", String.valueOf(connectedEntity.stats.health));
    setValue("MainEntityAnimation", connectedEntity.configNew.animation.current);
  }

  @Override
  public void draw(Batch batch, float opacity) {
    super.draw(batch, opacity);
    StringBuilder builder = new StringBuilder();
    for(Entry entry : values.entrySet()) {
        builder.append(entry.getKey() + ": ");
        builder.append(entry.getValue() + "\n");
    }
    font.draw(batch, builder.toString(), 25, Gdx.graphics.getHeight() - 25);
  }
}