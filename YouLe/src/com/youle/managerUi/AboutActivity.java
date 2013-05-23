package com.youle.managerUi;

import com.youle.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AboutActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.about);
	}
	
}
