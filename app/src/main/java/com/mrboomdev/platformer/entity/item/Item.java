package com.mrboomdev.platformer.entity.item;

import androidx.annotation.NonNull;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.character.CharacterSkin;
import com.mrboomdev.platformer.projectile.ProjectileManager;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

public class Item {
    public String name;
    public Stats stats;
    public Flags flags;
    public Attack attack;
    public Entity.Frame skin;
    public Entity.States state;
    @Json(ignore = true)
    public FileUtil source;
    @Json(ignore = true)
    private Sprite sprite;

    public void attack(Vector2 power, ProjectileManager projectiles) {
        switch(attack.type) {
            case THROW_CHILD:
                projectiles.shoot(power);
                break;

            case USE:
                break;

            case PUNCH:
                projectiles.attack(power);
                break;
        }
    }

    public Sprite getSprite() {
        if(sprite == null) {
            sprite = new Sprite(new Texture(source.goTo(skin.texture).getFileHandle()));
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
        copy.flags = flags;
        copy.source = source;
        copy.state = state;
        copy.skin = skin;
        copy.attack = attack;
        return copy;
    }

    public static class Stats {
        @Json(name = "max_count") public int maxCount;
        @Json(name = "reload_duration") public int reloadDuration;
    }

    public static class Flags {
        @Json(name = "is_toggleable") public boolean isToggleable;
        @Json(name = "is_aimable") public boolean isAimable;
    }

    public static class Attack {
        public AttackType type;
    }

    public enum AttackType {
        @Json(name = "throw_child") THROW_CHILD,
        @Json(name = "use") USE,
        @Json(name = "punch") PUNCH
    }
}