package com.mrboomdev.platformer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.mrboomdev.platformer.MainGame;

public class ReactActivity extends Activity {

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    //SoLoader.init()
    Button button = new Button(this);
    button.setText("Open game");
    button.setOnClickListener((View view) -> {
        finish();
        MainGame.getInstance().toggleGameView(true);
    });
    setContentView(button);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}