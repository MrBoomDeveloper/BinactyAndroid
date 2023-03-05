package com.mrboomdev.platformer.environment.editor.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mrboomdev.platformer.util.ActorUtil;

public class ButtonWidget extends ActorUtil {
    private String text;

    public ButtonWidget(String text) {
        this.text = text;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        
    }
}