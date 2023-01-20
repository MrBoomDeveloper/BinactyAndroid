package com.mrboomdev.platformer.projectile;
import com.mrboomdev.platformer.projectile.ProjectileBullet;

public class ProjectileBullet {
    public Stats stats;
    
    public ProjectileBullet() {
        this.stats = new Stats();
    }
    
    public void gainDamage(int damage) {
        
    }
    
    public void destroy() {
        
    }
    
    public class Stats {
        public int health = 1;
        public int damage = 10;
        public int speed = 5;
    }
}