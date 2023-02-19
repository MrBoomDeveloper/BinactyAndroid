package com.mrboomdev.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.util.BadWordChecker;
import java.util.regex.Pattern;

public class DebugStartGameActivity extends AndroidApplication implements NativeContainer {
	private FirebaseAnalytics analytics;
	private AndroidApplicationConfiguration gameConfig;
	private SharedPreferences prefs;
	private BadWordChecker badWordChecker = new BadWordChecker();
	private String[] badWords;
	private boolean isInitialized = false;
	
	@Override
	protected void onCreate(Bundle instance) {
		LogSender.startLogging(this);
		super.onCreate(instance);
		analytics = FirebaseAnalytics.getInstance(this);
		
		gameConfig = new AndroidApplicationConfiguration();
		gameConfig.useImmersiveMode = true;
		gameConfig.useAccelerometer = false;
		gameConfig.useCompass = false;
		gameConfig.useGyroscope = false;
		initialize(MainGame.setInstance(new AndroidAnalytics(), this, new GameSettings()), gameConfig);
		
		prefs = getSharedPreferences("Save", Context.MODE_PRIVATE);
		Gson gson = new Gson();
		//badWords = gson.fromJson(Gdx.files.internal("world/player/blacklist.json").readString(), String[].class);
		badWords = new String[]{};
		showDialog();
	}
	
	private void showDialog() {
		DynamicColors.applyToActivityIfAvailable(this);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_input_layout, null);
		
		EditText input = view.findViewById(R.id.input);
		input.setText(prefs.getString("nick", "Player228"));
		EditText bots = view.findViewById(R.id.bots);
		bots.setText(String.valueOf(prefs.getInt("botsCount", 2)));
		
		CheckBox colissionsBox = view.findViewById(R.id.colissions);
		colissionsBox.setChecked(prefs.getBoolean("showBodyColissions", false));
		
		AlertDialog dialog = new MaterialAlertDialogBuilder(this)
			.setTitle("PlatformerDebug Config")
			.setMessage("The game will remember the text that you'll pass here, but dialog will show again.")
			.setCancelable(false)
			.setView(view)
			.setPositiveButton("Confirm", (dialogInterface, i) -> {})
			.create();
		dialog.setOnShowListener((dialogInterface) -> {
			Button confirm = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
			bots.setOnEditorActionListener((textView, actionId, keyEvent) -> {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					confirm.performClick();
					return true;
				}
				return false;
			});
			confirm.setOnClickListener((button) -> {
				String text = input.getText().toString().trim();
				if(text.isEmpty()) {
					input.setError("You can't make your nick empty. Please enter some text.");
				} else if(containsBadWords(text)) {
					input.setError("Please do not use bad words in your nickname.");
				} else if(bots.getText().toString().isEmpty()) {
					bots.setError("Please enter any number here from 0 to whatever you want.");
				} else {
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("nick", text);
					editor.putInt("botsCount", Integer.parseInt(bots.getText().toString()));
					editor.putBoolean("showBodyColissions", colissionsBox.isChecked());
					editor.commit();
							
					toggleGameView(false);
					dialog.dismiss();
				}
			});
		});
		//dialog.show();
	}
	
	private boolean containsBadWords(String text) {
		String formatted = text.toLowerCase().replaceAll("0", "o")
											 .replaceAll("1", "i");
		
		formatted = formatted.replaceAll(Pattern.quote("!"), "i")
							 .replaceAll(Pattern.quote("+"), "t")
							 .replaceAll(Pattern.quote("-"), "")
							 .replaceAll(Pattern.quote("_"), "")
							 .replaceAll(Pattern.quote("—"), "")
							 .replaceAll(Pattern.quote("·"), "")
							 .replaceAll(Pattern.quote("("), "c")
							 .replaceAll(Pattern.quote("~"), "")
							 .replaceAll(Pattern.quote("|"), "")
							 .replaceAll(Pattern.quote("•"), "")
							 .replaceAll(Pattern.quote("×"), "")
							 .replaceAll(Pattern.quote("="), "")
							 .replaceAll(Pattern.quote("*"), "")
							 .replaceAll(Pattern.quote("."), "");
		
		for(String badWord : badWords) {
			if(formatted.contains(badWord) || 
			 removeDoubleChars(formatted, 0).contains(badWord) ||
			 removeDoubleChars(formatted, 1).contains(badWord) ||
			   formatted.replaceAll(" ", "").contains(badWord)) {
				return true;
			}
		}
		return false;
	}
	
	private String removeDoubleChars(String request, int maxRepeats) {
		StringBuilder result = new StringBuilder();
		char previousChar = '$';
		int repeats = 0;
		for(char nextChar : request.toCharArray()) {
			if(nextChar != previousChar) {
				result.append(nextChar);
				repeats = 0;
			} else {
				if(repeats < maxRepeats) result.append(nextChar);
				repeats++;
			}
			previousChar = nextChar;
		}
		return result.toString();
	}
	
	@Override
	public void toggleGameView(boolean isActive) {
		Gdx.app.postRunnable(() -> {
			MainGame game = MainGame.getInstance();
			game.setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
		});
	}
}