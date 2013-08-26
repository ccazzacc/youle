package com.youle.managerUi;

import com.youle.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebMeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_me_activity);
		WebView webView = (WebView)findViewById(R.id.webview);
		webView.loadUrl("http://www.radiotrip.com");
	}

}
