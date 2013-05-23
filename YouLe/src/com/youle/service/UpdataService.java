package com.youle.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.MultipartFormDataBody;

import java.io.File;

public class UpdataService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    private void upLoad(){
        AsyncHttpPost post=new AsyncHttpPost("");
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addFilePart("my-file", new File("/path/to/file.txt"));
        body.addStringPart("foo", "bar");
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().execute(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                System.out.println("Server says: " + result);
            }
        });
    }
	

}
