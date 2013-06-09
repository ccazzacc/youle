package com.youle.managerUi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.SharedPref.YLSession;

import java.util.HashMap;

public class SysSetActivity extends Activity implements OnClickListener {
	private Button mBtnSina, mBtnQQ;
	private SharedPref sharedPref;
	private AbstractWeibo mWeibo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_set_activity);
		initView();
		connected();
		AbstractWeibo.initSDK(this);
	}

	private void connected() {
		sharedPref = new SharedPref(SysSetActivity.this);
		Log.i("1234", "sian: " + sharedPref.getSinaStatus());
		if (sharedPref.getSinaStatus()) {
			mBtnSina.setVisibility(View.INVISIBLE);
			TextView textView = (TextView) findViewById(R.id.sys_txt_sina);
			textView.setText(textView.getText() + " ("
					+ sharedPref.getSinaNickname() + ")");
		}
		if (sharedPref.getQQStatus()) {
			mBtnQQ.setVisibility(View.INVISIBLE);
			TextView textView = (TextView) findViewById(R.id.sys_txt_qq);
			textView.setText(textView.getText() + " ("
					+ sharedPref.getQQnickname() + ")");
		}
	}

	private void initView() {
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.set);
		((Button) findViewById(R.id.sys_logout)).setOnClickListener(this);
		mBtnSina = (Button) findViewById(R.id.sys_btn_sinaLink);
		mBtnSina.setOnClickListener(this);
		mBtnQQ = (Button) findViewById(R.id.sys_btn_tentLink);
		mBtnQQ.setOnClickListener(this);
		((LinearLayout)findViewById(R.id.sysset_meSet)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.sysset_about))
				.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.sysset_pass))
		.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.sysset_applyCar))
		.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.sysset_applyShop))
		.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sysset_about:
			startActivity(new Intent(SysSetActivity.this, AboutActivity.class));
			break;
		case R.id.sysset_meSet:
			startActivity(new Intent(SysSetActivity.this,MeSetActivity.class));
			SysSetActivity.this.finish();
			break;
		case R.id.twobtn_header_left:
			SysSetActivity.this.finish();
			break;

		case R.id.sys_logout:
			Utility.mSession.resetToken();
			Utility.mToken = null;
			startActivity(new Intent(SysSetActivity.this, LoginActivity.class));
			SysSetActivity.this.finish();
			break;
		case R.id.sys_btn_sinaLink:
			mWeibo = AbstractWeibo
					.getWeibo(SysSetActivity.this, SinaWeibo.NAME);
			mWeibo.setWeiboActionListener(weiboListener);
			mWeibo.showUser(null);
			break;
		case R.id.sys_btn_tentLink:
			mWeibo = AbstractWeibo.getWeibo(SysSetActivity.this,
					TencentWeibo.NAME);
			mWeibo.setWeiboActionListener(weiboListener);
			mWeibo.showUser(null);
			break;
		case R.id.sysset_pass:
			startActivity(new Intent(SysSetActivity.this, PwfixActivity.class));
			break;
		case R.id.sysset_applyCar:
			startActivity(new Intent(SysSetActivity.this, ApplyTaxiActivity.class));
			break;
		case R.id.sysset_applyShop:
			startActivity(new Intent(SysSetActivity.this, ApplyShopActivity.class));
			break;
		}
	}
	
	public WeiboActionListener weiboListener = new WeiboActionListener() {
		@Override
		public void onComplete(AbstractWeibo abstractWeibo, int i,
				HashMap<String, Object> stringObjectHashMap) {
			String shareName = abstractWeibo.getDb().get("nickname");

			if (abstractWeibo.getId() == 1) {
				sharedPref.saveSinaStatus(true, shareName);
			} else if (abstractWeibo.getId() == 2) {
				sharedPref.saveQQStatus(true, shareName);
			}
			Log.i("1234", abstractWeibo.getId() + "  id");
			connected();
		}

		@Override
		public void onError(AbstractWeibo abstractWeibo, int i,
				Throwable throwable) {

		}

		@Override
		public void onCancel(AbstractWeibo abstractWeibo, int i) {

		}
	};
}
