package com.mrboomdev.binacty.rn.components;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.text.ReactTextView;
import com.mrboomdev.binacty.game.core.CoreLauncher;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameSettings;

public class RNCharacterPreview extends SimpleViewManager<ReactTextView> {
	private String src;

	/*@ReactProp(name = "src")
	public void setSrc(@NonNull ReactTextView view, String src) {
		this.src = src;
		view.setText(src);
	}*/

	@NonNull
	@Override
	public String getName() {
		return "RNCharacterPreview";
	}

	@NonNull
	@Override
	protected ReactTextView createViewInstance(@NonNull ThemedReactContext ctx) {
		var text = new ReactTextView(ctx);
		text.setTextColor(Color.RED);
		text.setText("Not set new value");

		System.out.println("Create a View Instance!!! RN");

		return text;
	}

	private static class GameFragment extends AndroidFragmentApplication {

		@Nullable
		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			var config = new AndroidApplicationConfiguration();
			config.useCompass = false;

			var settings = new GameSettings();
			settings.isControlsEnabled = false;
			settings.isUiVisible = false;
			settings.playerName = "";

			var launcher = new CoreLauncher() {

				@Override
				public SharedPreferences getSave() {
					return inflater.getContext().getSharedPreferences("Save", 0);
				}
			};

			//var game = GameHolder.setInstance(launcher, settings);
			//TODO: Create a gamemode where above of player there is light, and behind him is walls with floor.

			return initializeForView(GameHolder.getInstance(), config);
		}
	}
}