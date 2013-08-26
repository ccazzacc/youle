package com.youle.managerUi;

import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.MultipartFormDataBody;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;

public class ApplyTaxiActivity extends StatActivity {
	private EditText mEdName, mEdPhone, mEdLicense, mEdCarNo, mEdCompany;
	private OnClickListener click = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.apply_btnCommit:
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(ApplyTaxiActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				if (OtherUtil.isNullOrEmpty(mEdName.getText().toString())
						|| OtherUtil.isNullOrEmpty(mEdPhone.getText()
								.toString())
						|| OtherUtil.isNullOrEmpty(mEdLicense.getText()
								.toString())
						|| OtherUtil.isNullOrEmpty(mEdCarNo.getText()
								.toString())
						|| OtherUtil.isNullOrEmpty(mEdCompany.getText()
								.toString())) {
					ToastUtil.show(ApplyTaxiActivity.this, R.string.please_com);
				} else {
					if(!OtherUtil.is3gWifi(ApplyTaxiActivity.this))
			        	ToastUtil.show(ApplyTaxiActivity.this, R.string.net_no);
			        else
			        	new AppTaxiTask().execute();
				}

				break;
			case R.id.twobtn_header_left:
				ApplyTaxiActivity.this.finish();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applytaxi_activity);
		initView();
		MyApplication.getInstance().addActivity(this);
	}

	private void initView() {
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(click);
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.apply_car);
		Button btnCommit = (Button) findViewById(R.id.apply_btnCommit);
		btnCommit.setOnClickListener(click);
		mEdName = (EditText) findViewById(R.id.apply_drName);
		mEdPhone = (EditText) findViewById(R.id.apply_etPhone);
		mEdLicense = (EditText) findViewById(R.id.apply_etLicense);
		mEdCarNo = (EditText) findViewById(R.id.apply_etCarNum);
		mEdCompany = (EditText) findViewById(R.id.apply_etCompany);
	}

	private class AppTaxiTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(ApplyTaxiActivity.this);
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				Utility.mSession.storeTaxi();
				ToastUtil.show(ApplyTaxiActivity.this, R.string.apply_success);
				Intent it = new Intent(ApplyTaxiActivity.this,
						SlidActivity.class);
				it.putExtra("flag", 6);
				startActivity(it);
				ApplyTaxiActivity.this.finish();
			} else if (result.equals("taxi already applied")) {
				ToastUtil.show(ApplyTaxiActivity.this, R.string.applying);
				ApplyTaxiActivity.this.finish();
			} else {
				ToastUtil.showToast(ApplyTaxiActivity.this, result);
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return YouLe.applyTaxi(ApplyTaxiActivity.this, new SharedPref(
					ApplyTaxiActivity.this).getRadioId() + "", mEdName
					.getText().toString(), mEdPhone.getText().toString(),
					mEdLicense.getText().toString(), mEdCompany.getText()
							.toString(), mEdCarNo.getText().toString());
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(ApplyTaxiActivity.this);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(ApplyTaxiActivity.this,
					getString(R.string.please_wait));
		}

	}
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// Intent it = new Intent(ApplyTaxiActivity.this,SlidActivity.class);
	// it.putExtra("flag", 6);
	// startActivity(it);
	// ApplyTaxiActivity.this.finish();
	// }
	// return true;
	// }
}
