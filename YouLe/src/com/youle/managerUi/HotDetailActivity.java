package com.youle.managerUi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.GlobalData;

public class HotDetailActivity extends StatActivity {
    View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.hot_detail_back:
                    finish();
                    break;
                case R.id.hot_detail_re:
                    Intent intent=new Intent();
                    intent.putExtra(GlobalData.TOPIC_ID,""+mTopicId);
                    intent.putExtra("DETAIL",true);
                    intent.setClass(HotDetailActivity.this, HotReplyActivity.class);
                    startActivity(intent);
//                    finish();
                    break;
                case R.id.hot_detail_posts:
                    Intent intent1=new Intent();
                    intent1.putExtra(GlobalData.TOPIC_ID,""+mTopicId);
                    intent1.setClass(HotDetailActivity.this,HotCommentActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    };
    private int mTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hot_detail_activity);
        MyApplication.getInstance().addActivity(this);
        initView();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("1234", "start onRestart~~~");
    }

    private void initView() {
        int mReplies = getIntent().getIntExtra("replies", 0);
        mTopicId=getIntent().getIntExtra("topic_id",0);
        String url=getIntent().getStringExtra("uri");
        url=url+"?access_token="+ Utility.mToken.getAccess_token()+"&radio_id="
                +new SharedPref(HotDetailActivity.this).getRadioId();
        Button btnBack = (Button) findViewById(R.id.hot_detail_back);
        Button btnPosts = (Button) findViewById(R.id.hot_detail_posts);
        btnPosts.setText(mReplies + getString(R.string.posts));
        btnPosts.setOnClickListener(click);
        RelativeLayout re=(RelativeLayout)findViewById(R.id.hot_detail_re);
        btnBack.setOnClickListener(click);
        re.setOnClickListener(click);
        WebView webView = (WebView) findViewById(R.id.hot_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 90) {
                    ProgressBar pb = (ProgressBar) findViewById(R.id.hot_pd);
                    pb.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        Log.i("1234",""+url);
        webView.loadUrl(url);

    }
}
