package com.mrboomdev.platformer.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Player {
  public int x;
  public int y;
  public Texture texture;
  public String nick;

    public Player(int x, int y, Texture texture, String nick) {
    this.x = x;
    this.y = y;
    this.texture = texture;
    this.nick = nick;
  }
}
