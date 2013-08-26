package com.youle.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.youle.R;
import com.youle.util.OtherUtil;

/**
 * Created by zhaofuchao on 13-6-25.
 */
public class ShowDialog extends Activity {
	private boolean two = false;
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dialog_btnn:
                    finish();
                    break;
                case R.id.dialog_btny:
                	if(two)
                	{
                		Intent it = new Intent();
                    	it.putExtra("yes", "yes");
                    	setResult(0, it);
                	}
                    finish();
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_dialog);
        initView();
    }

    private void initView() {
    	two = getIntent().getExtras().getBoolean("showTwo");
        String tit = getIntent().getExtras().getString("dialog_title");
        String text = getIntent().getExtras().getString("dialog_text");
        String yes = getIntent().getExtras().getString("yes");
        String no = getIntent().getExtras().getString("no");
        Button buttonNo = (Button) findViewById(R.id.dialog_btnn);
        Button buttonYes = (Button) findViewById(R.id.dialog_btny);
        if (two) {
            buttonNo.setVisibility(View.VISIBLE);
        }
        if(!OtherUtil.isNullOrEmpty(yes))
        	buttonYes.setText(yes);
        if(!OtherUtil.isNullOrEmpty(no))
        	buttonNo.setText(no);
        TextView textViewTit = (TextView) findViewById(R.id.dialog_tit);
        TextView textViewText = (TextView) findViewById(R.id.dialog_txt);
        textViewTit.setText(tit);
        textViewText.setText(text);
        buttonNo.setOnClickListener(click);
        buttonYes.setOnClickListener(click);
    }
}