package com.mrboomdev.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.render.MapRender;
import com.mrboomdev.platformer.render.PlayerRender;
import com.mrboomdev.platformer.ui.TouchControls;

public class GameplayScene extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch sprites;
    private Music music;
    private MapRender map;
    private PlayerRender players;
    private TouchControls controls;
    private int screenWidth, screenHeight;
    private String myNick;
    private BitmapFont font;
    
    public GameplayScene(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }
    
    @Override
    public void create() {
        /*music = Gdx.audio.newMusic(Gdx.files.internal("music/test.mp3"));
        music.setLooping(true);
        music.play();*/
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        //camera.position.set(0, 0, 0);
        sprites = new SpriteBatch();
        font = new BitmapFont();
        
        map = new MapRender(sprites);
        players = new PlayerRender(sprites);
        controls = new TouchControls(sprites);
        Gdx.input.setInputProcessor(controls);
        
        myNick = "MrBoomDev";
        players.add(myNick);
        players.add("Nikita");
    }
    
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        sprites.setProjectionMatrix(camera.combined);
        sprites.begin();
        
        map.render();
        players.render();
        controls.render();
        font.setColor(Color.WHITE);
        font.draw(sprites, getDebugValues(), 0, screenHeight - 25, 150, 0, true);
        
        sprites.end();
        
        players.moveBy(myNick, (int)controls.joystick.powerX, (int)controls.joystick.powerY);
    }
    
    public String getDebugValues() {
        String result = "FPS: " + Gdx.graphics.getFramesPerSecond();
        result += "\n" + controls.joystick.getDebugValues();
        return result;
    }
    
    @Override
    public void dispose() {
        controls.joystick.dispose();
        font.dispose();
    }
}


