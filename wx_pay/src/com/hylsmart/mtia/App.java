package com.hylsmart.mtia;

import android.app.Application;


public class App extends Application {

	private static App instance;

	
	public App() {
		instance = this;
	}

	public static App getInstance() {
		if (instance == null)
			throw new IllegalStateException();
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	

}
