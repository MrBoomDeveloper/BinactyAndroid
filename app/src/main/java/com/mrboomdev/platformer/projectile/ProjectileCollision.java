package com.mrboomdev.platformer.projectile;

import androidx.annotation.NonNull;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.environment.map.tile.TileInteraction;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.Direction;
import com.mrboomdev.platformer.widgets.ActionButton;

public class ProjectileCollision implements ContactListener {
	private final GameHolder game = GameHolder.getInstance();
	private int selectedTilesCount;

    @Override
    public void beginContact(@NonNull Contact contact) {
        if (contact.getFixtureA() == null || contact.getFixtureB() == null) return;
        onCollide(getUserData(contact, true), getUserData(contact, false));
        onCollide(getUserData(contact, false), getUserData(contact, true));
    }

    private void onCollide(Object me, Object enemy) {
        if(enemy instanceof CharacterEntity) {
            if(me instanceof ProjectileBullet) {
                ProjectileBullet bullet = (ProjectileBullet) me;
                CharacterEntity player2 = (CharacterEntity) enemy;
                if(bullet.owner == player2) return;
                player2.gainDamage(bullet.stats.damage, bullet.power);
				if(enemy != game.settings.mainPlayer) {
					game.environment.particles.createParticle(
							"__medium_boom",
							bullet.body.getPosition().add(bullet.power.cpy().limit(.5f)),
							new Direction(bullet.power.x).isBackward());
				}
            }

			if(me instanceof ProjectileAttack) {
                ProjectileAttack attack = (ProjectileAttack) me;
                if(attack.owner == enemy) return;
                ((CharacterEntity) enemy).gainDamage(attack.owner.stats.damage, attack.power);
				attack.isDead = true;
            }
			
			if(me instanceof TileInteraction && !game.settings.enableEditor) {
				var player = ((CharacterEntity)enemy);
				var interaction = (TileInteraction)me;
				if(interaction.listener == null || player != game.settings.mainPlayer) return;
				player.nearInteraction = interaction;
				interaction.owner.isSelected = true;
				selectedTilesCount++;
				((ActionButton)game.environment.ui.widgets.get("use")).setActive(true);
			}
        }

        if(me instanceof ProjectileBullet && (enemy instanceof CharacterEntity
				|| enemy instanceof MapTile
				|| enemy instanceof ProjectileBullet
				|| enemy instanceof ProjectileAttack)) {
            ProjectileBullet bullet = (ProjectileBullet) me;
            bullet.deactivate();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        onCheckCollision(getUserData(contact, true), getUserData(contact, false), contact.getFixtureA(), contact);
        onCheckCollision(getUserData(contact, false), getUserData(contact, true), contact.getFixtureB(), contact);
    }

    private void onCheckCollision(Object me, Object enemy, Fixture fixture, Contact contact) {
		var game = GameHolder.getInstance();
		if(game.settings.mainPlayer == me && game.settings.enableEditor) contact.setEnabled(false);
		
        if(me instanceof ProjectileBullet || me instanceof ProjectileAttack) {
            contact.setEnabled(false);
        }

        if(me instanceof CharacterEntity && enemy instanceof MapTile) {
            if(((CharacterEntity) me).bottomFixture != fixture) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void endContact(@NonNull Contact contact) {
		onCollideEnd(getUserData(contact, true), getUserData(contact, false));
		onCollideEnd(getUserData(contact, false), getUserData(contact, true));
	}
	
	private void onCollideEnd(Object me, Object enemy) {
		if(me instanceof CharacterEntity && enemy instanceof TileInteraction && !game.settings.enableEditor) {
			var player = (CharacterEntity)me;
			var interaction = (TileInteraction)enemy;
			if(interaction.listener == null || player != game.settings.mainPlayer) return;
			player.nearInteraction = null;
			interaction.owner.isSelected = false;
			
			if(--selectedTilesCount == 0)
				((ActionButton)game.environment.ui.widgets.get("use")).setActive(false);
		}
	}

	private Object getUserData(Contact contact, boolean isA) {
		var fixture = isA ? contact.getFixtureA() : contact.getFixtureB();
		return fixture.getBody().getUserData();
	}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}