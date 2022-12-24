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
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.data.MapTiles;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;
import java.util.Map;

public class MapBuilder {
    private Array<Block> blocks = new Array<>();
    private Sprite shadow;
    private MapTiles mapTiles;
    private final int tileSize = 2;
    
    public MapBuilder(MapTiles tiles) {
        this.mapTiles = tiles;
        this.shadow = new Sprite(new Texture(Gdx.files.internal("world/blocks/shadow.png")));
        this.shadow.setSize(tiles.background[0].length * 2, 2);
        this.shadow.setPosition(-1, -1);
        this.shadow.setAlpha(.9f);
    }
    
    public void loadBlocks(String[] load, Map<String, Block> blocks) {
        for(String name : load) {
            this.blocks.add(blocks.get(name).init());
        }
    }
    
    public void build(World world) {
        for(int x = mapTiles.foreground.length - 1; x >= 0; x--) {
            for(int y = 0; y < mapTiles.foreground[x].length; y++) {
                buildBlock(blocks.get(mapTiles.foreground[x][y]), new Vector2(x * 2, y * 2), world);
            }
        }
    }
    
    public void buildBlock(Block block, Vector2 position, World world) {
        if(block.colission) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(position.y, position.x + ((block.height - tileSize) / 2) + .4f);
            Body body = world.createBody(bodyDef);
            PolygonShape polygon = new PolygonShape();
            polygon.setAsBox(block.width / 2, block.height / 2 - .4f);
            body.createFixture(polygon, 0);
            polygon.dispose();
        }
    }
    
    public void render(MapLayer layer, SpriteBatch batch, Bounds cameraBounds) {
        int[][] tiles = (layer == MapLayer.FOREGROUND
            ? mapTiles.foreground
            : mapTiles.background);
            
        for(int y = tiles.length - 1; y >= 0; y--) {
            for(int x = 0; x < tiles[y].length; x++) {
                renderBlock(blocks.get(tiles[y][x]),
                    new Vector2(x * tileSize, y * tileSize),
                    cameraBounds, batch);
            }
        }
        
        if(layer == MapLayer.FOREGROUND) shadow.draw(batch);
    }
    
    public void renderBlock(Block block, Vector2 position, Bounds bounds, SpriteBatch batch) {
        if(position.x - (block.width / 2) > bounds.toX ||
            position.x + (block.width / 2) < bounds.fromX ||
            position.y - (block.height / 2) > bounds.toY ||
            position.y + block.height < bounds.fromY) {
                return;
        }
        
        if(block.sprite != null) {
            batch.draw(block.sprite, position.x - (tileSize / 2) + block.offsetX, 
                position.y - (tileSize / 2) + block.offsetY, 
                block.width, block.height);
        }
    }
}