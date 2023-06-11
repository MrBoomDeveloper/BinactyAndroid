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
			ReactContext context = ActivityManager.reactActivity.reactInstance.getCurrentReactContext();
			Objects.requireNonNull(context).getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("reload", null);
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

			if(pack.author != null) {
				var jsonAuthorMap = Arguments.createMap();
				jsonAuthorMap.putString("name", pack.author.name);
				jsonAuthorMap.putString("url", pack.author.url);
				jsonMap.putMap("author", jsonAuthorMap);
			}

			jsonArray.pushMap(jsonMap);
		}

		promise.resolve(jsonArray);
	}

	@ReactMethod
	public void savePack(@NonNull ReadableMap map, @NonNull Promise promise) {
		promise.reject("Unfinished functionality", "Sorry, but this feature wasn't finished yet.");
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