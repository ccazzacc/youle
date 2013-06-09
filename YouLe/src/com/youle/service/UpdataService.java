package com.youle.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.util.ToastUtil;

public class UpdataService extends Service {

    private double lat;
    private double lng;
    private float spd;
    private String place;
    private int mark;
    private String aud;
    private int aud_t;
    private String img;
    private String txt;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        spd = intent.getFloatExtra("spd", 0);
        place = intent.getStringExtra("place");
        mark = intent.getIntExtra("mark", 0);
        aud = intent.getStringExtra("aud");
        aud_t = intent.getIntExtra("aud_t", 0);
        img = intent.getStringExtra("img");
        txt = intent.getStringExtra("txt");
        Log.i("1234", "lat " + lat + " lng " + lng + " place " + place + " img " + img);
        new SubmitTask().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private class SubmitTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (Utility.hasToken() && Utility.isSessionValid()) {
                YouLe.upTrack(UpdataService.this, lng, lat, place, spd, mark, aud, aud_t, img, txt);
            }else{
                YouLe.refreshToken(UpdataService.this,Utility.mToken.getRefresh_token());
                YouLe.upTrack(UpdataService.this, lng, lat, place, spd, mark, aud, aud_t, img, txt);
            }
            Log.i("1234",lng+ lat+place+ spd+mark+ aud+aud_t+img+ txt);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ToastUtil.show(UpdataService.this,"上传成功");
            UpdataService.this.stopSelf();
        }
    }


}
