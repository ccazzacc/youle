package com.youle.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.youle.R;

/**
 * Created by zhaofuchao on 13-6-25.
 */
public class ShowDialog extends Activity {
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dialog_btnn:
                    finish();
                    break;
                case R.id.dialog_btny:
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
        boolean two = getIntent().getExtras().getBoolean("showTwo");
        String tit = getIntent().getExtras().getString("dialog_title");
        String text = getIntent().getExtras().getString("dialog_text");

        Button buttonNo = (Button) findViewById(R.id.dialog_btnn);
        Button buttonYes = (Button) findViewById(R.id.dialog_btny);
        if (two) {
            buttonNo.setVisibility(View.VISIBLE);
        }
        TextView textViewTit = (TextView) findViewById(R.id.dialog_tit);
        TextView textViewText = (TextView) findViewById(R.id.dialog_txt);
        textViewTit.setText(tit);
        textViewText.setText(text);
        buttonNo.setOnClickListener(click);
        buttonYes.setOnClickListener(click);
    }
}