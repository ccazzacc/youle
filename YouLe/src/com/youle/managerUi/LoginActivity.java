package com.youle.managerUi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.util.ToastUtil;

public class LoginActivity extends Activity {
	private Button mSinaLogin, mQQLogin,mClose;
	private AbstractWeibo mWeibo;
	private EditText mEditUname, mEditPsd;
	private Button mBtnSub;
	private String mWbName, mWbUid, mWbAvatar;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		AbstractWeibo.initSDK(this);
		initView();
	}

	private void initView() {
		mSinaLogin = (Button) findViewById(R.id.sina_login);
		mQQLogin = (Button) findViewById(R.id.qq_login);
		mSinaLogin.setOnClickListener(click);
		mQQLogin.setOnClickListener(click);
		mEditPsd = (EditText) findViewById(R.id.edit_upsd);
		mEditPsd.setOnKeyListener(onkey);
		mEditUname = (EditText) findViewById(R.id.edit_uname);
		mBtnSub = (Button) findViewById(R.id.twobtn_header_right);
		mBtnSub.setBackgroundResource(R.drawable.bar_btn_login);
		mBtnSub.setOnClickListener(click);
		mBtnSub.setVisibility(View.VISIBLE);
		Button close=(Button)findViewById(R.id.twobtn_header_left);
		close.setBackgroundResource(R.drawable.bar_button_close_normal);
		close.setVisibility(View.VISIBLE);
		TextView reg=(TextView)findViewById(R.id.text_reg);
		reg.setOnClickListener(click);
	}
	OnKeyListener onkey=new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode==66){
				submit();
			}
			return false;
		}
	};

	OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sina_login:
				mWeibo = AbstractWeibo.getWeibo(LoginActivity.this,
						SinaWeibo.NAME);
				mWeibo.setWeiboActionListener(weiboListener);
				mWeibo.showUser(null);
				break;
			case R.id.qq_login:
				mWeibo = AbstractWeibo.getWeibo(LoginActivity.this,
						TencentWeibo.NAME);
				mWeibo.setWeiboActionListener(weiboListener);
				mWeibo.showUser(null);
				break;
			case R.id.twobtn_header_right:
				submit();
				break;
			case R.id.text_reg:
				startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
				finish();
				break;
			default:

				break;
			}
		}

		
	};
	private void submit() {
		if (mEditPsd.getText().toString() == ""
				|| mEditUname.getText().toString() == "") {
			ToastUtil.showToast(LoginActivity.this, "请输入完整信息");
			return;
		}
		
		String[] nameAndPsd = new String[2];
		nameAndPsd[0] = mEditPsd.getText().toString();
		nameAndPsd[1] = mEditUname.getText().toString();
		new SubmitTask().execute(nameAndPsd);
	}

	private class SubmitTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.i("1234", " "+mEditPsd.getText()+" "+mEditUname.getText());
			Utility.mSession=new YLSession(LoginActivity.this);
			String s = YouLe.loginApp(LoginActivity.this, params[1], params[0]);
			Log.i("1234", s+" "+params[0]+" "+ params[1]);
			return s;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("1234", ""+result);
			if(!result.equals("999")){
				ToastUtil.showToast(LoginActivity.this, R.string.oauth_fail);
				mEditPsd.setText("");
				mEditUname.setText("");
			}else{
				//登录成功
				finish();
			}
		}

	}

	public WeiboActionListener weiboListener = new WeiboActionListener() {

		@Override
		public void onError(AbstractWeibo arg0, int arg1, Throwable arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(AbstractWeibo weibo, int arg1,
				HashMap<String, Object> res) {
			// TODO Auto-generated method stub
			Log.i("1234", "" + weibo.getAuthedUserName() + "\n"
					+ weibo.getDb().getWeiboId() + "id::" + weibo.getId());
			if (weibo.getId() == 1) {
				try {
					Log.i("1234",
							"sss  "
									+ getJSONObject(res).getString(
											"avatar_large"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					Log.i("1234",
							"sss  "
									+ getJSONObject(res)
											.getString("https_head") + "/120");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// 05-13 15:46:59.519: I/1234(30593): sss
		// https://app.qlogo.cn/mbloghead/7f363df6040b0e93b6a4/120

		@Override
		public void onCancel(AbstractWeibo arg0, int arg1) {
			// TODO Auto-generated method stub
			// 05-13 14:41:02.476: I/System.out(22650): Redirect URL:
			// http://pathtrip.com/callback#access_token=2.00rGyYaBVXWwmB4f8e8d63eaDQv7yD&remind_in=1253937&expires_in=1253937&uid=1456415145
			// 05-13 15:03:29.180: I/1234(25830):
			// "avatar_large":"http://tp2.sinaimg.cn/1456415145/180/1300353985/1",

		}
	};

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

}
