package com.youle.managerUi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;

public class GuideActivity extends StatActivity implements OnClickListener {

	// private static final int TO_THE_END = 0;// 到达最后一张
	// private static final int LEAVE_FROM_END = 1;// 离开最后一张

	private int[] ids = { R.drawable.welcome1, R.drawable.welcome2,
			R.drawable.welcome3 };

	private List<View> guides = new ArrayList<View>();
	private ViewPager pager;
	// private ImageView open;
	private ImageView curDot, ivLogin, ivReg, ivSina, ivTencent;
	private int offset;// 位移量
	private int curPos = 0;// 记录当前的位置
	private AbstractWeibo mWeibo;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		AbstractWeibo.initSDK(this);
		MyApplication.getInstance().addActivity(this);
		for (int i = 0; i < ids.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setImageResource(ids[i]);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT);
			iv.setLayoutParams(params);
			iv.setScaleType(ScaleType.FIT_XY);
			guides.add(iv);
		}
		curDot = (ImageView) findViewById(R.id.cur_dot);
		ivLogin = (ImageView) findViewById(R.id.hy_login);
		ivLogin.setOnClickListener(this);
		ivReg = (ImageView) findViewById(R.id.hy_register);
		ivReg.setOnClickListener(this);
		ivSina = (ImageView) findViewById(R.id.hy_sina);
		ivSina.setOnClickListener(this);
		ivTencent = (ImageView) findViewById(R.id.hy_tencent);
		ivTencent.setOnClickListener(this);
		curDot.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						offset = curDot.getWidth();
						return true;
					}
				});

		GuidePagerAdapter adapter = new GuidePagerAdapter(guides);
		pager = (ViewPager) findViewById(R.id.contentPager);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				moveCursorTo(arg0);
				// if (arg0 == ids.length - 1) {//到最后一张了
				// handler.sendEmptyMessageDelayed(TO_THE_END, 500);
				// } else if (curPos == ids.length - 1) {
				// handler.sendEmptyMessageDelayed(LEAVE_FROM_END, 100);
				// }
				curPos = arg0;
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}

		});
	}

	/**
	 * 移动指针到相邻的位置
	 * 
	 * @param position
	 *            指针的索引值
	 * */
	private void moveCursorTo(int position) {
		TranslateAnimation anim = new TranslateAnimation(offset * curPos,
				offset * position, 0, 0);
		anim.setDuration(300);
		anim.setFillAfter(true);
		curDot.startAnimation(anim);
	}

	// Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// if (msg.what == TO_THE_END)
	// open.setVisibility(View.VISIBLE);
	// else if (msg.what == LEAVE_FROM_END)
	// open.setVisibility(View.GONE);
	// }
	// };
	class GuidePagerAdapter extends PagerAdapter {

		private List<View> views;

		public GuidePagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

	public WeiboActionListener weiboListener = new WeiboActionListener() {

		@Override
		public void onError(AbstractWeibo arg0, int arg1, Throwable arg2) {

		}

		@Override
		public void onComplete(AbstractWeibo weibo, int arg1,
				HashMap<String, Object> res) {
			SharedPref sharedPref = new SharedPref(GuideActivity.this);
			String shareName = weibo.getDb().get("nickname");
			Log.i("1234", "111 weiboId:" + weibo.getId() + " ; "
					+ weibo.getDb().get("nickname") + " id: "
					+ weibo.getDb().getWeiboId());

			 String[] nameAndPsd = new String[3];
			 nameAndPsd[0] = weibo.getId()+"";
			 nameAndPsd[1] = weibo.getDb().getWeiboId();
			 nameAndPsd[2] = weibo.getDb().get("nickname");
			 if(!OtherUtil.is3gWifi(GuideActivity.this))
		        	ToastUtil.show(GuideActivity.this, R.string.net_no);
		        else
		        	new SubmitTask().execute(nameAndPsd);

			if (weibo.getId() == 1) {
				try {
					Log.i("1234",
							"av  "
									+ getJSONObject(res).getString(
											"avatar_large"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				sharedPref.saveSinaStatus(true, shareName);
			} else {
				try {
					Log.i("1234",
							"sss  "
									+ getJSONObject(res)
											.getString("https_head") + "/120");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				sharedPref.saveQQStatus(true, shareName);
			}
			GuideActivity.this.finish();

		}

		@Override
		public void onCancel(AbstractWeibo arg0, int arg1) {

		}
	};

	private class SubmitTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.i("1234", " aaaa:" + params[0] + ";" + params[1] + ";"
					+ params[2]);
			Utility.mSession = new YLSession(GuideActivity.this);
			// 第三方登录
			String s = YouLe.ThirdPlate(GuideActivity.this, params[0],
					params[1], params[2]);
			Log.i("1234", s + " " + params[1] + " " + params[2]);
			return s;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.i("1234", "PostExecute " + result);
			if (!result.startsWith(GlobalData.RESULT_OK)) {
				ToastUtil.showToast(GuideActivity.this, result);
			} else {
				// 登录成功
				startActivity(new Intent(GuideActivity.this, SlidActivity.class));
				finish();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONObject(HashMap<String, Object> map)
			throws JSONException {
		JSONObject json = new JSONObject();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof HashMap<?, ?>) {
				value = getJSONObject((HashMap<String, Object>) value);
			} else if (value instanceof ArrayList<?>) {
				value = getJSONArray((ArrayList<Object>) value);
			}
			json.put(entry.getKey(), value);
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getJSONArray(ArrayList<Object> list) throws JSONException {
		JSONArray array = new JSONArray();
		for (Object value : list) {
			if (value instanceof HashMap<?, ?>) {
				value = getJSONObject((HashMap<String, Object>) value);
			} else if (value instanceof ArrayList<?>) {
				value = getJSONArray((ArrayList<Object>) value);
			}
			array.put(value);
		}
		return array;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hy_login:
			startActivity(new Intent(GuideActivity.this, LoginActivity.class));
			break;
		case R.id.hy_register:
			startActivity(new Intent(GuideActivity.this, RegisterActivity.class));
			break;
		case R.id.hy_sina:
			mWeibo = AbstractWeibo.getWeibo(GuideActivity.this, SinaWeibo.NAME);
			mWeibo.setWeiboActionListener(weiboListener);
			mWeibo.showUser(null);
			break;
		case R.id.hy_tencent:
			mWeibo = AbstractWeibo.getWeibo(GuideActivity.this,
					TencentWeibo.NAME);
			mWeibo.setWeiboActionListener(weiboListener);
			mWeibo.showUser(null);
			break;
		}
	}

}