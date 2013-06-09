package com.youle.managerData;

import net.tsz.afinal.FinalBitmap;
import android.app.Application;

public class MyApplication extends Application{
	private static MyApplication mInstance = null;
	private FinalBitmap fb;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInstance = this;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
