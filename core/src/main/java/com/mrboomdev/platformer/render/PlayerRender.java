package com.mrboomdev.platformer.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import java.util.HashMap;
import java.util.HashSet;
import com.mrboomdev.platformer.items.Player;

/*
    THIS IS LITERALLY JUST A LEGACY BULLSHIT
*/

public class PlayerRender {
    private Texture playerTexture;
    private SpriteBatch sprites;
    private HashMap<String, Player> players = new HashMap<String, Player>();
    private BitmapFont fonts;
    private World world;
    
    public PlayerRender(SpriteBatch sprites, World world) {
        playerTexture = new Texture(Gdx.files.internal("img/player/player.jpg"));
        this.world = world;
        this.sprites = sprites;
        fonts = new BitmapFont();
    }
    
    public void add(String nick) {
        Player player = new Player(0, 0, playerTexture, nick);
        players.put(nick, player);
    }
    
    public void move(String nick, int x, int y) {
        Player player = new Player(x, y, playerTexture, nick);
        players.replace(nick, player);
    }
    
    public void moveBy(String nick, int x, int y) {
        Player oldPlayer = players.get(nick);
        Player newPlayer = new Player(oldPlayer.x + x, oldPlayer.y + y, playerTexture, nick);
        players.replace(nick, newPlayer);
    }
    
    public void render() {
        for(Player player : players.values()) {
        	sprites.draw(player.texture, player.x, player.y);
            fonts.draw(sprites, player.nick, player.x, player.y + 100);
        }
    }
}
