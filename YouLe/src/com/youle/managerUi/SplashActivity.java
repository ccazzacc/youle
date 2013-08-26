package com.youle.managerUi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.mobstat.StatActivity;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.HttpConnectCallback;
import com.koushikdutta.async.http.MultipartFormDataBody;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.LoginPref;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;


public class SplashActivity extends StatActivity {
    private final int TIME_UP = 1;
    private LoginPref lp;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == TIME_UP) {
                lp = new LoginPref(SplashActivity.this);
                if (lp.getLogin()) {
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    overridePendingTransition(R.anim.splash_screen_fade,
                            R.anim.splash_screen_hold);
                } else {
                    if (Utility.hasToken()) {
                        startActivity(new Intent(SplashActivity.this, SlidActivity.class));
                        overridePendingTransition(R.anim.splash_screen_fade,
                                R.anim.splash_screen_hold);
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.splash_screen_fade,
                                R.anim.splash_screen_hold);
                    }
                }
                lp.storeLogin(false);
                SplashActivity.this.finish();
            }
        }
    };

    //	private static final LatLng marker1 = new LatLng(30.667327,104.089328);
//	private static final LatLng marker2 = new LatLng(30.667327, 104.079373);
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.splash_activity);
        Utility.mSession = new YLSession(this);
        Utility.mToken = Utility.mSession.getToken();
        MyApplication.getInstance().addActivity(this);
        new Thread() {
            public void run() {
                try {
//					YouLe.getForums(SplashActivity.this, 1, 1, 20);
//					YouLe.scanCoupon(SplashActivity.this, "1000");
//					YouLe.getCoupon(SplashActivity.this, "0", "1", 5000, 104.089328, 30.667327);
//					float distance = AMapUtils.calculateLineDistance(marker1, marker2);
//					Log.i("test", "distance:"+distance);
                    if (Utility.hasToken()) {
                        String res = YouLe.getMeInfo(SplashActivity.this);
                        if (res.startsWith(GlobalData.RESULT_OK))
                            YouLe.jsonUserInfo(res.substring(3), true);
                    }

                    Thread.sleep(600);

                } catch (Exception e) {

                }
                Message msg = new Message();
                msg.what = TIME_UP;
                handler.sendMessage(msg);
            }
        }.start();
        saveTimeStamp();
    }

    private void saveTimeStamp() {
        if(Utility.mToken==null){
            return;
        }
        File path=new File(OtherUtil.getSDcard()+"/.cache/time_stamp/");
        if(!path.exists()){
            path.mkdir();
        }
        File[] files = path.listFiles();
        String fileName = null;
        long time=0;
        for (File file : files) {
            if(file.getName().startsWith("00")){
                time=Long.parseLong(file.getName());
                if(Calendar.getInstance().getTimeInMillis() / 1000-time<86400){
                    fileName=file.getAbsolutePath();
                }else{
                    if(OtherUtil.is3gWifi(SplashActivity.this)){
                        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
                        String imei = telephonyManager.getDeviceId();
                        if(imei==null){
                            imei= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                        }
                        StringBuffer sb=new StringBuffer();
                        try {
                            sb.append("{").append("\"token\"").append(":\"")
                                    .append(imei+"\",").append("\"type\":\"").append(0)
                                    .append("\",").append("\"version\":\"").append(Build.VERSION.RELEASE)
                                    .append("\",").append("\"manufacturer\":\"").append(Build.MANUFACTURER + "-" + Build.MODEL)
                                    .append("\",").append("\"app_version\":\"").append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName)
                                    .append("\",").append("\"logs\":[").append(getLogs()).append("]}");
                            Log.i("1234","sb:"+sb);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String url = new StringBuffer().append(YouLe.BASE_URL).append("log").toString();
                        AsyncHttpPost post = new AsyncHttpPost(url);
                        MultipartFormDataBody body = new MultipartFormDataBody();
                        body.addStringPart("access_token", Utility.mToken.getAccess_token());
                        body.addStringPart("data", sb+"");
                        post.setBody(body);
                        AsyncHttpClient.getDefaultInstance().execute(post,new HttpConnectCallback() {
                            @Override
                            public void onConnectCompleted(Exception e, AsyncHttpResponse asyncHttpResponse) {
                                if (e != null) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        });
                        file.delete();
                    }
                }
            }
        }



        if (fileName == null) {
            fileName =path+ "/00"+Calendar.getInstance().getTimeInMillis() / 1000 ;
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, true)));
            out.write(Calendar.getInstance().getTimeInMillis() / 1000 + ",");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String getLogs() {
        File path=new File(OtherUtil.getSDcard()+"/.cache/time_stamp/");

        File[] files =path.listFiles();
        String fileName = null;
        for (File file : files) {
            Log.i("1234", "file is " + file);
            if (file.getName().startsWith("00")) {
                fileName = file.getAbsolutePath();
            }
        }
        Log.i("1234","filename:"+fileName);
        try {
            FileInputStream inStream = new FileInputStream(fileName);
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int length=-1;
            while((length=inStream.read(buffer))!=-1)   {
                stream.write(buffer,0,length);
            }
            stream.close();
            inStream.close();
            Log.i("1234","stream: "+stream.toString());
            return stream.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}