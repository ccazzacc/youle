package com.youle.managerUi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;
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

public class LoginActivity extends StatActivity {
    public WeiboActionListener weiboListener = new WeiboActionListener() {

        @Override
        public void onError(AbstractWeibo arg0, int arg1, Throwable arg2) {

        }

        @Override
        public void onComplete(AbstractWeibo weibo, int arg1,
                               HashMap<String, Object> res) {
            SharedPref sharedPref = new SharedPref(LoginActivity.this);
            String shareName = weibo.getDb().get("nickname");
            Log.i("1234", "111 " + weibo.getDb().get("nickname") + " id: "
                    + weibo.getDb().getWeiboId());

            String[] nameAndPsd = new String[3];
            nameAndPsd[0] = weibo.getId()+"";
            nameAndPsd[1] = weibo.getDb().getWeiboId();
            nameAndPsd[2] = weibo.getDb().get("nickname");
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
            LoginActivity.this.finish();

        }

        @Override
        public void onCancel(AbstractWeibo arg0, int arg1) {

        }
    };
    OnKeyListener onkey = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 66) {
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
                case R.id.login_btn_login:
                    submit();
                    break;
                case R.id.text_reg:
                	Intent its = new Intent(LoginActivity.this,
                            RegisterActivity.class);
                	if(isLogout)
                		its.putExtra("logout", true);
                    startActivity(its);
                    break;
                case R.id.text_forgot_psd:
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    String RESET_PSD_URL = "http://www.scyoule.com/reset_passwd1";
                    it.setData(Uri.parse(RESET_PSD_URL));
                    it = Intent.createChooser(it, null);
                    startActivity(it);
                    finish();
                    break;
                case R.id.twobtn_header_left:
                	if(isLogout)
                		MyApplication.getInstance().exit();
                	else
                		LoginActivity.this.finish();
                	break;
            }
        }

    };
	
    private Button mSinaLogin, mQQLogin, mClose;
    private AbstractWeibo mWeibo;
    private EditText mEditUname, mEditPsd;
    private Button mBtnSub;
    private String mWbName, mWbUid, mWbAvatar;
    private boolean isSns,isLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        AbstractWeibo.initSDK(this);
        initView();
        MyApplication.getInstance().addActivity(this);
    }

    private void initView() {
        mSinaLogin = (Button) findViewById(R.id.sina_login);
        mQQLogin = (Button) findViewById(R.id.qq_login);
        mSinaLogin.setOnClickListener(click);
        mQQLogin.setOnClickListener(click);
        mEditPsd = (EditText) findViewById(R.id.edit_upsd);
        mEditPsd.setOnKeyListener(onkey);
        mEditUname = (EditText) findViewById(R.id.edit_uname);
        mBtnSub = (Button) findViewById(R.id.login_btn_login);
        mBtnSub.setOnClickListener(click);
//        mBtnSub.setVisibility(View.VISIBLE);
        Button close = (Button) findViewById(R.id.twobtn_header_left);
        close.setBackgroundResource(R.drawable.bar_button_close_normal);
        close.setVisibility(View.VISIBLE);
        close.setOnClickListener(click);
        TextView tit = (TextView) findViewById(R.id.twobtn_header_tv);
        tit.setText(getString(R.string.login));
        TextView reg = (TextView) findViewById(R.id.text_reg);
        reg.setOnClickListener(click);
        TextView forgot = (TextView) findViewById(R.id.text_forgot_psd);
        forgot.setOnClickListener(click);
        isLogout = this.getIntent().getBooleanExtra("logout", false);
    }

    private void submit() {
        if (OtherUtil.isNullOrEmpty(mEditUname.getText().toString())
                || OtherUtil.isNullOrEmpty(mEditPsd.getText().toString())) {
            ToastUtil.show(LoginActivity.this, getString(R.string.please_com));
            return;
        }

        String[] nameAndPsd = new String[3];
        nameAndPsd[0] = "0";
        nameAndPsd[1] = mEditUname.getText().toString();
        nameAndPsd[2] = mEditPsd.getText().toString();
        new SubmitTask().execute(nameAndPsd);
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

    private class SubmitTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            Utility.mSession = new YLSession(LoginActivity.this);
            String s;
            if (params[0].equals("0")) {
                s = YouLe.loginApp(LoginActivity.this, params[1], params[2]);
            }else{
                //第三方登录
                s= YouLe.ThirdPlate(LoginActivity.this,params[0],params[1],params[2]);
            }
            Log.i("1234", s + " " + params[1] + " " + params[2]);
            return s;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("1234", "re: " + result);
            if (!result.startsWith(GlobalData.RESULT_OK)) {
                if(result.equals("invalid email")){
                    result=""+getString(R.string.input_correct_email);
                }else if(result.equals("user not found")){
                    result=""+getString(R.string.user_not_found);
                }else if(result.equals("401")){
                    result=""+getString(R.string.pw_err);
                }
                ToastUtil.showToast(LoginActivity.this,result);
                mEditPsd.setText("");
                mEditUname.setText("");
            } else {
                // 登录成功
            	
                startActivity(new Intent(LoginActivity.this,SlidActivity.class));
                finish();
            }
        }
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
			if(isLogout)
			{
				if (isExit == false) {
					isExit = true;
					Toast.makeText(LoginActivity.this, R.string.press_again,
							Toast.LENGTH_SHORT).show();
					if (!hasTask) {
						tExit.schedule(task, 2000);
					}
				} else {
					MyApplication.getInstance().exit();
				}
			}else
				LoginActivity.this.finish();
		}
		return true;
	}

}
