package com.mrboomdev.platformer.environment;

public class MapData {
    public String[] load;
    public Tiles tiles;
    
    public class Tiles {
        public int[][] foreground;
        public int[][] background;
    }
}