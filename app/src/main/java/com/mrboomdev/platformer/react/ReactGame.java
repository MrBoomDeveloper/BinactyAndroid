package com.mrboomdev.platformer.react;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.NativeModule;
import com.mrboomdev.platformer.react.ReactBridge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReactGame implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext context) {
        ArrayList<NativeModule> modules = new ArrayList<>();
        modules.add(new ReactBridge(context));
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext context) {
        return Collections.emptyList();
    }
}