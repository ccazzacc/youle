package com.youle.managerUi;

import com.youle.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PwfixActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_fix);
		initView();
	}
	private void initView()
	{
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.pw_fix);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.twobtn_header_left:
			PwfixActivity.this.finish();
			break;

		default:
			break;
		}
	}
}
