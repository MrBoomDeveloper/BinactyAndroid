package com.mrboomdev.platformer.react;

import android.app.Application;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.PackageList;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.soloader.SoLoader;
import com.facebook.react.ReactPackage;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.react.ReactGame;
import java.util.List;

public class ReactApp extends Application implements ReactApplication {
    private final ReactNativeHost host = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          List<ReactPackage> packages = new PackageList(this).getPackages();
          packages.add(new ReactGame());
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return host;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ReactFeatureFlags.useTurboModules = false;
        SoLoader.init(this, false);
    }
}