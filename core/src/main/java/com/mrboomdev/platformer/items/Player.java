package com.mrboomdev.platformer.items;

import com.badlogic.gdx.graphics.Texture;

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
