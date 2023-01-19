package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.PlayerEntity;

public class EntityColission implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        if(contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        
        if(contact.getFixtureA().getBody().getUserData() instanceof Entity) {
            Entity player = (Entity)contact.getFixtureA().getBody().getUserData();
            if(contact.getFixtureB().getBody().getUserData() instanceof Entity) {
                Entity player2 = (Entity)contact.getFixtureB().getBody().getUserData();
                player.gainDamage(player2.stats.damage);
                player2.gainDamage(player.stats.damage);
            }
        }
    }

    @Override
    public void endContact(Contact arg0) {}

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {}

    @Override
    public void postSolve(Contact arg0, ContactImpulse arg1) {}
}