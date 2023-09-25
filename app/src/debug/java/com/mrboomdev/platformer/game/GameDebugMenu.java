package com.mrboomdev.platformer.game;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.badlogic.gdx.utils.Array;
import com.mrboomdev.binacty.game.core.CoreLauncher;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

public class GameDebugMenu {
	private static boolean isMenuCreated = false;
	private static boolean isMenuOpened = false;
	private WindowManager wm;
	private final SharedPreferences prefs;
	public View myView;
	private final Context context;
	
	public GameDebugMenu(@NonNull Context context) {
		this.context = context;
		this.prefs = context.getSharedPreferences("Save", 0);
	}
	
	@TargetApi(26)
	@SuppressLint({"InflateParams", "ClickableViewAccessibility"})
	public void onResume() {
		if(isMenuCreated || (Build.VERSION.SDK_INT < 26)) return;
		
		if(!Settings.canDrawOverlays(context)) {
			String action = android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
			Uri uri = Uri.parse("package:" + context.getPackageName());

			Intent intent = new Intent(action, uri);
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
		
		LinearLayout holder = myView.findViewById(R.id.holder);
		LinearLayout menu = myView.findViewById(R.id.menu);
		Button menuTrigger = myView.findViewById(R.id.menuTrigger);
			
		params.gravity = Gravity.TOP | Gravity.START;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		
		menuTrigger.setOnClickListener(button -> {
			isMenuOpened = !isMenuOpened;

			menu.setVisibility(isMenuOpened ? View.VISIBLE : View.GONE);
			params.width = isMenuOpened ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
			params.height = isMenuOpened ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
			wm.updateViewLayout(myView, params);

			holder.setBackgroundColor(isMenuOpened ? Color.parseColor("#cc11071F") : Color.TRANSPARENT);
			((Button)button).setText(isMenuOpened ? "Hide DevMenu" : "Show DevMenu");
		});

		wm.addView(myView, params);
		
		GameSettings settings = GameHolder.getInstance().settings;
		setupButtonTriggers(myView, settings);
		setupSwitchTriggers(myView, settings);
		isMenuCreated = true;
	}
	
	public void destroy() {
		myView.setVisibility(View.GONE);

		var launcher = GameHolder.getInstance().launcher;
		launcher.exit(CoreLauncher.ExitStatus.LOBBY);

		wm.removeView(myView);
		isMenuCreated = false;
	}
	
	private void setupButtonTriggers(@NonNull View view, GameSettings settings) {
		view.findViewById(R.id.closeGameButton).setOnClickListener(button -> this.destroy());
		
		view.findViewById(R.id.gainHealthButton).setOnClickListener(button -> {
			if(settings.mainPlayer == null) return;

			settings.mainPlayer.stats.maxHealth = 666666;
			settings.mainPlayer.gainDamage(-666666);
			settings.mainPlayer.stats.maxStamina = 666666;
			settings.mainPlayer.stats.stamina = 666666;
		});

		view.findViewById(R.id.enableControlsButton).setOnClickListener(button -> {
			settings.isUiVisible = true;
			settings.isControlsEnabled = true;

			for(var widget : GameHolder.getInstance().environment.ui.widgets.values()) {
				widget.setOpacityNow(1);
				widget.setVisibilityNow(true);
				widget.setVisible(true);
			}

			CameraUtil.reset();
			CameraUtil.setTarget(settings.mainPlayer);
		});

		registerTeleportButton(view, R.id.teleportDownButton, 0, -10);
		registerTeleportButton(view, R.id.teleportUpButton, 0, 10);
		registerTeleportButton(view, R.id.teleportLeftButton, -10, 0);
		registerTeleportButton(view, R.id.teleportRightButton, 10, 0);
		
		view.findViewById(R.id.gameOverButton).setOnClickListener(button -> {
			try {
				settings.mainPlayer.gainDamage(Integer.MAX_VALUE);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});

		view.findViewById(R.id.killAllButton).setOnClickListener(button -> {
			try {
				var game = GameHolder.getInstance();
				var everyone = new Array<>(game.environment.entities.characters);
				for(var character : everyone) {
					if(character == game.settings.mainPlayer) continue;
					character.gainDamage(Integer.MAX_VALUE);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		});

		view.findViewById(R.id.teleportAllToMeButton).setOnClickListener(button -> {
			try {
				var game = GameHolder.getInstance();
				var everyone = new Array<>(game.environment.entities.characters);
				for(var character : everyone) {
					if(character == game.settings.mainPlayer) continue;
					character.body.setTransform(game.settings.mainPlayer.getPosition(), 0);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		});

		view.findViewById(R.id.healAllButton).setOnClickListener(button -> {
			try {
				var game = GameHolder.getInstance();
				var everyone = new Array<>(game.environment.entities.characters);
				for(var character : everyone) {
					if(character == game.settings.mainPlayer) continue;
					character.gainDamage(-character.stats.maxHealth);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		});

		view.findViewById(R.id.resetButton).setOnClickListener(button -> {
			this.prefs.edit().clear().apply();
			this.destroy();
		});
	}

	private void registerTeleportButton(@NonNull View view, int id, float x, float y) {
		view.findViewById(id).setOnClickListener(button -> {
			try {
				var player = GameHolder.getInstance().settings.mainPlayer;
				var currentPosition = player.body.getPosition();
				player.body.setTransform(currentPosition.add(x, y), 0);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}

	@SuppressLint("UseSwitchCompatOrMaterialCode")
	private void setupSwitchTriggers(@NonNull View view, @NonNull GameSettings settings) {
		Switch debugRendererSwitch = view.findViewById(R.id.debugRendererSwitch);
		debugRendererSwitch.setChecked(settings.debugRenderer);
		debugRendererSwitch.setOnCheckedChangeListener((toggle, isActive) -> settings.debugRenderer = isActive);

		Switch lightsSwitch = view.findViewById(R.id.lightsSwitch);
		lightsSwitch.setChecked(settings.debugRaysDisable);
		lightsSwitch.setOnCheckedChangeListener((toggle, isActive) -> settings.debugRaysDisable = isActive);

		Switch volumeSwitch = view.findViewById(R.id.volumeToggle);
		volumeSwitch.setOnCheckedChangeListener((toggle, isActive) -> {
			AudioUtil.isDisabled = isActive;
			AudioUtil.soundVolume = isActive ? 0 : 1;
			AudioUtil.musicVolume = isActive ? 0 : 1;
		});

		Switch stageSwitch = view.findViewById(R.id.debugStage);
		stageSwitch.setOnCheckedChangeListener((toggle, isActive) -> settings.debugStage = isActive);

		Switch debugCameraToggle = view.findViewById(R.id.debugCameraToggle);
		debugCameraToggle.setOnCheckedChangeListener((toggle, isActive) -> settings.debugCamera = isActive);
		
		Switch editorSwitch = view.findViewById(R.id.editorSwitch);
		editorSwitch.setChecked(settings.enableEditor);
		editorSwitch.setOnCheckedChangeListener((toggle, isActive) -> {
			settings.enableEditor = isActive;
			settings.pause = false;
			prefs.edit().putBoolean("forceEditor", isActive).apply();

			((CoreLauncher)context).exit(CoreLauncher.ExitStatus.GAME_OVER);
		});
	}
}