package com.mrboomdev.platformer.environment;

import java.util.Map;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;

public class MapBuilder {
    private Array<Block> blocks = new Array<>();
    private int[][] tiles;
    private final int tileSize = 2;
    
    public MapBuilder(int[][] tiles) {
        this.tiles = tiles;
    }
    
    public void loadBlocks(String[] load, Map<String, Block> blocks) {
        for(String name : load) {
            this.blocks.add(blocks.get(name).init());
        }
    }
    
    public void build(World world) {
        for(int x = 0; x < tiles.length; x++) {
            for(int y = 0; y < tiles[x].length; y++) {
                buildBlock(blocks.get(tiles[x][y]), new Vector2(x * 2, y * 2), world);
            }
        }
    }
    
    public void buildBlock(Block block, Vector2 position, World world) {
        if(block.colission) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(position.y, position.x + ((block.height - tileSize) / 2));
            Body body = world.createBody(bodyDef);
            PolygonShape polygon = new PolygonShape();
            polygon.setAsBox(block.width / 2, block.height / 2);
            body.createFixture(polygon, 0);
            polygon.dispose();
        }
    }
    
    public void render(SpriteBatch batch, Bounds cameraBounds) {
        for(int x = 0; x < tiles.length; x++) {
            for(int y = 0; y < tiles[x].length; y++) {
                renderBlock(blocks.get(tiles[x][y]),
                    new Vector2(y * tileSize, x * tileSize),
                    cameraBounds, batch);
            }
        }
    }
    
    public void renderBlock(Block block, Vector2 position, Bounds bounds, SpriteBatch batch) {
        if(position.x - (block.width / 2) > bounds.toX ||
            position.x + (block.width / 2) < bounds.fromX ||
            position.y - (block.height / 2) > bounds.toY ||
            position.y + (block.height / 2) < bounds.fromY) {
                return;
        }
        
        if(block.sprite != null) {
            batch.draw(block.sprite, position.x - (tileSize / 2), 
                position.y - (tileSize / 2), 
                block.width, block.height);
        }
    }
}