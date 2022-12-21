package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MapBuilder {
    private Array<Sprite> textures = new Array<>();
    private int[][] tiles;
    
    public MapBuilder(int[][] tiles) {
        this.tiles = tiles;
    }
    
    public void loadTextures(String[] tiles) {
        textures.add(null);
        for(String name : tiles) {
            Sprite sprite = new Sprite(new Texture(Gdx.files.internal("img/tiles/" + name + ".png")));
            textures.add(sprite);
        }
    }
    
    public void build(World world) {
        for(int x = 0; x < tiles.length; x++) {
            for(int y = 0; y < tiles[x].length; y++) {
                int tile = tiles[x][y];
                if(tile != 0) {
                    BodyDef bodyDef = new BodyDef();
                    bodyDef.position.set(y * 4, x * 4);
                    Body body = world.createBody(bodyDef);
                    PolygonShape polygon = new PolygonShape();
                    polygon.setAsBox(2, 2);
                    body.createFixture(polygon, 0);
                    polygon.dispose();
                }
            }
        }
    }
    
    public void render(SpriteBatch batch, Vector2 screenSize) {
        for(int x = 0; x < tiles.length; x++) {
            for(int y = 0; y < tiles[x].length; y++) {
                /*if((x * 4 > screenSize.x || x * 4 < screenSize.x) || 
                    (y * 4 > screenSize.y || y * 4 < screenSize.y)) {
                        System.out.println(x * 4 + " " + y * 4 + " : " + screenSize.x + " " + screenSize.y);
                    break;
                }*/
                
                int tile = tiles[x][y];
                
                Sprite back = textures.get(2);
                back.setAlpha(.3f);
                back.setBounds(y * 4 - 2, x * 4 - 2, 4, 4);
                back.draw(batch);
                
                if(tile != 0) {
                    batch.draw(textures.get(tile), y * 4 - 2, x * 4 - 2, 4, 4);
                }
            }
        }
    }
}