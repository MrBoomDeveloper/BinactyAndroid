package com.mrboomdev.platformer.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.HashSet;

public class PlayerRender {
    private class Player {
        public int x;
        public int y;
        public Texture texture;
        public String nick;
        
        public Player(int x, int y, Texture texture, String nick) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.nick = nick;
        }
    }
    
    private SpriteBatch sprites;
    private HashMap<String, Player> players = new HashMap<String, Player>();
    private BitmapFont fonts;
    
    public PlayerRender(SpriteBatch sprites) {
        this.sprites = sprites;
        fonts = new BitmapFont();
    }
    
    public void add(String nick) {
        Player player = new Player(0, 0, new Texture(Gdx.files.internal("img/player/player.jpg")), nick);
        players.put(nick, player);
    }
    
    public void move(String nick, int x, int y) {
        Player player = new Player(x, y, new Texture(Gdx.files.internal("img/player/player.jpg")), nick);
        players.replace(nick, player);
    }
    
    public void render() {
        for(Player player : players.values()) {
        	sprites.draw(player.texture, player.x, player.y);
            fonts.draw(sprites, player.nick, player.x, player.y + 100);
        }
    }
}
