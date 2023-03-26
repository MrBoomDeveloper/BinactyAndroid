package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.Block;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;

public class ProjectileColission implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        onCollide(
                contact.getFixtureA().getBody().getUserData(),
                contact.getFixtureB().getBody().getUserData());
        onCollide(
                contact.getFixtureB().getBody().getUserData(),
                contact.getFixtureA().getBody().getUserData());
    }

    private void onCollide(Object me, Object enemy) {
        if (enemy instanceof CharacterEntity) {
            if (me instanceof ProjectileBullet) {
                ProjectileBullet bullet = (ProjectileBullet) me;
                CharacterEntity player2 = (CharacterEntity) enemy;
                if (bullet.owner == player2) return;
                player2.gainDamage(bullet.stats.damage, Vector2.Zero);
            }

            if (me instanceof ProjectileAttack) {
                ProjectileAttack attack = (ProjectileAttack) me;
                if (attack.owner == enemy) return;
                ((CharacterEntity) enemy).gainDamage(attack.stats.damage, attack.power);
                attack.isDead = true;
            }
        }

        if (me instanceof ProjectileBullet
                && (enemy instanceof CharacterEntity
                        || enemy instanceof Block
                        || enemy instanceof MapTile
                        || enemy instanceof ProjectileBullet
                        || enemy instanceof ProjectileAttack)) {
            ProjectileBullet bullet = (ProjectileBullet) me;
            bullet.deactivate();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        onCheckColission(
                contact.getFixtureA().getBody().getUserData(),
                contact.getFixtureB().getBody().getUserData(),
                contact.getFixtureA(),
                contact);

        onCheckColission(
                contact.getFixtureB().getBody().getUserData(),
                contact.getFixtureA().getBody().getUserData(),
                contact.getFixtureB(),
                contact);
    }

    private void onCheckColission(Object me, Object enemy, Fixture fixture, Contact contact) {
		var game = GameHolder.getInstance();
		if(game.settings.mainPlayer == me && game.settings.enableEditor) contact.setEnabled(false);
		
        if (me instanceof ProjectileBullet || me instanceof ProjectileAttack) {
            contact.setEnabled(false);
        }

        if (me instanceof CharacterEntity && enemy instanceof MapTile) {
            if (((CharacterEntity) me).bottomFixture != fixture) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
