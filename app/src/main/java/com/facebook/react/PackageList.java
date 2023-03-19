package com.facebook.react;

import android.app.Application;
import android.content.Context;
import com.facebook.react.shell.MainPackageConfig;
import com.facebook.react.shell.MainReactPackage;
import java.util.Arrays;
import java.util.ArrayList;

public class PackageList {
	private Application application;
	private ReactNativeHost reactNativeHost;
	private MainPackageConfig mConfig;
	
	public PackageList(ReactNativeHost reactNativeHost) {
		this(reactNativeHost, null);
	}
	
	public PackageList(Application application) {
		this(application, null);
	}
	
	public PackageList(ReactNativeHost reactNativeHost, MainPackageConfig config) {
		this.reactNativeHost = reactNativeHost;
		mConfig = config;
	}
	
	public PackageList(Application application, MainPackageConfig config) {
		this.reactNativeHost = null;
		this.application = application;
		mConfig = config;
	}
	
	private Application getApplication() {
		if (this.reactNativeHost == null) return this.application;
		return this.reactNativeHost.getApplication();
	}
	
	private Context getApplicationContext() {
		return getApplication().getApplicationContext();
	}
	
	public ArrayList<ReactPackage> getPackages() {
		return new ArrayList<>(Arrays.<ReactPackage>asList(
			new MainReactPackage(mConfig)
		));
	}
}