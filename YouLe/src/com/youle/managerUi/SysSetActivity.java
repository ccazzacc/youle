package com.youle.managerUi;

import net.tsz.afinal.FinalBitmap;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.Utility;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.service.SystemMsgService;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.SlipButton;
import com.youle.view.SlipButton.OnChangedListener;

public class SysSetActivity extends StatActivity implements OnClickListener {
	// private Button mBtnSina, mBtnQQ;
	// private SharedPref sharedPref;
	// private AbstractWeibo mWeibo;
	private LinearLayout lSetPass, lyTaxi, lyShop;
	private TextView tvTaxi, tvShop;
	private View vSetPass;
	private GestureDetector detector;
	private SlipButton sb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_set_activity);
		initView();
		// connected();
		// AbstractWeibo.initSDK(this);
		MyApplication.getInstance().addActivity(this);
	}

	// private void connected() {
	// sharedPref = new SharedPref(SysSetActivity.this);
	// Log.i("1234", "sian: " + sharedPref.getSinaStatus());
	// if (sharedPref.getSinaStatus()) {
	// mBtnSina.setVisibility(View.INVISIBLE);
	// TextView textView = (TextView) findViewById(R.id.sys_txt_sina);
	// textView.setText(textView.getText() + " ("
	// + sharedPref.getSinaNickname() + ")");
	// }
	// if (sharedPref.getQQStatus()) {
	// mBtnQQ.setVisibility(View.INVISIBLE);
	// TextView textView = (TextView) findViewById(R.id.sys_txt_qq);
	// textView.setText(textView.getText() + " ("
	// + sharedPref.getQQnickname() + ")");
	// }
	// }

	@SuppressWarnings("deprecation")
	private void initView() {
		SlipMainCenter.lyButtom.setVisibility(View.GONE);
		SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);
		SlipMainCenter.tvName.setText(R.string.set);
		SlipMainCenter.btnMsgtop.setVisibility(View.GONE);
		((Button) findViewById(R.id.sys_logout)).setOnClickListener(this);
		// mBtnSina = (Button) findViewById(R.id.sys_btn_sinaLink);
		// mBtnSina.setOnClickListener(this);
		// mBtnQQ = (Button) findViewById(R.id.sys_btn_tentLink);
		// mBtnQQ.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.sysset_meSet))
				.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.sysset_about))
				.setOnClickListener(this);
		lSetPass = (LinearLayout) findViewById(R.id.sysset_pass);
		vSetPass = (View) findViewById(R.id.sysset_view_pass);
		SharedPref sp = new SharedPref(SysSetActivity.this);
		if (sp.getQQStatus() || sp.getSinaStatus()) {
			lSetPass.setVisibility(View.GONE);
			vSetPass.setVisibility(View.GONE);
		}
		lSetPass.setOnClickListener(this);
		lyTaxi = (LinearLayout) findViewById(R.id.sysset_applyCar);
		lyShop = (LinearLayout) findViewById(R.id.sysset_applyShop);
		tvTaxi = (TextView) findViewById(R.id.sysset_tvTaxi);
		tvShop = (TextView) findViewById(R.id.sysset_tvShop);
		int type = Utility.mSession.getMe().getType();
		if (type == 1 || type == 2) {
			lyTaxi.setBackgroundResource(R.drawable.list_round_bottom);
			lyShop.setBackgroundResource(R.drawable.list_round_center);
			if (type == 1)
				tvShop.setText(R.string.already_shop);
			else
				tvTaxi.setText(R.string.already_taxi);
		}
		else if (Utility.mSession.getShopTaxi()) {
			lyTaxi.setBackgroundResource(R.drawable.list_round_bottom);
			lyShop.setBackgroundResource(R.drawable.list_round_center);
		}
		else {
			lyTaxi.setOnClickListener(this);
			lyShop.setOnClickListener(this);
		}
		sb = (SlipButton)findViewById(R.id.sysset_sound);
		if(null != Utility.mSession && Utility.mSession.getSound() == 0)
			sb.setCheck(true);
		else
			sb.setCheck(false);
		sb.SetOnChangedListener(new OnChangedListener() {
			
			@Override
			public void OnChanged(boolean CheckState) {
				// TODO Auto-generated method stub
				if(CheckState && null != Utility.mSession)
					Utility.mSession.storeSound(0);
				else if(null != Utility.mSession)
					Utility.mSession.storeSound(1);
			}
		});
		((LinearLayout) findViewById(R.id.sysset_clearCache))
				.setOnClickListener(this);
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
					if (Math.abs(velocityX) > ViewConfiguration.get(
							SysSetActivity.this)
							.getScaledMinimumFlingVelocity()) {
						if (e1.getX() - e2.getX() > 100
								&& Math.abs(velocityX) > 10) {
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
		((ScrollView) findViewById(R.id.sys_set_sc))
				.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						detector.onTouchEvent(event);
						return false;
					}
				});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sysset_about:
			startActivity(new Intent(SysSetActivity.this, AboutActivity.class));
			break;
		case R.id.sysset_meSet:
			startActivity(new Intent(SysSetActivity.this, MeSetActivity.class));
			break;
		case R.id.sys_logout:
			Utility.mSession.resetToken();
			Utility.mToken = null;
			new SharedPref(SysSetActivity.this).resetSinaQQ();
			SystemMsgService.MSGbadge = 0;
			Intent it = new Intent(SysSetActivity.this, LoginActivity.class);
			it.putExtra("logout", true);
			startActivity(it);
			break;
		// case R.id.sys_btn_sinaLink:
		// mWeibo = AbstractWeibo
		// .getWeibo(SysSetActivity.this, SinaWeibo.NAME);
		// mWeibo.setWeiboActionListener(weiboListener);
		// mWeibo.showUser(null);
		// break;
		// case R.id.sys_btn_tentLink:
		// mWeibo = AbstractWeibo.getWeibo(SysSetActivity.this,
		// TencentWeibo.NAME);
		// mWeibo.setWeiboActionListener(weiboListener);
		// mWeibo.showUser(null);
		// break;
		case R.id.sysset_pass:
			startActivity(new Intent(SysSetActivity.this, PwfixActivity.class));
			break;
		case R.id.sysset_applyCar:
			startActivity(new Intent(SysSetActivity.this,
					ApplyTaxiActivity.class));
			break;
		case R.id.sysset_applyShop:
			startActivity(new Intent(SysSetActivity.this,
					ApplyShopActivity.class));
			break;
		case R.id.sysset_clearCache:
			FinalBitmap fb = FinalBitmap.create(SysSetActivity.this);
			fb.clearCache();
			CustomProgressDialog.showMsg(SysSetActivity.this,
					getString(R.string.please_wait));
			new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					try {
						sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler.sendEmptyMessage(1);
				}

			}.start();
			break;
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			CustomProgressDialog.stopProgressDialog(SysSetActivity.this);
			ToastUtil.show(SysSetActivity.this, R.string.clear_ok);
		}

	};
	// public WeiboActionListener weiboListener = new WeiboActionListener() {
	// @Override
	// public void onComplete(AbstractWeibo abstractWeibo, int i,
	// HashMap<String, Object> stringObjectHashMap) {
	// String shareName = abstractWeibo.getDb().get("nickname");
	//
	// if (abstractWeibo.getId() == 1) {
	// sharedPref.saveSinaStatus(true, shareName);
	// } else if (abstractWeibo.getId() == 2) {
	// sharedPref.saveQQStatus(true, shareName);
	// }
	// Log.i("1234", abstractWeibo.getId() + "  id");
	// connected();
	// }
	//
	// @Override
	// public void onError(AbstractWeibo abstractWeibo, int i,
	// Throwable throwable) {
	//
	// }
	//
	// @Override
	// public void onCancel(AbstractWeibo abstractWeibo, int i) {
	//
	// }
	// };
}
