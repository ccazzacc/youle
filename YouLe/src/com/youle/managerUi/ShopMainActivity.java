package com.youle.managerUi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.youle.R;
import com.zxing.activity.CaptureActivity;

public class ShopMainActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_mainactivity);
		initView();
	}
	private void initView()
	{
		((ImageView)findViewById(R.id.shop_btnList)).setOnClickListener(this);
		((ImageView)findViewById(R.id.shop_btnScan)).setOnClickListener(this);
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView)findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.im_shop);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.shop_btnList:
			startActivity(new Intent(ShopMainActivity.this,ConsumeActivity.class));
			overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;

		case R.id.shop_btnScan:
			//打开扫描界面扫描条形码或二维码
			Intent openCameraIntent = new Intent(ShopMainActivity.this,CaptureActivity.class);
			startActivityForResult(openCameraIntent, 0);
			break;
		case R.id.twobtn_header_left:
			ShopMainActivity.this.finish();
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//处理扫描结果
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			if(null != scanResult && !TextUtils.isEmpty(scanResult))
				showDialog(scanResult);
		}
	}
	private void showDialog(String result) {
		new AlertDialog.Builder(this)
				.setTitle((new StringBuffer().append("您扫描的券号：").append(result)).toString())
				.setPositiveButton(getString(R.string.use), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}
}
