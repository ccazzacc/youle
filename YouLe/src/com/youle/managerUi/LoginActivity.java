package com.youle.managerUi;

import android.app.Activity;
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
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class LoginActivity extends Activity {
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
                case R.id.twobtn_header_right:
                    submit();
                    break;
                case R.id.text_reg:
                    startActivity(new Intent(LoginActivity.this,
                            RegisterActivity.class));
                    finish();
                    break;
                case R.id.text_forgot_psd:
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setData(Uri.parse("http://www.baidu.com/"));
                    it = Intent.createChooser(it, null);
                    startActivity(it);
                    finish();
                    break;
            }
        }

    };
    private Button mSinaLogin, mQQLogin, mClose;
    private AbstractWeibo mWeibo;
    private EditText mEditUname, mEditPsd;
    private Button mBtnSub;
    private String mWbName, mWbUid, mWbAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        Button close = (Button) findViewById(R.id.twobtn_header_left);
        close.setBackgroundResource(R.drawable.bar_button_close_normal);
        close.setVisibility(View.VISIBLE);
        TextView tit = (TextView) findViewById(R.id.twobtn_header_tv);
        tit.setText(getString(R.string.login));
        TextView reg = (TextView) findViewById(R.id.text_reg);
        reg.setOnClickListener(click);
        TextView forgot = (TextView) findViewById(R.id.text_forgot_psd);
        forgot.setOnClickListener(click);

    }

    private void submit() {
        if (OtherUtil.isNullOrEmpty(mEditUname.getText().toString())
                || OtherUtil.isNullOrEmpty(mEditPsd.getText().toString())) {
            ToastUtil.show(LoginActivity.this, "请输入完整信息");
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
            Log.i("1234", "" + result);
            if (!result.startsWith(GlobalData.RESULT_OK)) {
                ToastUtil.showToast(LoginActivity.this,result);
                mEditPsd.setText("");
                mEditUname.setText("");
            } else {
                // 登录成功
                startActivity(new Intent(LoginActivity.this,MapActivity.class));
                finish();
            }
        }

    }

}
