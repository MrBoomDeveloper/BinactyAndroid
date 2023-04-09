package com.mrboomdev.platformer.ui.react.bridge;

import android.content.Intent;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.game.pack.PackWidget;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;

public class PackBridge extends ReactContextBaseJavaModule {
	
	public PackBridge(ReactApplicationContext context) {
        super(context);
    }

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
			PackLoader.reloadPacks();
			PackLoader.reloadGamemodes();
			ReactContext context = ActivityManager.reactActivity.reactInstance.getCurrentReactContext();
			context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("reload", null);
			dialog.close();
		}));
		dialog.addAction(new AndroidDialog.Action().setText("Import").setClickListener(button -> {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("application/zip");
			ActivityManager.current.startActivityForResult(intent, 1);
		}));
		dialog.addSpace(15).show();
	}
	
	@ReactMethod
	public void addListener(String name) {}
	
	@ReactMethod
	public void removeListeners(Integer count) {}
}