package com.mrboomdev.platformer.game.pack;

import android.util.Log;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackLoader {
	private static List<PackData.Manifest> packs;
	private static List<PackData.GamemodesRow> gamemodes;
	private static List<PackData.Config> configs;
	
	public static List<PackData.Config> getConfigs() {
		if(configs == null) reloadPacks();
		return configs;
	}

	public static List<PackData.Manifest> getPacks() {
		if(packs == null) {
			packs = new ArrayList<PackData.Manifest>();
			if(configs == null) {
				configs = new ArrayList<PackData.Config>();
			}
			reloadPacks();
		}
		return packs;
	}
	
	public static List<PackData.GamemodesRow> getGamemodes() {
		if(gamemodes == null) {
			gamemodes = new ArrayList<PackData.GamemodesRow>();
			reloadGamemodes();
		}
		return gamemodes;
	}
	
	public static void reloadGamemodes() {
		if(packs == null) reloadPacks();
		gamemodes.clear();
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<List<PackData.GamemodesRow>> adapter = moshi.adapter(Types.newParameterizedType(List.class, PackData.GamemodesRow.class));
			for(var pack : packs) {
				if(!pack.config.active || pack.resources == null || pack.resources.gamemodes == null) continue;
				List<PackData.GamemodesRow> rows = adapter.fromJson(pack.source.goTo(pack.resources.gamemodes).readString(false));
				for(var row : rows) {
					for(var gamemode : row.data) {
						gamemode.source = pack.source.goTo(pack.resources.gamemodes).getParent();
						gamemode.file = gamemode.source.goTo(gamemode.file.getPath());
						gamemode.author = pack.author;
					}
				}
				gamemodes.addAll(rows);
			}
		} catch(IOException e) {
			e.printStackTrace();
			showErrorDialog(e);
		}
	}
	
	public static void reloadPacks() {
		if(packs == null) getPacks();
		packs.clear();
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<List<PackData.Config>> adapter = moshi.adapter(Types.newParameterizedType(List.class, PackData.Config.class));
			configs = adapter.fromJson(FileUtil.external("packs/installed.json").readString(false));
			for(var config : configs) {
				JsonAdapter<PackData.Manifest> manifestAdapter = moshi.adapter(PackData.Manifest.class);
				var manifest = manifestAdapter.fromJson(config.file.goTo("manifest.json").readString(false));
				manifest.source = config.file.goTo("");
				manifest.config = config;
				packs.add(manifest);
			}
		} catch(IOException e) {
			e.printStackTrace();
			showErrorDialog(e);
		}
	}
	
	public static boolean addPack(PackData.Manifest manifest, FileUtil file) {
		for(var config : configs) {
			if(config.file.equals(file.getParent().goTo(manifest.id))) return false;
		}
		configs.add(new PackData.Config(FileUtil.external("packs/" + manifest.id)));
		saveConfig();
		return true;
	}
	
	public static void saveConfig() {
		Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<List<PackData.Config>> adapter = moshi.adapter(Types.newParameterizedType(List.class, PackData.Config.class));
		FileUtil.external("packs/installed.json").writeString(adapter.toJson(PackLoader.getConfigs()), false);
	}
	
	private static void showErrorDialog(Exception e) {
		var dialog = new AndroidDialog().setTitle("Failed to load Packs").setCancelable(false);
		dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setText("Something went wrong while loading Packs. Stacktrace:\n" + Log.getStackTraceString(e)));
		dialog.addAction(new AndroidDialog.Action().setText("Ignore").setClickListener(button -> dialog.close()));
		dialog.addAction(new AndroidDialog.Action().setText("Reset").setClickListener(button -> {
			FileUtil.external("packs").remove();
			FileUtil.external("packs/installed.json").writeString(FileUtil.internal("packs/defaultList.json").readString(false), false);
			reloadPacks();
			reloadGamemodes();
			ReactContext context = ActivityManager.reactActivity.reactInstance.getCurrentReactContext();
			context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("reload", null);
			dialog.close();
		}));
		dialog.addSpace(15).show();
	}
	
	public static PackData.Manifest findById(String id) {
		for(var pack : getPacks()) {
			if(pack.id.equals(id)) return pack;
		}
		throw new NullPointerException("No pack with a such name were found: " + id);
	}
}