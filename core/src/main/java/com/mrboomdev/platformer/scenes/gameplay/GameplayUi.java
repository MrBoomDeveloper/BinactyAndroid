package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.util.ActorUtil;
import java.text.SimpleDateFormat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mrboomdev.platformer.scenes.core.CoreUi;
import com.mrboomdev.platformer.widgets.JoystickWidget;
import com.mrboomdev.platformer.widgets.DebugValuesWidget;
import com.mrboomdev.platformer.widgets.ActionButton;
import com.mrboomdev.platformer.widgets.TextWidget;

public class GameplayUi implements CoreUi {
  public Stage stage;
  public JoystickWidget joystick;
  private float time = 360000;
  private TextWidget timer;
  public DebugValuesWidget debugValues;
  private GameplayScreen gameplay;
  private float cameraZoom;

  public GameplayUi(GameplayScreen screen, Entity entity) {
    this.gameplay = screen;
    stage = new Stage();
    debugValues = (DebugValuesWidget) new DebugValuesWidget().connectToEntity(entity).addTo(stage);
    /*ActorUtil screenshot = new ActionButton()
        .toPosition(new Vector2(Gdx.graphics.getWidth() - 175, Gdx.graphics.getHeight() - 175))
        .onClick(() -> Gdx.app.exit())
        .addTo(stage);
    ActorUtil pause = new ActionButton()
        .toPosition(new Vector2(Gdx.graphics.getWidth() - 335, Gdx.graphics.getHeight() - 175))
        .onClick(() -> System.exit(0))
        .addTo(stage);*/

    Table table = new Table();
    table.setFillParent(true);
    table.right().bottom().pad(75);

    cameraZoom = gameplay.camera.zoom;
    new ActionButton().onClick(() -> {
              cameraZoom += .05f;
              gameplay.camera.zoom = cameraZoom;
            }).addTo(table);
    new ActionButton().onClick(() -> {
              cameraZoom -= .05f;
              gameplay.camera.zoom = cameraZoom;
            }).addTo(table);

    joystick = (JoystickWidget) new JoystickWidget().connectToEntity(entity);
    stage.addActor(joystick);

    Table table1 = new Table();
    table.setFillParent(true);
    table1.right().bottom();
    ActionButton button3 = new ActionButton();
    table1.add(button3);
    stage.addActor(table1);

    timer = new TextWidget(Gdx.files.internal("font/gilroy-bold.ttf"), 60);
    timer.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 75);
    stage.addActor(timer);

    stage.addActor(table);
    stage.addListener(new ActorGestureListener() {
          @Override
          public void zoom(InputEvent event, float from, float to) {
            float willZoomTo = cameraZoom + ((from - to) / 1000);
            debugValues.setValue("wiilZoomTo", willZoomTo + "");
            debugValues.setValue("cameraZoom", cameraZoom + "");
            if (willZoomTo < 0.2f) return;
            if (willZoomTo > 1.5f) return;
            gameplay.camera.zoom = willZoomTo;
          }

          @Override
          public void touchDown(InputEvent arg0, float arg1, float arg2, int arg3, int arg4) {
            super.touchDown(arg0, arg1, arg2, arg3, arg4);
            cameraZoom = gameplay.camera.zoom;
          }
        });
    // stage.setDebugAll(true);
  }

  public void render(float delta) {
    stage.act(delta);
    stage.draw();
    time -= delta * 1000;

    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
    timer.setText(dateFormat.format((int) time));
    debugValues.setValue("TimeLeft", String.valueOf(time));

    if (time <= 0) {
      System.exit(0);
    }
  }

  public void dispose() {
    stage.dispose();
  }
}