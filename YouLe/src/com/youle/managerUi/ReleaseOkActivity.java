package com.youle.managerUi;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.youle.R;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

public class ReleaseOkActivity extends Activity {
	private MyBroadcastReciver reciver;
	private TextView mTxtLoc, mTxtSay;
	private ImageView mIvRoad;
	private boolean mIsUnRegister;
	private String[] address;
	private String txtSay;
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.re_ok_activity);
		getData();
		initView();

		OtherUtil.getLocation(ReleaseOkActivity.this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalData.BROADCAST_COUNTER_ACTION);
		reciver = new MyBroadcastReciver();
		this.registerReceiver(reciver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (!mIsUnRegister) {
			this.unregisterReceiver(reciver);
		}
	}

	private void getData() {
		mTxtSay = (TextView) findViewById(R.id.text_say);
		mIvRoad=(ImageView)findViewById(R.id.iv_road);
		Bitmap bmp = null;
		mType = getIntent().getIntExtra("type", 0);
		txtSay = getIntent().getStringExtra("txt");
		Uri uri=Uri.parse(txtSay);
		switch (mType) {
		case 0:
			
			break;
		case 1:
		case 2:
			mIvRoad.setVisibility(View.VISIBLE);
			try {
				bmp=BitmapFactory.decodeStream(ReleaseOkActivity.this.getContentResolver().openInputStream(uri));
				bmp=OtherUtil.zoomBitmap(bmp,getWindowPix()-80);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mIvRoad.setImageBitmap(bmp);
			break;
		case 3:
			mTxtSay.setVisibility(View.VISIBLE);
			mTxtSay.setText(txtSay);
			break;
		default:
			break;
		}
		
	}

	private void initView() {
		mTxtLoc = (TextView) findViewById(R.id.text_me_loc);
		
		
	}

	public class MyBroadcastReciver extends BroadcastReceiver {
		double lat, lng;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(GlobalData.BROADCAST_COUNTER_ACTION)) {
				// AMapLocation location=(AMapLocation)intent.getExtras();
				Bundle bundle = intent.getExtras();
				Log.i("1234", "onReceive  " + bundle.getDouble("lat") + " "
						+ bundle.getDouble("lng"));
				lat = bundle.getDouble("lat");
				lng = bundle.getDouble("lng");
				mThread.start();

			}

		}

		private Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				mTxtLoc.setText(" " + address[0] + address[1] + address[2]);
			}

		};
		Thread mThread = new Thread() {
			@Override
			public void run() {
				address = OtherUtil.getDesc(ReleaseOkActivity.this, lat, lng);
				handler.sendEmptyMessage(0);
			}

		};
	}
	/**
     * 得到屏幕宽度
     * @return
     */
    private int  getWindowPix() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
//        int heightPixels = dm.heightPixels;
//        float density = dm.density;
        return widthPixels;
    }
}
