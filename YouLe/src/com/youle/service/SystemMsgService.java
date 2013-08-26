package com.youle.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.HttpConnectCallback;
import com.koushikdutta.async.http.MultipartFormDataBody;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.managerUi.CouponDetailActivity;
import com.youle.managerUi.SlidActivity;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;

/**
 * Created by zhaofuchao on 13-7-1.
 */
public class SystemMsgService extends Service {
    public static int MSGbadge = 0;
    public static String Access_token;
    private int mMsgType, relative_id;
    NotificationManager nm;
    Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            OtherUtil.getLocation(SystemMsgService.this);
            mHandler.postDelayed(mRunnable, 600000);

        }
    };
    private MyBroadcastReciver reciver;
    private String mWsUrl = new StringBuffer().append(YouLe.WS_URL).append("private").toString();
    private int radioId;
    private Handler handler;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        String message = (String) msg.obj;
                        Log.i("1234", "message!" + message);
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            String content = jsonObject.getString("content");
                            content = YouLe.decodeUnicode(content);
                            MSGbadge = jsonObject.getInt("badge");
                            mMsgType = jsonObject.getInt("type");
                            relative_id = jsonObject.getInt("relative_id");
                            showNotification(content);
//{"content":"哈哈哈哈哈哈","badge":2,"type":1,"relative_id":1}
                            if (mMsgType == 4) {
                                if (Utility.mSession != null) {
                                    Utility.mSession.storeType(relative_id);
                                } else {
                                    new YLSession(SystemMsgService.this).storeType(relative_id);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        LatLng latLng= (LatLng) msg.obj;
                        String url = new StringBuffer().append(YouLe.BASE_URL).append("gps").toString();
                        AsyncHttpPost post = new AsyncHttpPost(url);
                        MultipartFormDataBody body = new MultipartFormDataBody();
                        body.addStringPart("access_token", Utility.mToken.getAccess_token());
                        body.addStringPart("lat", latLng.latitude+"");
                        body.addStringPart("lng", latLng.longitude+"");
                        post.setBody(body);
                        AsyncHttpClient.getDefaultInstance().execute(post,new HttpConnectCallback() {
                            @Override
                            public void onConnectCompleted(Exception e, AsyncHttpResponse asyncHttpResponse) {
                                if (e != null) {
                                    e.printStackTrace();
                                    return;
                                }
                                Log.i("1234","upGps OK!!");
                            }
                        });
                        Log.i("1234",latLng.latitude+" - "+latLng.longitude);
                        break;
                }

            }
        };
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (null != Utility.mToken && null != Utility.mToken.getAccess_token()) {
            Access_token = Utility.mToken.getAccess_token();
            Log.i("test", "Access_token---" + Access_token);
        }

        radioId = new SharedPref(SystemMsgService.this).getRadioId();
//        Access_token=Utility.mToken.getAccess_token();
//        getFromUser();
        wsTracks();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        getInfo();
    }

    private void getInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            Log.i("1234", imei + " - " + Build.MODEL + " - " + Build.MANUFACTURER + " - " + Build.VERSION.RELEASE + " - "
                    + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " - " + Calendar.getInstance().getTimeInMillis());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mHandler.post(mRunnable);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalData.BROADCAST_COUNTER_ACTION);
        reciver = new MyBroadcastReciver();
        this.registerReceiver(reciver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciver);
    }

    private void showNotification(String content) {
        //Notification管理器
        Notification notification = new Notification(R.drawable.notifi_launcher, content, System.currentTimeMillis());
        //后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间（不明白这个有什么用）
//        notification.defaults=Notification.DEFAULT_ALL;
        if(Utility.mSession.getSound()==0){
            notification.sound = Uri.parse("android.resource://com.youle/drawable/sms_received1");
        }
        Intent intent = null;
        switch (mMsgType) {
            case 1:
                intent = new Intent(SystemMsgService.this, CouponDetailActivity.class);
                intent.putExtra("uc_id", relative_id + "");
                break;
            case 2:
                intent = new Intent(SystemMsgService.this, SlidActivity.class);
                intent.putExtra("flag", 1);
                break;
            case 3:
                intent = new Intent(SystemMsgService.this, SlidActivity.class);
                intent.putExtra("flag", 5);
                break;
            case 4:
                intent = new Intent(SystemMsgService.this, SlidActivity.class);
                intent.putExtra("flag", 7);
                break;
            case 5:
                intent = new Intent(SystemMsgService.this, SlidActivity.class);
                intent.putExtra("flag", 1);
                break;

        }


        PendingIntent pt = PendingIntent.getActivity(this, 0, intent, 0);
        //点击通知后的动作，这里是转回main 这个Acticity
        notification.setLatestEventInfo(this, getString(R.string.app_name), content, pt);
        nm.notify(YouLe.NOTIFICATION_ID, notification);

    }

    //{"content": "\u6210\u90fd\u5e7f\u64ad\u7535\u53f0\u521a\u8d60\u9001\u4e00\u5f20\u4f18\u60e0\u52b5\u7ed9\u60a8\uff0c\u8bf7\u6ce8\u610f\u67e5\u6536", "badge": 1, "relative_id": 6, "type": 3}
    private void wsTracks() {
        String url = new StringBuffer().append(mWsUrl)//7659a1ee40e4dbd8
                .append("?access_token=").append(Access_token)
                /*.append("&map=1&radio_id=").append(radioId)*/
                .toString();
        WebSocketClient client = new WebSocketClient(URI.create(url), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                Log.i("1234", "Connected!");
            }

            @Override
            public void onMessage(String message) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = message;
                handler.sendMessage(msg);
            }

            @Override
            public void onMessage(byte[] data) {

            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.i("1234", "onDisconnect!");
            }

            @Override
            public void onError(Exception error) {
                Log.i("1234", "error!" + error);

            }
        }, null);
        client.connect();
    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        double lat, lng;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("1234", "onReceive  " + action);
            if (action.equals(GlobalData.BROADCAST_COUNTER_ACTION)) {
                // AMapLocation location=(AMapLocation)intent.getExtras();
                Bundle bundle = intent.getExtras();
                if (null != bundle) {
                    lat = bundle.getDouble("lat");
                    lng = bundle.getDouble("lng");
                    Message msg=new Message();
                    msg.what=1;
                    msg.obj=new LatLng(lat,lng);
                    handler.handleMessage(msg);
                }

            }
        }


    }

//    public void getFromUser() {
//           //http://119.15.136.126:8200/track
//        String url = new StringBuffer().append("ws://192.168.1.115:8888/track")
//                .append("?access_token=").append("4e3c16083f26d7b3")
//                .append("&radio_id=").append(new SharedPref(SystemMsgService.this).getRadioId())
//                .toString();
//        Log.i("1234", "private_messages " + url);
//        AsyncHttpClient.getDefaultInstance().websocket(url, null, new AsyncHttpClient.WebSocketConnectCallback() {
//            @Override
//            public void onCompleted(Exception e, WebSocket webSocket) {
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                webSocket.send("a string");
//                webSocket.send(new byte[10]);
//                webSocket.setStringCallback(new WebSocket.StringCallback() {
//                    @Override
//                    public void onStringAvailable(String s) {
//                        Log.i("1234", "webSocket: " + s);
//                    }
//                });
//                webSocket.setDataCallback(new DataCallback() {
//                    @Override
//                    public void onDataAvailable(DataEmitter dataEmitter, ByteBufferList byteBuffers) {
//
//                    }
//                });
//            }
//        });
//    }
}
