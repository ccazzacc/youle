package com.youle.managerUi;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;

public class RegisterActivity extends StatActivity implements OnClickListener {
	private EditText etEmail, etName, etPw;
	private Context context;
	private ImageView ivEmail, ivName, ivPw;
	private int i=0;
	private boolean isLogout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		context = RegisterActivity.this;
		initView();
		MyApplication.getInstance().addActivity(this);
	}

	private void initView() {
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		Button btnReg = (Button) findViewById(R.id.register_btn);
		btnBack.setBackgroundResource(R.drawable.bar_button_close_normal);
		((TextView) findViewById(R.id.twobtn_header_tv))
				.setText(R.string.register);
		btnBack.setOnClickListener(this);
		btnReg.setOnClickListener(this);
		// btnBack.setText(R.string.back);
		btnBack.setVisibility(View.VISIBLE);
		etEmail = (EditText) this.findViewById(R.id.register_et_email);
		etEmail.setOnFocusChangeListener(new EditListener());
		etEmail.requestFocus();
		etName = (EditText) this.findViewById(R.id.register_et_name);
		etName.setOnFocusChangeListener(new EditListener());
		etPw = (EditText) this.findViewById(R.id.register_et_pw);
		etPw.setOnFocusChangeListener(new EditListener());
		isLogout = this.getIntent().getBooleanExtra("logout", false);
		etPw.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) {
					i++;
					if(i%2==1)
						register();
				}
				return false;
			}
		});
		ivEmail = (ImageView) findViewById(R.id.register_iv_email);
		ivName = (ImageView) findViewById(R.id.register_iv_name);
		ivPw = (ImageView) findViewById(R.id.register_iv_pw);
	}

	class EditListener implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.register_et_email:
				if (etEmail.hasFocus() == false) {
					if (!TextUtils.isEmpty(etEmail.getText().toString())
							&& !OtherUtil.isEmail(etEmail.getText().toString())) {
						ivEmail.setVisibility(View.VISIBLE);
						ToastUtil.show(context,
								getString(R.string.input_correct_email));
					} else
						ivEmail.setVisibility(View.GONE);
				}
				break;
			case R.id.register_et_name:
				if (etName.hasFocus() == false) {
					if (!TextUtils.isEmpty(etName.getText().toString())
							&& etName.getText().toString().length() < 2) {
						ivName.setVisibility(View.VISIBLE);
						ToastUtil.show(context,
								getString(R.string.error_userName_short));
					} else
						ivName.setVisibility(View.GONE);
				}
				break;
			case R.id.register_et_pw:
				if (etPw.hasFocus() == false) {
					if (!TextUtils.isEmpty(etPw.getText().toString())
							&& etPw.getText().toString().length() < 6) {
						ivPw.setVisibility(View.VISIBLE);
						ToastUtil.show(context,
								getString(R.string.error_pw_short));
					} else
						ivPw.setVisibility(View.GONE);
				}
				break;
			}
		}
	}

	private class UpMsgTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(context, getString(R.string.please_wait));
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return YouLe.register(context, etEmail.getText().toString(), etName
					.getText().toString(), etPw.getText().toString());
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(RegisterActivity.this);
			if (result.equals(GlobalData.RESULT_OK)) {
				startActivity(new Intent(RegisterActivity.this,
						SlidActivity.class));
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				ToastUtil.show(context, R.string.register_success);
				RegisterActivity.this.finish();
			} else {
				ToastUtil.showToast(context, result);
			}

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(RegisterActivity.this);
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
					Toast.makeText(RegisterActivity.this, R.string.press_again,
							Toast.LENGTH_SHORT).show();
					if (!hasTask) {
						tExit.schedule(task, 2000);
					}
				} else {
					MyApplication.getInstance().exit();
				}
			}else
				RegisterActivity.this.finish();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		context = null;
	}

	private void register() {
		if (TextUtils.isEmpty(etEmail.getText().toString())
				|| (etEmail.getText().toString()).equals("")
				|| !OtherUtil.isEmail(etEmail.getText().toString())) {
			if (!OtherUtil.isEmail(etEmail.getText().toString()))
				ToastUtil
						.show(context, getString(R.string.input_correct_email));
			else
				ToastUtil.show(context, getString(R.string.input_email));
		} else if (TextUtils.isEmpty(etName.getText().toString())
				|| (etName.getText().toString()).equals("")
				|| etName.getText().toString().length() < 2) {
			if (etName.getText().toString().length() < 2)
				ToastUtil.show(context,
						getString(R.string.error_userName_short));
			else
				ToastUtil.show(context, getString(R.string.input_name));
		} else if (TextUtils.isEmpty(etPw.getText().toString())
				|| (etPw.getText().toString()).equals("")
				|| etPw.getText().toString().length() < 6) {
			if (etPw.getText().toString().length() < 6)
				ToastUtil.show(context, getString(R.string.error_pw_short));
			else
				ToastUtil.show(context, getString(R.string.input_pw));
		} else {
			if (OtherUtil.is3gWifi(RegisterActivity.this)) {
				new UpMsgTask().execute();
			} else {
				ToastUtil.show(RegisterActivity.this,
						getString(R.string.please_check_net));
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.twobtn_header_left:
			RegisterActivity.this.finish();
			break;
		case R.id.register_btn:
			register();
			break;
		}
	}

}
