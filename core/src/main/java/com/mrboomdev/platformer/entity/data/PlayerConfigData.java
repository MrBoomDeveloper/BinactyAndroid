package com.mrboomdev.platformer.entity.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.util.Direction;

public class PlayerConfigData {
    private String texture_path;
    private Texture texture;
    public String id = "no_id";
	public String name = "Unknown character";
	public String tag = "A custom character";
	public int hp = 100;
	public int speed = 10;
	public int shield = 10;
	public int attack = 10;
	public int[] size;
	public Bones bones;
    
    public PlayerConfigData init() {
        texture = new Texture(Gdx.files.internal("world/player/characters/" + id + "/" + texture_path));
        bones.init(texture);
        return this;
    }
	
	public class Bones {
        public float legs_gap = 0.15f;
		public Bone head;
		public Bone body;
		public Bone arm;
		public Bone leg;
        
        public void init(Texture texture) {
            head.init(texture);
            body.init(texture);
            leg.init(texture);
            arm.init(texture);
        }
	}
	
	public class Bone {
        public Sprite sprite;
		public int[] region;
		public float[] position;
		public float[] size;
        
        public void init(Texture texture) {
            sprite = new Sprite(new TextureRegion(texture, region[0], region[1], region[2], region[3]));
            sprite.setSize(size[0], size[1]);
            sprite.setPosition(position[0], position[1]);
        }
        
        public void draw(SpriteBatch batch, Vector2 position, float angle, Direction direction) {
            sprite.setSize(direction.isBackward() ? -size[0] : size[0], size[1]);
            sprite.setPosition((direction.isBackward()
                ? position.x - this.position[0]
                : position.x + this.position[0]),
                  position.y + this.position[1]);
            sprite.setOrigin(
                sprite.getWidth() / 2, 
                sprite.getHeight());
            sprite.setRotation(angle);
                
            sprite.draw(batch);
        }
	}
}