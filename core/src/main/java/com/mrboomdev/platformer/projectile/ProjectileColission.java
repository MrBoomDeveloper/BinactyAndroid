package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.environment.Block;
import com.mrboomdev.platformer.projectile.ProjectileBullet;

public class ProjectileColission implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        if(contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        onCollide(contact.getFixtureA().getBody(), contact.getFixtureB().getBody());
        onCollide(contact.getFixtureB().getBody(), contact.getFixtureA().getBody());
    }
    
    public void onCollide(Body me, Body enemy) {
        if(me.getUserData() instanceof Entity && enemy.getUserData() instanceof Entity) {
            Entity player = (Entity)me.getUserData();
            Entity player2 = (Entity)enemy.getUserData();
            player.gainDamage(player2.stats.damage);
            player2.gainDamage(player.stats.damage);
        }
        
        if(me.getUserData() instanceof ProjectileBullet && enemy.getUserData() instanceof Block) {
            ProjectileBullet bullet = (ProjectileBullet)me.getUserData();
            ((Entity)enemy.getUserData()).gainDamage(bullet.stats.damage);
            bullet.gainDamage(999);
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold manifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}