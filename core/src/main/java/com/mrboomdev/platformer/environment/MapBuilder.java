package com.mrboomdev.platformer.environment;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.environment.FreePosition;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.environment.path.PositionPoint;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;
import com.mrboomdev.platformer.entity.EntityManager.Spawn;
import java.util.Map;

public class MapBuilder {
    private Array<Block> blocks = new Array<>();
    private Sprite shadow;
    private MapData.Tiles mapTiles;
    private final int tileSize = 2;
    private MapManager manager;
    
    public MapBuilder(MapData.Tiles tiles, MapManager manager) {
        this.mapTiles = tiles;
        this.manager = manager;
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
    
    public void build(World world, RayHandler rayHandler) {
        for(int x = mapTiles.foreground.length - 1; x >= 0; x--) {
            for(int y = 0; y < mapTiles.foreground[x].length; y++) {
                String special = blocks.get(mapTiles.foreground[x][y]).build(
                    new Vector2(y * 2, x * 2),
                    world, rayHandler);
                    
                if(special == null) continue;
                if(special.equals("spawn_position")) {
                    manager.spawnPositions.add(new Spawn(y * 2, x * 2));
                    manager.aiZones.put(PositionPoint.toText(
                        new Vector2(y * 2, x * 2)), new FreePosition(y * 2, x * 2));
                }
                if(special.equals("ai_zone")) {
                    manager.aiZones.put(PositionPoint.toText(
                        new Vector2(y * 2, x * 2)), new FreePosition(y * 2, x * 2));
                }
            }
        }
    }
    
    public void render(MapLayer layer, SpriteBatch batch, Bounds cameraBounds) {
        int[][] tiles = (layer == MapLayer.FOREGROUND
            ? mapTiles.foreground
            : mapTiles.background);
            
        for(int y = tiles.length - 1; y >= 0; y--) {
            for(int x = 0; x < tiles[y].length; x++) {
                blocks.get(tiles[y][x]).render(
                    new Vector2(x * tileSize, y * tileSize),
                    cameraBounds, batch);
            }
        }
        
        if(layer == MapLayer.FOREGROUND) shadow.draw(batch);
    }
}