package com.mrboomdev.platformer.ui.react;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.NativeModule;
import com.mrboomdev.platformer.ui.react.bridge.AppBridge;
import com.mrboomdev.platformer.ui.react.bridge.PackBridge;
import com.mrboomdev.platformer.ui.react.view.ReactCharacterViewManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReactGame implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext context) {
		return Arrays.<NativeModule>asList(
            new ReactBridge(context),
			new PackBridge(context),
			new AppBridge(context)
       );
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext context) {
        return Arrays.<ViewManager>asList(
            new ReactCharacterViewManager(context)
       );
    }
}