package com.mrboomdev.binacty.rn;

import android.annotation.SuppressLint;
import android.content.Context;

import com.facebook.flipper.android.AndroidFlipperClient;
import com.facebook.flipper.android.utils.FlipperUtils;
import com.facebook.flipper.core.FlipperClient;
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin;
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin;
import com.facebook.flipper.plugins.fresco.FrescoFlipperPlugin;
import com.facebook.flipper.plugins.inspector.DescriptorMapping;
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin;
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor;
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin;
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin;
import com.facebook.react.ReactInstanceEventListener;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.network.NetworkingModule;
public class RNFlipper {

	@SuppressLint("VisibleForTests")
	public static void init(Context context, ReactInstanceManager reactInstanceManager) {
		if(!FlipperUtils.shouldEnableFlipper(context)) return;

		FlipperClient client = AndroidFlipperClient.getInstance(context);
		client.addPlugin(new InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()));
		client.addPlugin(new DatabasesFlipperPlugin(context));
		client.addPlugin(new SharedPreferencesFlipperPlugin(context));
		client.addPlugin(CrashReporterPlugin.getInstance());

		NetworkFlipperPlugin networkFlipperPlugin = new NetworkFlipperPlugin();

		NetworkingModule.setCustomClientBuilder(builder -> {
			builder.addNetworkInterceptor(new FlipperOkhttpInterceptor(networkFlipperPlugin));
		});

		client.addPlugin(networkFlipperPlugin);
		client.start();

		ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
		if(reactContext == null) {
			reactInstanceManager.addReactInstanceEventListener(new ReactInstanceEventListener() {
				@Override
				public void onReactContextInitialized(ReactContext reactContext) {
					reactInstanceManager.removeReactInstanceEventListener(this);
					reactContext.runOnNativeModulesQueueThread(() -> {
						client.addPlugin(new FrescoFlipperPlugin());
					});
				}
			});
		} else {
			client.addPlugin(new FrescoFlipperPlugin());
		}
	}
}