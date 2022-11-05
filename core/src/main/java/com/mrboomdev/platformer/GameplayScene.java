package com.mrboomdev.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.render.MapRender;
import com.mrboomdev.platformer.render.PlayerRender;
import com.mrboomdev.platformer.ui.TouchControls;

public class GameplayScene extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch sprites;
    private Music music;
    private Rectangle rect;
    private MapRender map;
    private PlayerRender players;
    private TouchControls controls;
    private int screenWidth, screenHeight;
    
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
        sprites = new SpriteBatch();
        
        rect = new Rectangle();
        
        map = new MapRender(sprites);
        players = new PlayerRender(sprites);
        controls = new TouchControls(sprites);
        Gdx.input.setInputProcessor(controls);
        
        players.add("MrBoomDev");
        players.add("Enemy");
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
        
        sprites.end();
        
        if(Gdx.input.isTouched()) {
            Vector3 pos = new Vector3();
            pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            
            rect.x = pos.x - 64 / 2;
            if(rect.x < 0) {
                rect.x = 0;
             }
             if(rect.x > 800 - 64) {
                 rect.x = 800 - 64;
             }
             
             players.move("MrBoomDev", (int)rect.x, 25);
         }
    }
}


