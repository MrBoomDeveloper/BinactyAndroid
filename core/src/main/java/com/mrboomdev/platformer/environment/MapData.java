package com.mrboomdev.platformer.environment;
import com.mrboomdev.platformer.environment.data.MapTiles;

public class MapData {
    public String[] load;
    public int[][] spawns;
    public MapTiles tiles;
    
    public class about {
        public String name = "Unknown";
        public String author = "Unknown";
        public String[] tags = {"No", "Tags", "Found"};
        public int duration = 6;
        public int version = 1;
    }
}