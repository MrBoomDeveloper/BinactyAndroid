package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityBody;
import com.mrboomdev.platformer.entity.skin.EntityAnimation;

public class EntityConfig {
    private String bodyPath;
    private String animationPath;
    public String highlight;
    public String name = "New entity";
    public String description = "No description.";
    public EntityAnimation animation;
    public EntityBody body;
    public Stats stats;
    
    public EntityConfig build(String character, World world) {
        Gson gson = new GsonBuilder().setLenient().create();
        animation = gson.fromJson(Gdx.files.internal(character + "/" + animationPath).readString(), 
            EntityAnimation.class).build();
        body = gson.fromJson(Gdx.files.internal(character + "/" + bodyPath).readString(), 
            EntityBody.class).build(character, world, animation);
        return this;
    }
    
    public class Stats {
        public int health = 100;
        public int damage = 10;
        public int speed = 5;
        public int shield = 0;
        public int stamina = 10;
        
        public Stats cpy() {
            return new Stats() {
                int health = this.health;
                int damage = this.damage;
                int speed = this.speed;
                int shield = this.shield;
                int stamina = this.stamina;
            };
        }
    }
}