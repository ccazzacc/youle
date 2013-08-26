package com.youle.managerUi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;

public class PwfixActivity extends StatActivity implements OnClickListener {
	private EditText etOld, etNew;
	private ImageView ivError1, ivError2;
	private int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_fix);
		initView();
		MyApplication.getInstance().addActivity(this);
	}

	private void initView() {
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.set_account);
		((Button) findViewById(R.id.passFix_btn)).setOnClickListener(this);
		etOld = (EditText) findViewById(R.id.passFix_et_pw1);
		etOld.requestFocus();
		etNew = (EditText) findViewById(R.id.passFix_et_pw2);
		etOld.setOnFocusChangeListener(new EditListener());
		etNew.setOnFocusChangeListener(new EditListener());
		etNew.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) {
					i++;
					if (i % 2 == 1)
						sendFix();
				}
				return false;
			}
		});
		ivError1 = (ImageView) findViewById(R.id.passFix_iv_pw1);
		ivError2 = (ImageView) findViewById(R.id.passFix_iv_pw2);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.twobtn_header_left:
			PwfixActivity.this.finish();
			break;
		case R.id.passFix_btn:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(PwfixActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			sendFix();
			break;
		}
	}

	private void sendFix() {
		if (TextUtils.isEmpty(etOld.getText().toString())) {
			ivError1.setVisibility(View.VISIBLE);
			ToastUtil.show(PwfixActivity.this, R.string.input_pw_old);
			return;
		}
		if (!TextUtils.isEmpty(etOld.getText().toString())
				&& etOld.getText().toString().length() < 6) {
			ivError1.setVisibility(View.VISIBLE);
			ToastUtil.show(PwfixActivity.this, R.string.error_pw_short);
			return;
		} else
			ivError1.setVisibility(View.GONE);
		if (TextUtils.isEmpty(etNew.getText().toString())) {
			ivError2.setVisibility(View.VISIBLE);
			ToastUtil.show(PwfixActivity.this, R.string.input_pw_new);
			return;
		}
		if (!TextUtils.isEmpty(etNew.getText().toString())
				&& etNew.getText().toString().length() < 6) {
			ivError2.setVisibility(View.VISIBLE);
			ToastUtil.show(PwfixActivity.this, R.string.error_pw_short);
			return;
		} else
			ivError2.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(etOld.getText().toString())
				&& etOld.getText().toString().length() >= 6
				&& !TextUtils.isEmpty(etNew.getText().toString())
				&& etNew.getText().toString().length() >= 6) {
			if (etOld.getText().toString().equals(etNew.getText().toString())) {
				ivError2.setVisibility(View.VISIBLE);
				ToastUtil.show(PwfixActivity.this, R.string.error_samePw);
			} else
				if(!OtherUtil.is3gWifi(this))
		        	ToastUtil.show(this, R.string.net_no);
		        else
		        	new FixPwTask().execute();
		}
	}

	class EditListener implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.passFix_et_pw1:
				if (etOld.hasFocus() == false) {
					if (!TextUtils.isEmpty(etOld.getText().toString())
							&& etOld.getText().toString().length() < 6) {
						ivError1.setVisibility(View.VISIBLE);
						ToastUtil.show(PwfixActivity.this,
								R.string.error_pw_short);
					} else
						ivError1.setVisibility(View.GONE);
				}
				break;
			case R.id.passFix_et_pw2:
				if (etNew.hasFocus() == false) {
					if (!TextUtils.isEmpty(etNew.getText().toString())
							&& etNew.getText().toString().length() < 6) {
						ivError2.setVisibility(View.VISIBLE);
						ToastUtil.show(PwfixActivity.this,
								R.string.error_pw_short);
					} else
						ivError2.setVisibility(View.GONE);
				}
				break;
			}
		}
	}

	private class FixPwTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(PwfixActivity.this);
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				ToastUtil.show(PwfixActivity.this, R.string.fix_ok);
				PwfixActivity.this.finish();
			} else if (result.equals("wrong password")) {
				ToastUtil.show(PwfixActivity.this, R.string.error_pw1);
			} else {
				ToastUtil.showToast(PwfixActivity.this, result);
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return YouLe.fixPassword(PwfixActivity.this, etOld.getText()
					.toString(), etNew.getText().toString());
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(PwfixActivity.this,
					getString(R.string.please_wait));
		}

	}
}
