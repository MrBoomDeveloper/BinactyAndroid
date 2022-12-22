package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.text.SimpleDateFormat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.TextWidget;

public class GameplayUi implements CoreUi {
	public Stage stage; 
    public JoystickWidget joystick;
    private float time = 360000;
    private TextWidget timer;
    public DebugValuesWidget debugValues; 
	
	public GameplayUi() {
		stage = new Stage();
        debugValues = new DebugValuesWidget();
        stage.addActor(debugValues);
        
        ActionButton screenshot = new ActionButton();
        screenshot.setPosition(Gdx.graphics.getWidth() - screenshot.getWidth() - 50,
            Gdx.graphics.getHeight() - screenshot.getHeight() - 50);
        screenshot.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.exit(0);
            }
        });
        stage.addActor(screenshot);
        
        ActionButton pause = new ActionButton();
        pause.setPosition(Gdx.graphics.getWidth() - (screenshot.getWidth() * 2) - 50 - 35,
            Gdx.graphics.getHeight() - screenshot.getHeight() - 50);
        pause.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.exit(0);
            }
        });
        stage.addActor(pause);
        
		Table table = new Table();
        table.setFillParent(true);
        table.right().bottom().pad(50);
        
        table.add(new Actor()).pad(20);
        ActionButton button = new ActionButton();
        table.add(button).pad(20);
        table.row();
        ActionButton button1 = new ActionButton();
        table.add(button1).pad(20);
        ActionButton button2 = new ActionButton();
        table.add(button2).pad(20);
        
        joystick = new JoystickWidget();
        stage.addActor(joystick);
        
        Table table1 = new Table();
        table.setFillParent(true);
        table1.right().bottom();
        ActionButton button3 = new ActionButton();
        table1.add(button3);
        stage.addActor(table1);
        
        timer = new TextWidget(Gdx.files.internal("font/gilroy-bold.ttf"), 60);
        timer.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 75);
        stage.addActor(timer);
        
		stage.addActor(table);
        stage.setDebugAll(true);
	}
	
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
        time -= delta * 1000;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        timer.setText(dateFormat.format((int)time));
        debugValues.setValue("TimeLeft", String.valueOf(time));
        
        if(time <= 0) {
            System.exit(0);
        }
	}
	
	public void dispose() {
		stage.dispose();
	}
}