package com.youle.managerUi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.managerData.MyApplication;

/**
 * Created by zhaofuchao on 13-5-27.
 */
public class ForgotPsdActivity extends StatActivity implements View.OnClickListener{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_psd_activity);
        initView();
        MyApplication.getInstance().addActivity(this);
    }

    private void initView() {
        Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.twobtn_header_tv);
        textView.setText(getString(R.string.forgot_password));
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}