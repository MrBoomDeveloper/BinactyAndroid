package com.mrboomdev.platformer.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.mrboomdev.platformer.object.Player;
import java.util.HashMap;

public class PlayersManager {
    private World world;
    public HashMap<String, Player> players = new HashMap<String, Player>();
    public Array<Player> players1 = new Array<Player>();
    
    @Deprecated
    public PlayersManager(World world) {
        this.world = world;
    }
    
    @Deprecated
    public void create(String nick) {
        Texture texture = new Texture(Gdx.files.internal("img/player/enemy.jpg"));
        Sprite sprite = new Sprite(texture);
		sprite.setSize(150, 150);
        sprite.setPosition(510, 250);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2), (sprite.getY() + sprite.getHeight() / 2));
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2f;
        fixtureDef.friction = 2f;
        fixtureDef.restitution = 2f;
        body.createFixture(fixtureDef);
        shape.dispose();
        
        Player player = new Player(nick, sprite, body);
        players.put(nick, player);
    }
    
    @Deprecated
    public void moveBy(String nick, int x, int y) {
        throw new RuntimeException("YOU CANT USE THIS METHOD");
    }
    
    @Deprecated
    public void render(SpriteBatch batch) {
        for(Player player : players.values()) {
            player.render(batch);
        }
    }
}