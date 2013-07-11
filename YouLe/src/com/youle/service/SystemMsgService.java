package com.youle.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhaofuchao on 13-7-1.
 */
public class SystemMsgService extends Service {
    private String mApplyUrl = new StringBuffer().append(YouLe.BASE_URL).append("private_messages").toString();

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getFromUser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getFromUser() {
        String url = new StringBuffer().append(YouLe.BASE_URL).append("private_messages/")
                .append("?access_token=").append(Utility.mToken.getAccess_token()).toString();
        Log.i("1234","private_messages "+url+"  me id "+Utility.mSession.getUserId());
        AsyncHttpClient.getDefaultInstance().get(url,new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Log.i("1234","diantai msg : "+ jsonObject + "\n share"+new SharedPref(SystemMsgService.this).getRadioId());
                try {
                    JSONArray jsonArray=jsonObject.getJSONArray("messages");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject obj1=jsonArray.getJSONObject(i);
                        JSONObject obj2=obj1.getJSONObject("from_user");
                        String username=obj2.getString("username");
                        String avatar_url=obj2.getString("avatar_url");
                        int user_id=obj2.getInt("user_id");

                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
