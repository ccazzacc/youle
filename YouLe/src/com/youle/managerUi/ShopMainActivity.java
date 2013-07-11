package com.youle.managerUi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.YouLe;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.zxing.activity.CaptureActivity;

public class ShopMainActivity extends StatActivity implements OnClickListener {
	private String scanResult;
	private GestureDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_mainactivity);
		initView();
	}
	@SuppressWarnings("deprecation")
	private void initView() {
		((LinearLayout) findViewById(R.id.shop_consume))
				.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.shop_scan)).setOnClickListener(this);
		SlipMainCenter.tvName.setText(R.string.im_shop);
		SlipMainCenter.lyButtom.setVisibility(View.GONE);
		SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);
		SlipMainCenter.btnMsgtop.setVisibility(View.GONE);
		detector = new GestureDetector(new OnGestureListener() {

			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				try {
					if (Math.abs(velocityX) > ViewConfiguration
							.get(ShopMainActivity.this).getScaledMinimumFlingVelocity()) {
						if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 10) {
							SlidActivity.showMenu();
						} else if (e2.getX() - e1.getX() > 100
								&& Math.abs(velocityX) > 10) {
							SlidActivity.showMenu();
						}
					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}

			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		((LinearLayout)findViewById(R.id.shopmain_ly)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				detector.onTouchEvent(event);
				return true;
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.shop_consume:
			startActivity(new Intent(ShopMainActivity.this,
					ConsumeActivity.class));
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		case R.id.shop_scan:
			// 打开扫描界面扫描条形码或二维码
			Intent openCameraIntent = new Intent(ShopMainActivity.this,
					CaptureActivity.class);
			startActivityForResult(openCameraIntent, 0);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 处理扫描结果
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			scanResult = bundle.getString("result");
			if (null != scanResult && !TextUtils.isEmpty(scanResult))
				new UpScanTask().execute(scanResult,"0");
		}
	}

	private void showDialog(String result) {
		new AlertDialog.Builder(this)
				.setTitle(
						(new StringBuffer().append(getString(R.string.coupon_use)))
								.toString())
				.setPositiveButton(getString(R.string.use),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								new UpScanTask().execute(scanResult,"1");
							}
						})
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}).show();
	}


	private class UpScanTask extends AsyncTask<String, Void, String> {
		String isUse;
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(ShopMainActivity.this);
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				if(isUse.equals("0"))
					showDialog(getString(R.string.coupon_use));
					
			} else
				ToastUtil.show(ShopMainActivity.this, result);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			isUse = params[1];
			return YouLe.scanCoupon(ShopMainActivity.this, params[0],params[1]);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(ShopMainActivity.this);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(ShopMainActivity.this, getString(R.string.please_wait));
		}
		
	}
}
