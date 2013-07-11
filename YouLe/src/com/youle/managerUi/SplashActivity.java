package com.youle.managerUi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.LoginPref;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.util.GlobalData;


public class SplashActivity extends StatActivity {
	private final int TIME_UP = 1;
	private LoginPref lp;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == TIME_UP) {
				lp = new LoginPref(SplashActivity.this);
				if(lp.getLogin())
				{
					startActivity(new Intent(SplashActivity.this, GuideActivity.class));
					overridePendingTransition(R.anim.splash_screen_fade,
							R.anim.splash_screen_hold);
				}else
				{
					if(Utility.hasToken())
					{
						startActivity(new Intent(SplashActivity.this, SlidActivity.class));
						overridePendingTransition(R.anim.splash_screen_fade,
								R.anim.splash_screen_hold);
					}else
					{
						startActivity(new Intent(SplashActivity.this, LoginActivity.class));
						overridePendingTransition(R.anim.splash_screen_fade,
								R.anim.splash_screen_hold);
					}
				}
				lp.storeLogin(false);
				SplashActivity.this.finish();
			}
		}
	};
//	private static final LatLng marker1 = new LatLng(30.667327,104.089328);
//	private static final LatLng marker2 = new LatLng(30.667327, 104.079373);
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.splash_activity);
		MyApplication.getInstance().addActivity(this);
		new Thread() {
			public void run() {
				try {
					Utility.mSession = new YLSession(SplashActivity.this);
					Utility.mToken = Utility.mSession.getToken();
//					YouLe.getForums(SplashActivity.this, 1, 1, 20);
//					YouLe.scanCoupon(SplashActivity.this, "1000");
//					YouLe.getCoupon(SplashActivity.this, "0", "1", 5000, 104.089328, 30.667327);
//					float distance = AMapUtils.calculateLineDistance(marker1, marker2);
//					Log.i("test", "distance:"+distance);
					if(Utility.hasToken())
					{
						String res = YouLe.getMeInfo(SplashActivity.this);
						if(res.startsWith(GlobalData.RESULT_OK))
							YouLe.jsonUserInfo(res.substring(3),true);
					}
						
					Thread.sleep(600);
					
				} catch (Exception e) {

				}
				Message msg = new Message();
				msg.what = TIME_UP;
				handler.sendMessage(msg);
			}
		}.start();
	}

}