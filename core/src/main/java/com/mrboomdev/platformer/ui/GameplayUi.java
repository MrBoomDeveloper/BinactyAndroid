package com.mrboomdev.platformer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameplayUi {
	private Stage stage;
	private Table table;
	
	public GameplayUi() {
		stage = new Stage();
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		table.setDebug(true);
		
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/roboto-medium.ttf"));
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
		fontParameter.size = 12;
		BitmapFont buttonFont = fontGenerator.generateFont(fontParameter);
		fontGenerator.dispose();
		
		Texture buttonTexture = new Texture(Gdx.files.internal("img/ui/button.png"));
		TextureRegion buttonUp = new TextureRegion(buttonTexture, 0, 0, 50, 50);
		TextureRegion buttonDown = new TextureRegion(buttonTexture, 50, 0, 100, 50);
		
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(buttonUp);
		buttonStyle.down = new TextureRegionDrawable(buttonDown);
		buttonStyle.font = buttonFont;
		TextButton button = new TextButton("Hello, World!", buttonStyle);
		table.add(button);
	}
	
	public void render() {
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	public void dispose() {
		stage.dispose();
	}
}