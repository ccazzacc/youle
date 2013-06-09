package com.youle.managerUi;

import com.youle.R;
import com.youle.fragment.MapLeftFragment;
import com.youle.fragment.MapCenterFragment;
import com.youle.util.GlobalData;
import com.youle.view.MySlidView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class MapActivity extends FragmentActivity{
	
	private MySlidView mSlidView;
	private MapLeftFragment mapLeftFragment;
	private MapCenterFragment mapCenterFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map_center_main);
		init();
		
	}
	
	private void init(){
		
		getScreenSize();
		
		mSlidView = (MySlidView)findViewById(R.id.slid_view);
		
		mSlidView.setMenuView(getLayoutInflater().inflate(R.layout.map_left_fragment, null));
		mSlidView.setSlidView(getLayoutInflater().inflate(R.layout.map_center_fragment, null));
		
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		
		mapLeftFragment = new MapLeftFragment();
		ft.replace(R.id.menu_fragment, mapLeftFragment);
		
		mapCenterFragment = new MapCenterFragment();
		ft.replace(R.id.slid_fragment, mapCenterFragment);
		
		ft.commit();
		
	}

	
	
	
	public void showMenu(){
		mSlidView.openMenuView();
	}

	/**
	 * 获得屏幕分辨率
	 */
	private void getScreenSize() {
		/*
		 * 实例化DisplayMetrics对象，为得到屏幕分辨率 getWindowManager()获得现有Activity的Handler,
		 * getDefaultDisplay()获得屏幕宽高度，
		 */
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager windowManager = getWindow().getWindowManager();
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);

		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		float density = displayMetrics.density;
		
		GlobalData.setScreenSize(width, height, density);
		
	}

}
