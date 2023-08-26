package com.mrboomdev.platformer.entity.item;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.character.CharacterSkin;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.ProjectileManager;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

import box2dLight.Light;
import box2dLight.PointLight;

public class Item {
    public String name;
    public Entity.Stats stats;
    public Attack attack;
    public Child child;
    public Entity.Frame skin;
    public Entity.States state;
    @Json(ignore = true)
    public FileUtil source;
    @Json(ignore = true)
    private Sprite sprite;
    @Json(ignore = true)
    private final GameHolder game = GameHolder.getInstance();
    @Json(ignore = true)
    private Light light;

    public void attack(Vector2 power, ProjectileManager projectiles) {
        switch(attack.type) {
            case THROW_CHILD:
                CameraUtil.addCameraShake(.1f, .1f);

                var owner = projectiles.owner;
                projectiles.shoot(power);

                FunUtil.setTimer(() -> {
                    boolean isFlip = owner.getDirection().isBackward();
                    var position = getOffset(owner.skin).scl(isFlip ? -1 : 1, 1).add(owner.getPosition());

                    game.environment.particles
                            .createParticle("__tiny_boom", position, isFlip);

                    if(light == null) {
                        light = new PointLight(game.environment.rayHandler, 30);
                        light.setDistance(1);
                        light.setColor(new Color(255, 255, 0, .5f));
                        light.setActive(false);
                    }

                    light.setPosition(position.x, position.y);
                    light.setActive(true);

                    FunUtil.setTimer(() -> light.setActive(false), .1f);
                }, owner.skin.getCurrentAnimationDeclaration().actionDelay);
                break;

            case USE:
            case PUNCH:
                projectiles.attack(power);
                break;
        }
    }

    public Sprite getSprite() {
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
        var currentCharacterFrame = characterSkin.getCurrentFrame();
        return new Vector2(skin.position[0] + currentCharacterFrame.handPosition[0], skin.position[1] + currentCharacterFrame.handPosition[1]);
    }

    public Item cpy() {
        var copy = new Item();
        copy.name = name;
        copy.stats = stats;
        copy.source = source;
        copy.state = state;
        copy.skin = skin;
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
        @Json(name = "shake") SHAKE,
        @Json(name = "none") NONE,
        @Json(name = "throw") THROW
    }

    public enum AttackType {
        @Json(name = "throw_child") THROW_CHILD,
        @Json(name = "use") USE,
        @Json(name = "punch") PUNCH
    }
}