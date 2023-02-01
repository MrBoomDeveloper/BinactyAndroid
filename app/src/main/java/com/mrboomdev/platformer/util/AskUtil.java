package com.mrboomdev.platformer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mrboomdev.platformer.AndroidLauncher;
import com.mrboomdev.platformer.R;

public class AskUtil {
    public static SharedPreferences prefs;
    private static Activity context;
    private static AlertDialog alert;
	private static AskType currentDialog = AskType.EMPTY;
    
    public enum AskType {
        SETUP_NICK,
        UPDATE,
		EMPTY
    }
    
    public static void setContext(Activity activity) {
        context = activity;
        prefs = context.getSharedPreferences("Save", 0);
    }
    
    public static void ask(AskType type, AskCallback callback) {
		if(type == currentDialog) return;
		context.runOnUiThread(() -> showDialog(type, callback));
		currentDialog = type;
    }
	
	private static void showDialog(AskType type, AskCallback callback) {
        DynamicColors.applyToActivityIfAvailable(context);
        LayoutInflater inflater = context.getLayoutInflater();
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
        builder.setCancelable(false);
        View view = null;
        
        switch(type) {
            case SETUP_NICK:
                view = inflater.inflate(R.layout.dialog_setup_nick, null);
                EditText input = view.findViewById(R.id.input);
                builder.setTitle("Welcome to Action Platformer!");
                builder.setMessage("(Game name will be changed in the future :/)\n Enter here your nickname. Please dont put here any bad words. Thanks :)");
                builder.setPositiveButton("Confirm", (dialogInterface, i) -> {});
                alert = builder.setView(view).create();
            	alert.setOnShowListener((dialogInterface) -> {
            		Button confirm = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            		input.requestFocus();
            		input.setOnEditorActionListener((textView, actionId, keyEvent) -> {
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
                		} else if(/*"containsBadWords(text)*/ false) {
							input.setError("Please do not use bad words in your nickname.");
						} else {
							SharedPreferences.Editor editor = prefs.edit();
                    		editor.putString("nick", text);
							editor.commit();
                    		callback.callbacked(text);
							alert.dismiss();
                		}
					});
        		});
                break;
            case UPDATE:
                break;
        }
        alert.show();
	}
	
	public interface AskCallback {
		public void callbacked(Object result);
	}
}