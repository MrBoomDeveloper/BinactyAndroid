package com.mrboomdev.platformer.game.pack;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.helper.BoomException;
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
			packs = new ArrayList<>();
			if(configs == null) {
				configs = new ArrayList<>();
			}
			reloadPacks();
		}
		return packs;
	}
	
	public static List<PackData.GamemodesRow> getGamemodes() {
		if(gamemodes == null) {
			gamemodes = new ArrayList<>();
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
				if(rows == null) return;
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
			if(configs == null) return;
			for(var config : configs) {
				JsonAdapter<PackData.Manifest> manifestAdapter = moshi.adapter(PackData.Manifest.class);
				var manifest = manifestAdapter.fromJson(config.file.goTo("manifest.json").readString(false));
				if(manifest == null) continue;
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

	@SuppressLint("VisibleForTests")
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
			if(context == null) return;
			context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("reload", null);
			dialog.close();
		}));
		dialog.addSpace(15).show();
	}
	
	@NonNull
	public static PackData.Manifest findById(String id) {
		for(var pack : getPacks()) {
			if(pack.id.equals(id)) return pack;
		}
		throw BoomException.builder("Failed to find a pack. No item with a such name were found: ").addQuoted(id).build();
	}

	public static FileUtil resolvePath(FileUtil contextDir, @NonNull String path) {
		if(path.startsWith("$")) {
			var packName = path.substring(1, path.indexOf("/"));
			var dir = findById(packName).source;
			return dir.goTo(path.substring(path.indexOf("/") + 1));
		} else {
			return contextDir.goTo(path);
		}
	}
}