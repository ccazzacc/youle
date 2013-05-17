package com.youle.managerUi;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;


public class RegisterActivity extends Activity implements OnClickListener{
	private EditText etEmail, etName, etPw;
	private Context context;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		context = RegisterActivity.this;
		initView();
	}

	private void initView() {
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		Button btnReg = (Button)findViewById(R.id.register_btn);
		btnBack.setBackgroundResource(R.drawable.bar_button_close);
		((TextView)findViewById(R.id.twobtn_header_tv)).setText(R.string.register);
		btnBack.setOnClickListener(this);
		btnReg.setOnClickListener(this);
//		btnBack.setText(R.string.back);
		btnBack.setVisibility(View.VISIBLE);
		etEmail = (EditText) this.findViewById(R.id.register_et_email);
		etEmail.setOnFocusChangeListener(new EditListener());
		etEmail.requestFocus();
		etName = (EditText) this.findViewById(R.id.register_et_name);
		etName.setOnFocusChangeListener(new EditListener());
		etPw = (EditText) this.findViewById(R.id.register_et_pw);
		etPw.setOnFocusChangeListener(new EditListener());
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
						ToastUtil.showToast(context,
								getString(R.string.input_correct_email));
					}
				}
				break;
			case R.id.register_et_name:
				if (etName.hasFocus() == false) {
					if (!TextUtils.isEmpty(etName.getText().toString())
							&& etName.getText().toString().length() < 2) {
						ToastUtil.showToast(context,
								getString(R.string.error_userName_short));
					}
				}
				break;
			case R.id.register_et_pw:
				if (etPw.hasFocus() == false) {
					if (!TextUtils.isEmpty(etPw.getText().toString())
							&& etPw.getText().toString().length() < 6) {
						ToastUtil.showToast(context,
								getString(R.string.error_pw_short));
					}
				}
				break;
			}
		}
	}
	private void showPb(Context context) {
		pd = new ProgressDialog(context);
		pd.setMessage(context.getString(R.string.please_wait));
		pd.show();
	}

	private void dissPb() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}
	private class UpMsgTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showPb(context);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return YouLe.register(context, etEmail.getText().toString(),
					etName.getText().toString(), etPw.getText().toString());
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dissPb();
			if (result.equals(GlobalData.RESULT_OK)) {
				startActivity(new Intent(RegisterActivity.this,
						MainActivity.class));
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				ToastUtil.showToast(context,R.string.register_success);
				RegisterActivity.this.finish();
			}else
			{
//				ToastUtil.showRegToast(context, result);
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
			if (isExit == false) {
				isExit = true;
				Toast.makeText(RegisterActivity.this, R.string.press_again,
						Toast.LENGTH_SHORT).show();
				if (!hasTask) {
					tExit.schedule(task, 2000);
				}
			} else {
				RegisterActivity.this.finish();
			}
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		context = null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.twobtn_header_left:
			RegisterActivity.this.finish();
			break;
		case R.id.register_btn:
			if (TextUtils.isEmpty(etEmail.getText().toString())
					|| (etEmail.getText().toString()).equals("")
					|| !OtherUtil.isEmail(etEmail.getText().toString())) {
				if (!OtherUtil.isEmail(etEmail.getText().toString()))
					ToastUtil.showToast(context,
							getString(R.string.input_correct_email));
				else
					ToastUtil.showToast(context,
							getString(R.string.input_email));
			} else if (TextUtils.isEmpty(etName.getText().toString())
					|| (etName.getText().toString()).equals("")
					|| etName.getText().toString().length() < 2) {
				if (etName.getText().toString().length() < 2)
					ToastUtil.showToast(context,
							getString(R.string.error_userName_short));
				else
					ToastUtil.showToast(context,
							getString(R.string.input_name));
			} else if (TextUtils.isEmpty(etPw.getText().toString())
					|| (etPw.getText().toString()).equals("")
					|| etPw.getText().toString().length() < 6) {
				if (etPw.getText().toString().length() < 6)
					ToastUtil.showToast(context,
							getString(R.string.error_pw_short));
				else
					ToastUtil.showToast(context,
							getString(R.string.input_pw));
			} else {
				if(OtherUtil.is3gWifi(RegisterActivity.this))
				{
					new UpMsgTask().execute();
				}else
				{
					ToastUtil.showToast(RegisterActivity.this, getString(R.string.please_check_net));
				}
			}
			break;
		}
	}

}