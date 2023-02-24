package com.mrboomdev.platformer.environment;

import com.mrboomdev.platformer.util.ColorUtil;

public class MapData {
    public About about;
    public Options options;
    public Ambient ambient;
    public String[] load;
    public Tiles tiles;
    
    public class About {
        public String name;
        public Author[] authors;
        public String[] tags = {};
        public String banner;
        public String wallpaper;
    }
    
    public class Author {
        public String name;
        public String avatar;
        public String url;
        public String[] tags = {};
    }
    
    public class Options {
        public boolean enableDeath = true;
        public boolean hide_ui = false;
        public boolean allow_movement = true;
        public int session_duration = 6;
        public int movement_speed;
        public String[] include_gamemodes;
        public String[] exclude_gamemodes;
    }
    
    public class Ambient {
        public String[] music = {};
        public Lights lights;
    }
    
    public class Lights {
        public ColorUtil scene;
        public ColorUtil player;
    }
    
    public class Tiles {
        public int[][] foreground;
        public int[][] background;
    }
}