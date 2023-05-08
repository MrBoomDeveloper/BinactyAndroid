package com.mrboomdev.platformer.game;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.R;

public class GameDebugMenu {
	private static boolean isMenuCreated = false;
	private static boolean isMenuOpened = false;
	private WindowManager wm;
	private SharedPreferences prefs;
	public View myView;
	private Context context;
	
	public GameDebugMenu(Context context) {
		this.context = context;
		this.prefs = ((Activity)context).getSharedPreferences("Save", 0);
	}
	
	@TargetApi(26)
	@SuppressLint("UseSwitchCompatOrMaterialCode")
	public void onResume() {
		if(isMenuCreated || (Build.VERSION.SDK_INT < 26)) return;
		
		if(!Settings.canDrawOverlays(context)) {
			Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
			context.startActivity(intent);
			return;
		}
			
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			PixelFormat.TRANSPARENT);
			
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		myView = ((Activity)context).getLayoutInflater().inflate(R.layout.dev_menu_layout, null);
		
		LinearLayout hold = myView.findViewById(R.id.holder);
		ScrollView menu = myView.findViewById(R.id.menu);
		Button menuTrigger = myView.findViewById(R.id.menuTrigger);
			
		OnTouchListener dragEvent = new OnTouchListener() {
			private int x, y;
				
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						x = (int) event.getRawX();
						y = (int) event.getRawY();
						break;
						
					case MotionEvent.ACTION_MOVE: int nowX = (int) event.getRawX();
						int nowY = (int) event.getRawY();
						int movedX = nowX - x;
						int movedY = nowY - y;
						x = nowX; y = nowY;
						params.x = params.x + movedX;
						params.y = params.y + movedY;
						wm.updateViewLayout(myView, params);
						break;
				}
				return false;
			}
		};
		
		hold.setOnTouchListener(dragEvent);
		menuTrigger.setOnTouchListener(dragEvent);
			
		params.gravity = Gravity.TOP | Gravity.START;
		params.x = 0;
		params.y = 0;
		
		menuTrigger.setOnClickListener(button -> {
			isMenuOpened = isMenuOpened ? false : true;
			menu.setVisibility(isMenuOpened ? View.VISIBLE : View.GONE);
			((Button)button).setText(isMenuOpened ? "Hide DevMenu" : "Show DevMenu");
		});
		wm.addView(myView, params);
		
		GameSettings settings = GameHolder.getInstance().settings;
		setupButtonTriggers(myView, settings);
		setupSwitchTriggers(myView, settings);
		isMenuCreated = true;
	}
	
	public void destroy() {
		((Activity)context).finishAffinity();
		wm.removeView(myView);
		isMenuCreated = false;
	}
	
	private void setupButtonTriggers(View view, GameSettings settings) {
		Button closeGameButton = view.findViewById(R.id.closeGameButton);
		closeGameButton.setOnClickListener(button -> this.destroy());
		
		Button gainHealthButton = view.findViewById(R.id.gainHealthButton);
		gainHealthButton.setOnClickListener(button -> {
			settings.mainPlayer.stats.maxHealth = 5000;
			settings.mainPlayer.gainDamage(-5000, Vector2.Zero);
		});
		
		Button gameOverButton = view.findViewById(R.id.gameOverButton);
		gameOverButton.setOnClickListener(button -> settings.mainPlayer.die(false));
	}
	
	private void setupSwitchTriggers(View view, GameSettings settings) {
		Switch debugRendererSwitch = view.findViewById(R.id.debugRendererSwitch);
		debugRendererSwitch.setChecked(settings.debugRenderer);
		debugRendererSwitch.setOnCheckedChangeListener((toggle, isActive) -> {
			settings.debugRenderer = isActive;
			prefs.edit().putBoolean("debugRenderer", isActive).apply();
		});
		
		Switch lightsSwitch = view.findViewById(R.id.lightsSwitch);
		lightsSwitch.setChecked(settings.debugRaysDisable);
		lightsSwitch.setOnCheckedChangeListener((toggle, isActive) -> {
			settings.debugRaysDisable = isActive;
			prefs.edit().putBoolean("debugRaysDisable", isActive).apply();
		});
		
		Switch stageSwitch = view.findViewById(R.id.debugStage);
		stageSwitch.setOnCheckedChangeListener((toggle, isActive) -> {
			settings.debugStage = isActive;
		});
		
		Switch editorSwitch = view.findViewById(R.id.editorSwitch);
		editorSwitch.setChecked(settings.enableEditor);
		editorSwitch.setOnCheckedChangeListener((toggle, isActive) -> {
			settings.enableEditor = isActive;
			settings.pause = false;
			prefs.edit().putBoolean("forceEditor", isActive).apply();
			((GameDebugLauncher)context).exit(GameLauncher.Status.GAME_OVER);
		});
	}
}