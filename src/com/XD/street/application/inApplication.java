package com.XD.street.application;


import android.app.Application;
import android.content.Context;

public class inApplication extends Application {

	private static Context appContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
//		ImageLoaderConfiguration DefaultConfig = ImageLoaderConfiguration.createDefault(this);
//		// Initialize ImageLoader with configuration.
//		ImageLoader.getInstance().init(DefaultConfig);
//		
		appContext = getApplicationContext();
	}
	
	public static Context getAppContext() {
		return appContext;
	}
}
