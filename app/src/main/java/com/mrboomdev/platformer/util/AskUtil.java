package com.mrboomdev.platformer.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.ui.ActivityManager;

public class AskUtil {
    public static SharedPreferences prefs;
    private static Activity context;
	private static AskType currentDialog = null;
    
    public static void ask(AskType type, AskCallback callback) {
		if(type == currentDialog) return;
		context = ActivityManager.current;
        prefs = context.getSharedPreferences("Save", 0);
		context.runOnUiThread(() -> {
			createDialog(type, callback).show();
		});
		currentDialog = type;
    }
	
	private static AlertDialog createDialog(AskType type, AskCallback callback) {
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
                final AlertDialog alert = builder.setView(view).create();
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
                    		callback.callbacked(text);
							alert.dismiss();
                		}
					});
        		});
				return alert;
			
            case REQUEST_CLOSE:
				currentDialog = null;
                return builder.setTitle("Close the game")
					.setMessage("Are you sure want to close the game? We will miss you.")
					.setNegativeButton("Stay here", (dialoginterface, i) -> {
						dialoginterface.dismiss();
					})
                	.setPositiveButton("Close", (dialogInterface, i) -> {
						callback.callbacked(true);
					})
					.setCancelable(true)
					.create();
			
			case UPDATE:
				return builder.setTitle("Update the game")
					.setMessage("Hey, there is a new update is out! Go and check it now.")
					.setPositiveButton("Update", (dialoginterface, i) -> {
						callback.callbacked(true);
					}).create();
			
			default:
				return null;
        }
	}
	
	public enum AskType {
        SETUP_NICK,
        UPDATE,
		REQUEST_CLOSE
    }
	
	public interface AskCallback {
		public void callbacked(Object result);
	}
}