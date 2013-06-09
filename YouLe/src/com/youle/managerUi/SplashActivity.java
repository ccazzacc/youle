package com.youle.managerUi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.LoginPref;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.util.OtherUtil;


public class SplashActivity extends Activity {
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
						startActivity(new Intent(SplashActivity.this, MapActivity.class));
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

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.splash_activity);
		new Thread() {
			public void run() {
				try {
					Utility.mSession = new YLSession(SplashActivity.this);
					Utility.mToken = Utility.mSession.getToken();
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