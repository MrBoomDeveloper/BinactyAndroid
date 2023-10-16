package com.mrboomdev.platformer.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.game.core.CoreLauncher;
import com.mrboomdev.binacty.ui.widgets.Button;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

public class GameDebugMenu extends FrameLayout {
	private final SharedPreferences prefs = getContext().getSharedPreferences("Save", 0);

	public GameDebugMenu(Context context) {
		super(context);
	}

	@SuppressLint({"SetTextI18n", "InflateParams"})
	public void start(@NonNull Activity activity) {
		var inflater = activity.getLayoutInflater();

		var view = inflater.inflate(R.layout.dev_menu_layout, null);
		view.setVisibility(GONE);
		addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		var buttonHolder = new LinearLayout(activity);
		addView(buttonHolder);

		var button = new Button(activity);
		button.setText("DevMenu");
		button.setBackgroundColor(Color.WHITE);
		buttonHolder.addView(button);

		button.setOnClickListener(v -> {
			var newVisibility = view.getVisibility() == VISIBLE ? GONE : VISIBLE;
			view.setVisibility(newVisibility);
		});

		setupActions(view);
	}

	public void setupActions(@NonNull View myView) {
		GameSettings settings = GameHolder.getInstance().settings;

		setupButtonTriggers(myView, settings);
		setupSwitchTriggers(myView, settings);
	}
	
	private void setupButtonTriggers(@NonNull View view, GameSettings settings) {
		view.findViewById(R.id.closeGameButton).setOnClickListener(button -> {
			var game = GameHolder.getInstance();
			game.launcher.exit(CoreLauncher.ExitStatus.LOBBY);
		});
		
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

		view.findViewById(R.id.resetButton).setOnClickListener(button -> {
			this.prefs.edit().clear().apply();
			var launcher = GameHolder.getInstance().launcher;
			launcher.exit(CoreLauncher.ExitStatus.LOBBY);
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

			((CoreLauncher)getContext()).exit(CoreLauncher.ExitStatus.GAME_OVER);
		});
	}
}