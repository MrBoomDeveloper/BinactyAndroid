package com.mrboomdev.binacty.rn;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactNativeHost;
import com.facebook.soloader.SoLoader;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.ui.react.ReactGame;
import com.mrboomdev.platformer.util.helper.BoomException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressLint("VisibleForTests")
public class RNApp extends Application implements ReactApplication {
	private static ReactNativeHost mReactNativeHost;

	public static ReactInstanceManager getReactInstance() {
		return mReactNativeHost.getReactInstanceManager();
	}

	@NotNull
	public static ReactContext getReactContext() {
		var context = getReactInstance().getCurrentReactContext();

		if(context == null) {
			throw new BoomException("React context is null!");
		}

		return context;
	}

	public static SharedPreferences getSave(String file) {
		return getReactContext().getSharedPreferences(file, 0);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		mReactNativeHost = null;
	}

	{
		mReactNativeHost = new DefaultReactNativeHost(this) {
			@Override
			public boolean getUseDeveloperSupport() {
				return BuildConfig.DEBUG;
			}

			@NonNull
			@Override
			protected List<ReactPackage> getPackages() {
				List<ReactPackage> packages = new PackageList(this).getPackages();
				packages.add(new ReactGame());

				return packages;
			}

			@NonNull
			@Contract(pure = true)
			@Override
			protected String getJSMainModuleName() {
				return "index";
			}

			@Override
			protected boolean isNewArchEnabled() {
				return BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
			}

			@Override
			protected Boolean isHermesEnabled() {
				return BuildConfig.IS_HERMES_ENABLED;
			}
		};
	}

	@Override
	public ReactNativeHost getReactNativeHost() {
		return mReactNativeHost;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SoLoader.init(this, false);

		if(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
			DefaultNewArchitectureEntryPoint.load();
		}

		if(BuildConfig.DEBUG) initFlipper();
	}

	private void initFlipper() {
		try {
			var clazz = Class.forName("com.mrboomdev.binacty.rn.RNFlipper");
			var initializer = clazz.getDeclaredMethod("init", Context.class, ReactInstanceManager.class);
			initializer.invoke(null, this, getReactNativeHost().getReactInstanceManager());
		} catch(ClassNotFoundException e) {
			throw new BoomException("RNFlipper class was not found", e);
		} catch(NoSuchMethodException e) {
			throw new BoomException("RNFlipper.init() method was not found", e);
		} catch(InvocationTargetException e) {
			throw new BoomException("RNFlipper.init() method has different parameters", e);
		} catch(IllegalAccessException e) {
			throw new BoomException("RNFlipper.init() method was forbidden for access", e);
		}
	}
}