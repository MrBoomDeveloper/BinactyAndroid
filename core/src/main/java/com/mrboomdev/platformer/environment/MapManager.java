package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.mrboomdev.platformer.environment.MapBlock;
import com.mrboomdev.platformer.environment.MapData;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.fixed.FixedMapRender;

public class MapManager {
    private String json;
    private MapData data;
    private int version;
    
    public MapManager load(boolean isInternal, String name) {
        if(isInternal) {
            json = Gdx.files.internal("data/maps/" + name + ".json").readString();
        } else {
            json = Gdx.files.external(Gdx.files.getExternalStoragePath() + name).readString();
        }
        return this;
    }
    
    public MapManager parse(int version) {
        this.version = version;
        Gson gson = new Gson();
        data = gson.fromJson(json, MapData.class);
        return this;
    }
    
    public MapManager build() {
        return this;
    }
	
	public void render(SpriteBatch batch, MapLayer layer) {
		if(version == 4) {
            /*FixedMapRender.render(batch);
            for(int x = 0; x < data.tiles.length; x++) {
                for(int y = 0; y < data.tiles[x].length; y++) {
                    
                }
            }*/
        }
	}
}