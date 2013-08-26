package com.youle.managerUi;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.*;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.*;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.*;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.AsyncLoader;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.info.MainInfo;
import com.youle.service.SystemMsgService;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import net.tsz.afinal.FinalBitmap;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaofuchao on 13-7-8.
 */
public class RoadMapActivity extends FragmentActivity implements
        LocationSource, AMapLocationListener {

    AMap.OnMapLongClickListener longClick = new AMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            Log.i("1234", "longClick: " + latLng);
            lat = latLng.latitude;
            lng = latLng.longitude;
            new GetInfoTask().execute();
        }
    };
    AMap.OnMarkerClickListener markClick = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
//            marker.showInfoWindow();
//            LatLng latlng = new LatLng(marker.getPosition().latitude + 0.0001, marker.getPosition().longitude);
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,aMap.getCameraPosition().zoom));

            return false;
        }
    };
    private AMap aMap;
    private LocationManagerProxy mAMapLocationManager;
    private LocationSource.OnLocationChangedListener mListener;
    private List<MainInfo> list;
//    private List<LatLng> latList = new ArrayList<LatLng>();
    private FinalBitmap fb;
    private SharedPref sharedPref;
    private Double lat = 0.0, lng = 0.0;
    private String mPhoto;
    private Marker mMarker;
    private CustomInfoWindowAdapter infoWindowAdapter;
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.header_right:
                    SlipMainCenter.slidIntent(1,false);
                    break;
                case R.id.map_button_record:
                    GlobalData.isIn_Vehicle = false;
                    startActivity(new Intent(RoadMapActivity.this, ReleaseActivity.class));
                    overridePendingTransition(
                            R.anim.push_left_in, R.anim.push_left_out);
//                    finish();

                    break;
                case R.id.map_button_near:
                    startActivity(new Intent(RoadMapActivity.this, CouponMapActivity.class));
                    break;
                case R.id.map_button_car:
                    GlobalData.isIn_Vehicle = true;
                    startActivity(new Intent(RoadMapActivity.this, ReleaseActivity.class));
                    overridePendingTransition(
                            R.anim.push_left_in, R.anim.push_left_out);
//                    finish();
                    break;
                case R.id.custom_img:
                    Intent intent = new Intent(RoadMapActivity.this, ShowImage.class);
                    intent.putExtra("showUrl", mPhoto);
                    startActivity(intent);
                    break;
                case R.id.header_ly_bottom:
                    startActivity(new Intent(RoadMapActivity.this, ChooseCity.class));
                    break;
            }
        }
    };
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            CustomProgressDialog.stopProgressDialog();
            AMapLocation location = (AMapLocation) msg.obj;
            assert location != null;
            sharedPref.saveLatLngCity(location.getLatitude(), location.getLongitude(), location.getProvince() + location.getCity());
            lat = location.getLatitude();
            lng = location.getLongitude();
            if (mMarker != null) {
                mMarker.remove();
            }
            mMarker = aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title("aaa")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.location_a)));

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_center_view);
        sharedPref = new SharedPref(this);
        fb = FinalBitmap.create(this);
        fb.onResume();
        infoWindowAdapter = new CustomInfoWindowAdapter();
        initView();
        MyApplication.getInstance().addActivity(this);
        startService(new Intent(RoadMapActivity.this, SystemMsgService.class));
    }

    @Override
    protected void onResume() {


    }

    private void initView() {
        if (aMap == null) {
            aMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (aMap != null) {
                setUpMap();
            }
        }
//        Button btnHeadLeft = (Button) findViewById(R.id.header_left);
//        btnHeadLeft.setVisibility(View.VISIBLE);
//        btnHeadLeft.setBackgroundResource(R.drawable.bar_button_navigation_normal);
//        btnHeadLeft.setOnClickListener(click);
//        Button btnHeadR = (Button) findViewById(R.id.header_right);
//        btnHeadR.setBackgroundResource(R.drawable.bar_button_list_normal);
//        btnHeadR.setOnClickListener(click);
//        TextView txtTit = (TextView) findViewById(R.id.header_tv);
//        txtTit.setText(new SharedPref(this).getRadioName());
//        TextView txtCity = (TextView) findViewById(R.id.header_tv_bottom);
//        txtCity.setText(new SharedPref(this).getCity());
//        ((ImageView) findViewById(R.id.header_iv_bottom))
//                .setVisibility(View.VISIBLE);
//        LinearLayout header_all = (LinearLayout) findViewById(R.id.header_all);
//        header_all.setOnClickListener(click);
        SharedPref sharedPref = new SharedPref(this);
		SlipMainCenter.lyButtom.setVisibility(View.VISIBLE);
		if (!OtherUtil.isNullOrEmpty(sharedPref.getCity()))
			SlipMainCenter.tvSubName.setText(sharedPref.getCity());
		if(!OtherUtil.isNullOrEmpty(sharedPref.getRadioName()))
			SlipMainCenter.tvName.setText(sharedPref.getRadioName());
		SlipMainCenter.lyButtom.setOnClickListener(click);
		SlipMainCenter.btnRight.setVisibility(View.VISIBLE);
		SlipMainCenter.btnRight.setBackgroundResource(R.drawable.bar_button_list_normal);
		SlipMainCenter.btnRight.setOnClickListener(click);
		SlipMainCenter.btnMsgtop.setVisibility(View.GONE);

        Button btnRecord = (Button) findViewById(R.id.map_button_record);
        btnRecord.setOnClickListener(click);
        Button btnNear = (Button) findViewById(R.id.map_button_near);
        btnNear.setOnClickListener(click);
        Button btnCar = (Button) findViewById(R.id.map_button_car);
        btnCar.setOnClickListener(click);
    }

    private void setUpMap() {
        mAMapLocationManager = LocationManagerProxy
                .getInstance(this);
        if (sharedPref.getLatLng() != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sharedPref.getLatLng(), 14));
            lat = sharedPref.getLatLng().latitude;
            lng = sharedPref.getLatLng().longitude;
            new GetInfoTask().execute();
        }

        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMapLongClickListener(longClick);
        aMap.setOnMarkerClickListener(markClick);

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null) {
            mListener.onLocationChanged(aMapLocation);
            Message msg = new Message();
            msg.obj = aMapLocation;
            handler.sendMessage(msg);
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 14));
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(RoadMapActivity.this);
        }
        // 网络定位
        mAMapLocationManager.requestLocationUpdates(
                LocationProviderProxy.AMapNetwork, 10, 10000, this);
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    private void drawMarkers() {
        int[] marker_icon={R.drawable.pin_crowd,R.drawable.pin_accident,R.drawable.pin_danger,R.drawable.pin_road,
                R.drawable.pin_often,R.drawable.pin_webcam};

        if (null != list && list.size() > 0)
            for (int i = 0, j = list.size(); i < j; i++) {
                aMap.addMarker(new MarkerOptions()
                        .position(list.get(i).getLatLng())
                        .title(i + "")
                        .icon(BitmapDescriptorFactory
                                .fromResource(marker_icon[list.get(i).getMark()-1])));

//                switch (list.get(i).getMark()) {
//                    case 1:
//                        aMap.addMarker(new MarkerOptions()
//                                .position(list.get(i).getLatLng())
//                                .title(i + "")
//                                .icon(BitmapDescriptorFactory
//                                        .fromResource(R.drawable.pin_crowd)));
//                        break;
//                    case 2:
//                        aMap.addMarker(new MarkerOptions()
//                                .position(list.get(i).getLatLng())
//                                .title(i + "")
//                                .icon(BitmapDescriptorFactory
//                                        .fromResource(R.drawable.pin_accident)));
//                        break;
//                    case 3:
//                        aMap.addMarker(new MarkerOptions()
//                                .position(list.get(i).getLatLng())
//                                .title(i + "")
//                                .icon(BitmapDescriptorFactory
//                                        .fromResource(R.drawable.pin_danger)));
//                        break;
//                    case 4:
//                        aMap.addMarker(new MarkerOptions()
//                                .position(list.get(i).getLatLng())
//                                .title(i + "")
//                                .icon(BitmapDescriptorFactory
//                                        .fromResource(R.drawable.pin_road)));
//                        break;
//                    case 5:
//                        aMap.addMarker(new MarkerOptions()
//                                .position(list.get(i).getLatLng())
//                                .title(i + "")
//                                .icon(BitmapDescriptorFactory
//                                        .fromResource(R.drawable.pin_often)));
//                        break;
//                    case 6:
//                        aMap.addMarker(new MarkerOptions()
//                                .position(list.get(i).getLatLng())
//                                .title(i + "")
//                                .icon(BitmapDescriptorFactory
//                                        .fromResource(R.drawable.pin_webcam)));
//                        break;
//                }
            }
        if(RoadMapActivity.this.getIntent().getExtras()==null){
            return;
        }
        String info=RoadMapActivity.this.getIntent().getStringExtra("mainStr");
        Log.i("1234","info: "+info);
        MainInfo mainInfo=null;
        if(info!=null){
            try {
                JSONObject obj=new JSONObject(info);
                String latLng = obj.getString(GlobalData.LOCATION);
                mainInfo=new MainInfo(
                        obj.getString(GlobalData.TRACK_ID),
                        obj.getString(GlobalData.USER_ID),
                        obj.getString(GlobalData.USER_NAME),
                        obj.getString(GlobalData.AVATAR_URL),
                        obj.getString(GlobalData.CREATED),
                        obj.getString(GlobalData.TEXT), obj
                        .getInt(GlobalData.MARK), obj
                        .getString(GlobalData.AUDIO_URL), obj
                        .getInt(GlobalData.AUDIO_TIME), obj
                        .getString(GlobalData.IMAGE_URL), obj
                        .getString(GlobalData.ORIGIN_IMAGE_URL),
                        new LatLng(Double.parseDouble(latLng.substring(
                                latLng.indexOf(",") + 1,
                                latLng.lastIndexOf("]"))), Double
                                .parseDouble(latLng.substring(1,
                                        latLng.indexOf(",")))), obj
                        .getString(GlobalData.PLACE), obj
                        .getInt(GlobalData.FLAGS), obj
                        .getInt(GlobalData.WIDTH), obj
                        .getInt(GlobalData.HEIGHT), obj
                        .getInt(GlobalData.USER_TYPE));
                list.add(mainInfo);
                Log.i("1234","mainInfo: "+latLng+list.get(1).getLatLng());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Marker marker=aMap.addMarker(new MarkerOptions()
                    .position(mainInfo.getLatLng())
                    .title(list.size()-1 + "")
                    .icon(BitmapDescriptorFactory
                            .fromResource(marker_icon[mainInfo.getMark()-1])));
            jumpPoint(marker,mainInfo.getLatLng());
        }
    }

    public void jumpPoint(final Marker marker, final LatLng latLng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        Point startPoint = proj.toScreenLocation(latLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * latLng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * latLng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
        marker.showInfoWindow();
    }

    private class GetInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            int mDistance;
            if (lat > 0) {
                mDistance = 3000;
            } else {
                mDistance = 0;
            }
            String res = YouLe.getInfoList(RoadMapActivity.this, mDistance, lng, lat, 1, 50);
            if (res.startsWith(GlobalData.RESULT_OK)) {
//                latList = new ArrayList<LatLng>();
                list = YouLe.jsonInfo(res);
//                if (null != list && list.size() > 0)
//                    for (int i = 0, j = list.size(); i < j; i++) {
//                        latList.add(list.get(i).getLatLng());
//                    }
                return GlobalData.RESULT_OK;
            } else
                return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            aMap.setInfoWindowAdapter(infoWindowAdapter);
            aMap.clear();
            drawMarkers();
            aMap.setMyLocationEnabled(true);

        }
    }

    class CustomInfoWindowAdapter implements AMap.InfoWindowAdapter {
        private final View mContents;
        private int i;
        private AnimationDrawable animationDrawable;
        private MediaPlayer mediaPlayer;

        CustomInfoWindowAdapter() {
            mContents = (RoadMapActivity.this).getLayoutInflater().inflate(
                    R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            Log.i("1234", "!!finalize!!");
        }

        private void render(final Marker marker, View view) {
            int[] drP = {R.drawable.crowd_normal, R.drawable.accident_normal, R.drawable.danger_normal, R.drawable.road_normal, R.drawable.often_normal, R.drawable.webcam_normal};
            String[] eve = {getString(R.string.traffic), getString(R.string.accident), getString(R.string.danger), getString(R.string.roads), getString(R.string.police), getString(R.string.monitor)};
//            i = latList.indexOf(marker.getPosition());
            i= Integer.parseInt(marker.getTitle());
            ImageView ava = (ImageView) view.findViewById(R.id.custom_av);
            TextView uname = (TextView) view.findViewById(R.id.custom_uname);
            fb.display(ava, list.get(i).getAvaUrl());
            uname.setText(list.get(i).getUserName());
            ImageView ivPho = (ImageView) view.findViewById(R.id.custom_img);
            ivPho.setOnClickListener(click);
            ImageView ivType = (ImageView) view.findViewById(R.id.custom_user_type);
            ImageView ivClose = (ImageView) view.findViewById(R.id.custom_xx);
            Button even = (Button) view.findViewById(R.id.custom_even);
            RelativeLayout record = (RelativeLayout) view.findViewById(R.id.custom_layout_sound);
            TextView txt = (TextView) view.findViewById(R.id.custom_txt);
            TextView evenTxt = (TextView) view.findViewById(R.id.custom_even_txt);
            TextView time = (TextView) view.findViewById(R.id.custom_time);
            evenTxt.setText(eve[list.get(i).getMark() - 1]);
            even.setBackgroundResource(drP[list.get(i).getMark() - 1]);
            time.setText(list.get(i).getCreated());
            final String sound = list.get(i).getAudUrl();
            String content = list.get(i).getText();
            mPhoto = list.get(i).getImgUrl();
            switch (list.get(i).getUtype()) {
                case 2:
                    ivType.setBackgroundResource(R.drawable.taxi);
                    break;
                case 3:
                    ivType.setBackgroundResource(R.drawable.radio);
                    break;
            }
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    marker.hideInfoWindow();
                }
            });
            Log.i("1234", "index: "+list.get(i).getLatLng()+" txt:" + content + "img:" + mPhoto + "aud:" + sound + " avatar: " + list.get(i).getAvaUrl());
            if (!OtherUtil.isNullOrEmpty(sound)) {
                mediaPlayer = new MediaPlayer();
                ivPho.setVisibility(View.GONE);
                txt.setVisibility(View.GONE);
                record.setVisibility(View.VISIBLE);
                TextView audtime = (TextView) view.findViewById(R.id.custom_audtime);
                final ImageView iv = (ImageView) view.findViewById(R.id.custom_play);
                iv.setBackgroundResource(R.anim.sound_anim);
                animationDrawable = (AnimationDrawable) iv.getBackground();

                audtime.setText(list.get(i).getAudTime() + "s");
                record.setOnClickListener(new View.OnClickListener() {
                    boolean isplayed;

                    @Override
                    public void onClick(View view) {
                        AsyncLoader asyncLoader = new AsyncLoader();
                        String path = asyncLoader.loadMedia(sound, new AsyncLoader.ImageCallback() {
                            @Override
                            public String mediaLoaded(String mediaUrl) {
                                return mediaUrl;
                            }
                        });
                        if (!OtherUtil.isNullOrEmpty(path)) {

                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                animationDrawable.stop();
                            } else {
                                if (!isplayed) {
                                    try {
                                        mediaPlayer.setDataSource(path);
                                        mediaPlayer.prepare();
                                    } catch (IOException e) {
                                        Log.e("1234", e.getMessage());
                                    }
                                }
                                isplayed = true;
                                mediaPlayer.start();
                                animationDrawable.start();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        animationDrawable.stop();
//                                        iv.setBackgroundResource(R.drawable.sound0);
                                    }
                                });
                            }
                        }

                    }
                });
                return;
            } else if (!OtherUtil.isNullOrEmpty(content)) {
                ivPho.setVisibility(View.GONE);
                record.setVisibility(View.GONE);
                txt.setVisibility(View.VISIBLE);
                txt.setText(content);
                return;
            } else if (!OtherUtil.isNullOrEmpty(mPhoto)) {
                ivPho.setVisibility(View.VISIBLE);
                fb.display(ivPho, mPhoto);
                txt.setVisibility(View.GONE);
                record.setVisibility(View.GONE);
                return;
            } else {
                ivPho.setVisibility(View.GONE);
                record.setVisibility(View.GONE);
                txt.setVisibility(View.GONE);
            }
        }

    }
}