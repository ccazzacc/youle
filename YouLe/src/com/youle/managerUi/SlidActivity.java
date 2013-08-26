package com.youle.managerUi;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.youle.R;
import com.youle.fragment.SlidLeftFragment;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.MySlidView;



public class SlidActivity extends FragmentActivity{
	
	private static MySlidView mSlidView;
	private SlidLeftFragment slidLeftFragment;
//	private MapCenterFragment mapCenterFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slid_center_main);

        if(OtherUtil.isNullOrEmpty(new SharedPref(SlidActivity.this).getCity())){
            startActivity(new Intent(SlidActivity.this,ChooseCity.class));
        }
		init();
		MyApplication.getInstance().addActivity(this);
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(YouLe.NOTIFICATION_ID);
	}
	
	private void init(){
		
		getScreenSize();
		
		mSlidView = (MySlidView)findViewById(R.id.slid_view);
		
		mSlidView.setMenuView(getLayoutInflater().inflate(R.layout.slid_left_fragment, null));
		mSlidView.setSlidView(getLayoutInflater().inflate(R.layout.slid_center_fragment, null));
		
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		slidLeftFragment = new SlidLeftFragment();
		if(!OtherUtil.is3gWifi(this))
        	ToastUtil.show(this, R.string.net_no);
        else
        {
    		ft.replace(R.id.menu_fragment, slidLeftFragment);
        }
//		mapCenterFragment = new MapCenterFragment();
//		ft.replace(R.id.slid_fragment, mapCenterFragment);

        SlipMainCenter slipMainCenter=new SlipMainCenter();
        ft.replace(R.id.slid_fragment, slipMainCenter);

		ft.commit();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	public static void showMenu(){
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
	private Boolean isExit = false;
	private Boolean hasTask = false;
	Timer tExit = new Timer();
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			isExit = false;
			hasTask = true;
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// System.out.println("user back down");
			if (isExit == false) {
				if(OtherUtil.is3gWifi(this))
					showMenu();
				isExit = true;
				Toast.makeText(SlidActivity.this, R.string.press_again, Toast.LENGTH_SHORT)
						.show();
				if (!hasTask) {
					tExit.schedule(task, 2000);
				}
			} else {
				MyApplication.getInstance().exit();
			}
			
		}
		return true;
	}

}
