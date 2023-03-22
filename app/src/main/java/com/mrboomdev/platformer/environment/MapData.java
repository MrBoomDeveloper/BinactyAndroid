package com.mrboomdev.platformer.environment;

public class MapData {
    public String[] load;
    public Tiles tiles;
    
    public static class Tiles {
        public int[][] foreground;
    }
}