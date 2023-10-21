package com.mrboomdev.binacty.script.bridge;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.api.pack.PackSaves;
import com.mrboomdev.platformer.ui.ActivityManager;

public class MyPackSaves extends PackSaves {
	private final MyPackContext context;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	public MyPackSaves(MyPackContext context) {
		this.context = context;
		this.reload();
	}

	@NonNull
	private String getUniqueKey(String originalKey) {
		return context.getId() + "_save_" + originalKey;
	}

	@Override
	public boolean getBoolean(String s, boolean b) {
		return prefs.getBoolean(getUniqueKey(s), b);
	}

	@Override
	public String getString(String s, String a) {
		return prefs.getString(getUniqueKey(s), a);
	}

	@Override
	public int getInt(String s, int a) {
		return prefs.getInt(getUniqueKey(s), a);
	}

	@Override
	public float getFloat(String s, float a) {
		return prefs.getFloat(getUniqueKey(s), a);
	}

	@Override
	public void setString(String s, String s1) {
		initEditor();
		editor.putString(getUniqueKey(s), s1);
	}

	@Override
	public void setFloat(String s, float v) {
		initEditor();
		editor.putFloat(getUniqueKey(s), v);
	}

	@Override
	public void setInt(String s, int i) {
		initEditor();
		editor.putInt(getUniqueKey(s), i);
	}

	@Override
	public void setBoolean(String s, boolean b) {
		initEditor();
		editor.putBoolean(getUniqueKey(s), b);
	}

	@Override
	public void reload() {
		var context = ActivityManager.current;
		prefs = context.getSharedPreferences("Save", 0);

		if(editor != null) editor = prefs.edit();
	}

	private void initEditor() {
		if(editor == null) {
			editor = prefs.edit();
		}
	}

	@Override
	public void save() {
		if(editor == null) return;

		editor.apply();
		editor = null;
	}
}