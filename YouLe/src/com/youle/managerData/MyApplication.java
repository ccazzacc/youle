package com.youle.managerData;

/**
 * MyApplication.getInstance().addActivity(this);
 */

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static MyApplication instance;
//	private static FinalBitmap fb;
	private MyApplication() {
	}

	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;

	}
//	public static FinalBitmap getFb()
//	{
//		if(fb == null)
//		{
//			fb = new FinalBitmap(getInstance());
//		}
//		return fb;
//	}
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	public void removeAct(Activity activity)
	{
		while(activityList.contains(activity))
		{
			activityList.remove(activity);
			if(activity != null)
				activity.finish(); 
		}
	}
	
	public void exit() {
		try {
			for (Activity activity : activityList) {
				if(activity != null)
					activity.finish(); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		System.gc();
	}
	
}