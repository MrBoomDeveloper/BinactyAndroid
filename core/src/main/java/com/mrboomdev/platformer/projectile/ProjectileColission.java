package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.Block;
import com.mrboomdev.platformer.projectile.ProjectileAttack;
import com.mrboomdev.platformer.projectile.ProjectileBullet;

public class ProjectileColission implements ContactListener {
	
    @Override
    public void beginContact(Contact contact) {
        if(contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        onCollide(contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getBody().getUserData());
        onCollide(contact.getFixtureB().getBody().getUserData(), contact.getFixtureA().getBody().getUserData());
    }
    
    private void onCollide(Object me, Object enemy) {
		if(enemy instanceof CharacterEntity) {
			if(me instanceof ProjectileBullet) {
				ProjectileBullet bullet = (ProjectileBullet)me;
				CharacterEntity player2 = (CharacterEntity)enemy;
				if(bullet.owner == player2) return;
            	player2.gainDamage(bullet.stats.damage);
        	}
		
			if(me instanceof ProjectileAttack) {
				ProjectileAttack attack = (ProjectileAttack)me;
				if(attack.owner == enemy) return;
				((CharacterEntity)enemy).gainDamage(attack.stats.damage);
				attack.isDead = true;
			}
		}
		
		if(me instanceof ProjectileBullet && (
		   enemy instanceof CharacterEntity ||
		   enemy instanceof Block ||
		   enemy instanceof ProjectileBullet ||
		   enemy instanceof ProjectileAttack)
		) {
			ProjectileBullet bullet = (ProjectileBullet)me;
			bullet.deactivate();
		}
    }
	
	@Override
    public void preSolve(Contact contact, Manifold manifold) {
		onCheckColission(contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getBody().getUserData(), contact);
		onCheckColission(contact.getFixtureB().getBody().getUserData(), contact.getFixtureA().getBody().getUserData(), contact);
	}
	
	private void onCheckColission(Object me, Object enemy, Contact contact) {
		if(me instanceof ProjectileBullet || 
		   me instanceof ProjectileAttack) {
			contact.setEnabled(false);
		}
	}

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}