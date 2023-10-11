package com.mrboomdev.platformer.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

import java.io.IOException;
import java.util.Objects;

public class GameDebugLauncher extends GameLauncher {
	private static GameSettings previousSettings;

	@SuppressLint("SetTextI18n")
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		LogUtil.setThreadCrashHandler(Thread.currentThread());
		ActivityManager.current = this;
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
		GameHolder game;

		try {
			game = createGameInstance(getDebugIntent());
			AudioUtil.setVolume(1, 1);
		} catch(IOException e) {
			throw new BoomException("Unable to start game!", e);
		}

		GameDebugMenu menu = new GameDebugMenu(this);
		FrameLayout parent = findViewById(R.id.gameplay_parent);
		parent.addView(menu, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		menu.start(this);

		restorePreviousSettings();
		previousSettings = game.settings;
	}

	@NonNull
	private Intent getDebugIntent() throws IOException {
		var intent = new Intent();

		var entryJson = BoomFile.internal("standard_gamemode.json").readString();
		var adapter = Constants.moshi.adapter(PackData.GamemodeEntry.class);
		var entry = Objects.requireNonNull(adapter.fromJson(entryJson));

		var level = new Bundle();
		level.putString("id", entry.levelId);

		intent.putExtra("level", level);
		intent.putExtra("entries", new String[] { entryJson });

		return intent;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityManager.dispose();
	}
	
	@Override
	public void exit(ExitStatus status) {
		AudioUtil.clear();

		if(status == ExitStatus.CRASH || status == ExitStatus.LOBBY) {
			finishAffinity();
			return;
		}

		runOnUiThread(() -> {
			var intent = new Intent(this, GameDebugLauncher.class);
			startActivity(intent);
			finish();
		});
	}

	private void restorePreviousSettings() {
		if(previousSettings == null) return;
		var game = GameHolder.getInstance();

		game.settings.isControlsEnabled = previousSettings.isControlsEnabled;
		game.settings.isUiVisible = previousSettings.isUiVisible;

		game.settings.debugCamera = previousSettings.debugCamera;
		game.settings.debugRaysDisable = previousSettings.debugRaysDisable;
		game.settings.debugRenderer = previousSettings.debugRenderer;
		game.settings.debugValues = previousSettings.debugValues;
		game.settings.debugStage = previousSettings.debugStage;
	}
}