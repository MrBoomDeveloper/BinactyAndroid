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
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.entity.EntityManager.Spawn;
import java.util.Map;

public class MapBuilder {
    private Array<Block> blocks = new Array<>();
    private MapData.Tiles mapTiles;
    private final int tileSize = 2;
    private MapManager manager;
    
    public MapBuilder(MapData.Tiles tiles, MapManager manager) {
        this.mapTiles = tiles;
        this.manager = manager;
    }
    
    public void loadBlocks(String[] load, Map<String, Block> blocks) {
        for(String name : load) {
            this.blocks.add(blocks.get(name));
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
                }
            }
        }
    }
}