package com.mrboomdev.platformer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameplayUi {
	private Stage stage;
	private Table table;
	
	public GameplayUi() {
		Skin skin = new Skin(Gdx.files.internal("components/uiskin.json"));
		
		stage = new Stage();
		table = new Table();
		table.setFillParent(true);
		
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/roboto-medium.ttf"));
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
		fontParameter.size = 12;
		BitmapFont buttonFont = fontGenerator.generateFont(fontParameter);
		fontGenerator.dispose();
		
		TextButton button = new TextButton("Hello, World!", skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
			}
		});
		
		button.pad(10);
		table.add(button);
        
        Skin skin1 = new Skin(Gdx.files.internal("components/skin.json"));
        TextButton button1 = new TextButton("I love you", skin1, "default");
        table.add(button1);
		
		table.setDebug(true);
		stage.addActor(table);
	}
	
	public void render() {
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	public void dispose() {
		stage.dispose();
	}
}