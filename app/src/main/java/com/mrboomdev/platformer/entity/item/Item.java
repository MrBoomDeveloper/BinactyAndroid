package com.mrboomdev.platformer.entity.item;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.entity.character.CharacterSkin;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.ProjectileManager;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;

public class Item {
    public String name;
    public Entity.Stats stats;
    public Attack attack;
    public Child child;
    public Entity.Frame skin;
    @Json(name = "light")
    public Entity.LightDeclaration lightDeclaration;
    public Entity.States state;
    @Json(ignore = true)
    public FileUtil source;
    @Json(ignore = true)
    private Sprite sprite;
    @Json(ignore = true)
    private GameHolder game = GameHolder.getInstance();
    @Json(ignore = true)
    private Light light;
    @Json(ignore = true)
    private Vector2 power = new Vector2(), interpolatedPower = new Vector2();
    @Json(ignore = true)
    private CharacterEntity owner;
    @Json(ignore = true)
    private boolean didDisposed;

    public void setOwner(CharacterEntity owner) {
        this.owner = owner;
    }

    public void setIsSelected(boolean isSelected) {
        if(didDisposed) return;

        if(lightDeclaration != null) {
            if(light == null) createLight();

            if(lightDeclaration.type == Entity.LightDeclaration.LightType.DIRECTIONAL
                    && lightDeclaration.duration == Entity.LightDeclaration.LightDuration.ALWAYS) {
                light.setActive(isSelected);
            }
        }
    }

    public void setPower(Vector2 power) {
        this.power = power;
    }

    public void update() {
        if(didDisposed) return;

        if(light == null && lightDeclaration != null) {
            createLight();
        }

        interpolatedPower.add(
                (power.x - interpolatedPower.x) * .1f,
                (power.y - interpolatedPower.y) * .1f
        );

        boolean isFlip = owner.getDirection().isBackward();

        if(light != null && light.isActive()) {
            var position = getOffset(owner.skin).scl(isFlip ? -1 : 1, 1).add(owner.getPosition());
            light.setPosition(position.x, position.y);
            light.setDirection(interpolatedPower.angleDeg());
        }
    }

    public void attack(Vector2 power, ProjectileManager projectiles) {
        if(didDisposed) return;

        switch(attack.type) {
            case THROW_CHILD:
                CameraUtil.addCameraShake(.1f, .1f);
                projectiles.shoot(power);

                FunUtil.setTimer(() -> {
                    boolean isFlip = owner.getDirection().isBackward();
                    var position = getOffset(owner.skin).scl(isFlip ? -1 : 1, 1).add(owner.getPosition());

                    game.environment.particles
                            .createParticle("__tiny_boom", position, isFlip);

                    if(light == null) createLight();

                    light.setActive(true);
                    update();

                    FunUtil.setTimer(() -> light.setActive(false), .1f);
                }, owner.skin.getCurrentAnimationDeclaration().actionDelay);
                break;

            case USE:
            case PUNCH:
                projectiles.attack(power);
                break;
        }
    }

    private void createLight() {
        if(didDisposed) return;

        var color = new Color(
                lightDeclaration.color[0],
                lightDeclaration.color[1],
                lightDeclaration.color[2],
                lightDeclaration.color[3]);

        if(lightDeclaration.type == Entity.LightDeclaration.LightType.POINT) {
            light = new PointLight(game.environment.rayHandler, lightDeclaration.rays);
            light.setDistance(lightDeclaration.distance);
            light.setColor(color);
        } else {
            light = new ConeLight(
                    game.environment.rayHandler,
                    lightDeclaration.rays,
                    color,
                    lightDeclaration.distance,
                    0, 0, 0,
                    lightDeclaration.radius);
        }

        light.setActive(false);

        var filter = new Filter();
        filter.categoryBits = Entity.LIGHT;
        filter.maskBits = Entity.TILE_BOTTOM | Entity.BLOCK;
        light.setContactFilter(filter);
    }

    public void dispose() {
        if(didDisposed) return;
        didDisposed = true;

        if(light != null) {
            light.remove(true);
            light = null;
        }

        lightDeclaration = null;
        state = null;
        game = null;
        sprite = null;
        source = null;
        skin = null;
        attack = null;
        child = null;
        interpolatedPower = null;
        power = null;
    }

    public Sprite getSprite() {
        if(didDisposed) return null;

        if(sprite == null) {
            sprite = (skin.region != null)
                    ? (new Sprite(new Texture(source.goTo(skin.texture).getFileHandle()),
                        skin.region[0], skin.region[1], skin.region[2], skin.region[3]))
                    : new Sprite(new Texture(source.goTo(skin.texture).getFileHandle()));

            sprite.setSize(skin.size[0], skin.size[1]);
        }

        return sprite;
    }

    public Vector2 getOffset(@NonNull CharacterSkin characterSkin) {
        if(didDisposed) return Vector2.Zero;

        var currentCharacterFrame = characterSkin.getCurrentFrame();
        return new Vector2(skin.position[0] + currentCharacterFrame.handPosition[0], skin.position[1] + currentCharacterFrame.handPosition[1]);
    }

    public Item cpy() {
        if(didDisposed) throw new BoomException("Cannot copy a disposed item!");

        var copy = new Item();
        copy.name = name;
        copy.stats = stats;
        copy.source = source;
        copy.state = state;
        copy.skin = skin;
        copy.lightDeclaration = lightDeclaration;
        copy.child = child;
        copy.attack = attack;
        return copy;
    }

    public static class Child {
        public Entity.Stats stats;
        public Entity.Frame skin;
    }

    public static class Attack {
        public AttackType type;
    }

    public enum UseAnimation {
        @Json(name = "shake")
        SHAKE,
        @Json(name = "none")
        NONE,
        @Json(name = "throw")
        THROW
    }

    public enum AttackType {
        @Json(name = "throw_child")
        THROW_CHILD,
        @Json(name = "use")
        USE,
        @Json(name = "punch")
        PUNCH
    }
}