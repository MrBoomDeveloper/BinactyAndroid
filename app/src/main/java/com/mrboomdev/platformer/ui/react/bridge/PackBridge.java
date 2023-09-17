package com.mrboomdev.platformer.ui.react.bridge;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.binacty.rn.RNApp;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.game.pack.PackWidget;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Moshi;

import java.util.Objects;

@SuppressWarnings("unused")
@SuppressLint("VisibleForTests")
public class PackBridge extends ReactContextBaseJavaModule {
	
	public PackBridge(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
	@Override
    public String getName() {
		return "PackBridge";
	}
	
	@ReactMethod
	public void managePacks() {
		var dialog = new AndroidDialog().setTitle("Manage Your Packs").addSpace(15);

		for(var pack : PackLoader.getPacks()) {
			dialog.addField(new PackWidget.DialogPackWidget(pack));
			dialog.addSpace(15);
		}

		if(PackLoader.getPacks().size() == 0) {
			dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setText("No packs were found."));
		}

		dialog.addAction(new AndroidDialog.Action().setText("Save and reload").setClickListener(button -> {
			saveAndReload();

			ReactContext context = Objects.requireNonNull(RNApp.getReactInstance().getCurrentReactContext());
			context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("reload", null);

			dialog.close();
		}));

		dialog.addAction(new AndroidDialog.Action().setText("Import").setClickListener(button -> {
			dialog.close();
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("application/zip");
			ActivityManager.current.startActivityForResult(intent, 1);
		}));

		dialog.addSpace(15).show();
	}

	@ReactMethod
	public void getPacks(@NonNull Promise promise) {
		var jsonArray = Arguments.createArray();

		for(var pack : PackLoader.getPacks()) {
			var jsonMap = Arguments.createMap();
			jsonMap.putString("name", pack.name);
			jsonMap.putString("description", pack.description);
			jsonMap.putBoolean("isRequired", pack.required);
			jsonMap.putString("id", pack.id);
			jsonMap.putString("source", pack.source.toString());
			jsonMap.putString("config", pack.config.toString());

			if(pack.icon != null) jsonMap.putString("icon", pack.source.goTo(pack.icon).getFullPath(true));
			if(pack.author != null) jsonMap.putString("author", pack.author);

			jsonArray.pushMap(jsonMap);
		}

		promise.resolve(jsonArray);
	}

	@ReactMethod
	public void getGamemodes(Promise promise) {
		//var a = ReactParser.serializeMap(PackLoader.getGamemodes());

		var jsGamemodes = Arguments.createArray();
		for(var row : PackLoader.getGamemodes()) {
			var jsRow = Arguments.createMap();
			jsRow.putString("title", row.title);
			jsRow.putString("id", row.id);
			var jsData = Arguments.createArray();

			for(var gamemode : row.data) {
				var jsGamemode = Arguments.createMap();
				jsGamemode.putString("name", gamemode.name);
				jsGamemode.putString("id", gamemode.id);
				jsGamemode.putInt("maxPlayers", gamemode.maxPlayers);

				if(gamemode.file != null) jsGamemode.putString("file", gamemode.file.toString());
				if(gamemode.author != null) jsGamemode.putString("author", gamemode.author);
				if(gamemode.time != null) jsGamemode.putString("time", gamemode.time);
				if(gamemode.description != null) jsGamemode.putString("description", gamemode.description);
				if(gamemode.banner != null) jsGamemode.putString("banner", gamemode.source.goTo(gamemode.banner).getFullPath(true));

				if(gamemode.levels != null) {
					var jsLevels = Arguments.createArray();
					for(var levelCategory : gamemode.levels) {
						var jsLevelsCategory = Arguments.createMap();
						jsLevelsCategory.putString("title", levelCategory.title);
						jsLevelsCategory.putString("id", levelCategory.id);

						var jsLevelsCategoryData = Arguments.createArray();
						for(var level : levelCategory.data) {
							var jsLevel = Arguments.createMap();
							jsLevel.putString("id", level.id);
							jsLevel.putString("name", level.name);

							if(level.banner != null) {
								jsLevel.putString("banner", gamemode.source.goTo(level.banner).getFullPath(true));
							}

							if(level.description != null) jsLevel.putString("description", level.description);
							jsLevelsCategoryData.pushMap(jsLevel);
						}

						jsLevelsCategory.putArray("data", jsLevelsCategoryData);
						jsLevels.pushMap(jsLevelsCategory);
					}

					jsGamemode.putArray("levels", jsLevels);
				}

				if(gamemode.maps != null) {
					var jsMaps = Arguments.createArray();
					for(var map : gamemode.maps) {
						var jsMap = Arguments.createMap();
						jsMap.putString("name", map.name);
						jsMap.putString("author", map.author);
						jsMap.putString("file", gamemode.source.goTo(map.file.getPath()).toString());
						jsMaps.pushMap(jsMap);
					}
					jsGamemode.putArray("maps", jsMaps);
				}

				if(gamemode.entry != null) {
					var entryAdapter = Constants.moshi.adapter(PackData.GamemodeEntry.class);
					jsGamemode.putString("entry", entryAdapter.toJson(gamemode.entry));
				}

				jsData.pushMap(jsGamemode);
			}
			jsRow.putArray("data", jsData);
			jsGamemodes.pushMap(jsRow);
		}
		promise.resolve(jsGamemodes);
	}

	@ReactMethod
	public void savePack(@NonNull ReadableMap map, @NonNull Promise promise) {
		promise.reject("Unfinished functionality", "Sorry, but this feature wasn't finished yet.");
	}

	@ReactMethod
	public void pickPack(ReadableMap props, Promise promise) {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("application/zip");
		ActivityManager.current.startActivityForResult(intent, 1);
	}

	@ReactMethod
	public void deletePack(ReadableMap props, @NonNull Promise promise) {
		promise.reject("Not available", "bruh");
	}

	@ReactMethod
	public void setPackActive(ReadableMap props, @NonNull Promise promise) {
		promise.reject("Not available", "bruh");
	}

	@ReactMethod
	public void setPacksOrder(ReadableMap props, @NonNull Promise promise) {
		promise.reject("Not available", "bruh");
	}

	@ReactMethod
	public void createPack(@NonNull ReadableMap map, Promise promise) {
		var manifestFile = FileUtil.external("packs/" + map.getString("id") + "/manifest.json");
		if(manifestFile.getParent().getFile().exists()) {
			promise.reject("Failed to create a pack", "This path is already used!");
			return;
		}

		var manifest = new PackData.Manifest();
		manifest.id = map.getString("id");
		manifest.name = map.getString("name");
		manifest.description = "No description provided.";

		var moshi = new Moshi.Builder().build();
		var adapter = moshi.adapter(PackData.Manifest.class);
		manifestFile.writeString(adapter.toJson(manifest), false);
		PackLoader.addPack(manifestFile.getParent());

		promise.resolve(true);
	}
	
	private void saveAndReload() {
		PackLoader.saveConfig();
		PackLoader.reloadPacks();
		PackLoader.reloadGamemodes();
	}
	
	@ReactMethod
	public void addListener(String name) {}
	
	@ReactMethod
	public void removeListeners(Integer count) {}
}