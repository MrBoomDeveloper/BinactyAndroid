package com.mrboomdev.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.render.MapRender;

public class GameplayScene extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch sprites;
	private Texture player;
    private Texture enemy;
    private Music music;
    private Rectangle rect;
    private Array<Rectangle> players;
    private MapRender map;
    
    @Override
    public void create() {
        player = new Texture(Gdx.files.internal("img/player/player.jpg"));
        enemy = new Texture(Gdx.files.internal("img/player/enemy.jpg"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music/test.mp3"));
        
        music.setLooping(true);
        music.play();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        sprites = new SpriteBatch();
        rect = new Rectangle();
        rect.x = 800 / 2 - 64 / 2;
        rect.y = 20;
        rect.width = 64;
        rect.height = 64;
        map = new MapRender(sprites);
    }
    
    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0, 1);
        camera.update();
        sprites.setProjectionMatrix(camera.combined);
        sprites.begin();
        map.renderTextures();
        sprites.draw(player, rect.x, rect.y);
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
         }
    }
}


