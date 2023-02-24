package com.mrboomdev.platformer.scenes.charactereditor;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.scenes.charactereditor.CharacterEditorUi;
import com.mrboomdev.platformer.scenes.core.CoreScreen;

public class CharacterEditorScreen extends CoreScreen {
  private CharacterEditorUi ui;
  private MainGame game;
  private World world;
  private EntityManager entities;
  private SpriteBatch batch;

  @Override
  public void show() {
    world = new World(new Vector2(0, 0), true);
    entities = new EntityManager(world, new RayHandler(world));
    batch = new SpriteBatch();
    ui = new CharacterEditorUi();
    Gdx.input.setInputProcessor(ui.stage);
  }

  @Override
  public void render(float delta) {
    batch.begin();
    batch.end();
  }
}