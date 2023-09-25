package com.mrboomdev.platformer.ui.react;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.mrboomdev.binacty.rn.components.RNCharacterPreview;
import com.mrboomdev.platformer.ui.react.bridge.AppBridge;
import com.mrboomdev.platformer.ui.react.bridge.PackBridge;
import com.mrboomdev.platformer.ui.react.view.ReactCharacterViewManager;

import java.util.Arrays;
import java.util.List;

public class ReactGame implements ReactPackage {

    @NonNull
	@Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext context) {
		return Arrays.asList(
            new ReactBridge(context),
			new PackBridge(context),
			new AppBridge(context)
       );
    }

    @NonNull
	@Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext context) {
        return List.of(
				new ReactCharacterViewManager(context),
				new RNCharacterPreview()
		);
    }
}