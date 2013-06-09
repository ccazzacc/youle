package com.youle.fragment;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.info.MainInfo;
import com.youle.managerUi.MapActivity;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

public class MapCenterFragment extends Fragment implements
        LocationSource, AMapLocationListener {
    AMap.OnMapLongClickListener longClick = new AMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            Log.i("1234", "longClick: " + latLng);
            lat=latLng.latitude;
            lng=latLng.longitude;
            new GetInfoTask().execute();
        }
    };
    AMap.OnMarkerClickListener markClick = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AMapLocation location = (AMapLocation) msg.obj;

            sharedPref.saveLatLngCity(location.getLatitude(), location.getLongitude(), location.getProvince() + location.getCity());
        }
    };
    private Context ct;
    private AMap aMap;
    private LocationManagerProxy mAMapLocationManager;
    private OnLocationChangedListener mListener;
    private List<MainInfo> list;
    private List<LatLng> latList = new ArrayList<LatLng>();
    private FinalBitmap fb;
    private View view;
    private SharedPref sharedPref;
    private Double lat=0.0,lng=0.0;
    private int mDistance;
    private Marker marker;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_center_view, null);
        initView(view);
        return view;

    }

    private void initView(View view) {
        Button btn = (Button) view.findViewById(R.id.map_cBtn_list);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((MapActivity) getActivity()).showMenu();
            }
        });
        if (aMap == null) {
            aMap = ((SupportMapFragment) ((MapActivity) ct)
                    .getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (checkReady(aMap)) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mAMapLocationManager = LocationManagerProxy
                .getInstance(ct);
        if(sharedPref.getLatLng()!=null){
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sharedPref.getLatLng(), 14));
        }
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        new GetInfoTask().execute();
        aMap.setOnMapLongClickListener(longClick);
        aMap.setOnMarkerClickListener(markClick);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ct = (MapActivity) getActivity();
        sharedPref = new SharedPref(ct);
        fb = FinalBitmap.create(ct);
        fb.onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        deactivate();
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
        Log.i("1234", "provider: " + s);

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(ct);
        }
        // 网络定位
        mAMapLocationManager.requestLocationUpdates(
                LocationProviderProxy.AMapNetwork, 10, 5000, this);
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

    /**
     * AMap对象判断是否为null
     */
    public boolean checkReady(AMap aMap) {
        if (aMap == null) {
            ToastUtil.show(ct, "map_not_ready");
            return false;
        }
        return true;
    }

    public void drawMarkers() {
        if (null != list && list.size() > 0)
            for (int i = 0, j = list.size(); i < j; i++) {
                switch (list.get(i).getMark()) {
                    case 0:
                        aMap.addMarker(new MarkerOptions()
                                .position(list.get(i).getLatLng())
                                .title("mark1")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.pin_status_trafficjam)));
                        break;
                    case 105:
                        aMap.addMarker(new MarkerOptions()
                                .position(list.get(i).getLatLng())
                                .title("mark1")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.pin_status_road)));
                        break;
                    case 3:
                        aMap.addMarker(new MarkerOptions()
                                .position(list.get(i).getLatLng())
                                .title("mark1")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.pin_status_danger)));
                        break;
                    case 4:
                        aMap.addMarker(new MarkerOptions()
                                .position(list.get(i).getLatLng())
                                .title("mark1")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.pin_status_event)));
                        break;
                    case 5:
                        aMap.addMarker(new MarkerOptions()
                                .position(list.get(i).getLatLng())
                                .title("mark1")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.pin_status_unimpeded)));
                        break;
                }
            }

    }

    private class GetInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if(lat>0){
                mDistance=1000;
            }else{
                mDistance=0;
            }
            String res = YouLe.getInfoList(ct, mDistance, lng, lat, 100);
            if (res.startsWith(GlobalData.RESULT_OK)) {
                list = YouLe.jsonInfo(res);
                if (null != list && list.size() > 0)
                    for (int i = 0, j = list.size(); i < j; i++) {
                        latList.add(list.get(i).getLatLng());
                    }
                return GlobalData.RESULT_OK;
            } else
                return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            aMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            aMap.clear();
            drawMarkers();
            aMap.setMyLocationEnabled(true);
        }
    }

    class CustomInfoWindowAdapter implements AMap.InfoWindowAdapter {
        private final View mContents;
        private int i;

        CustomInfoWindowAdapter() {
            mContents = ((MapActivity) ct).getLayoutInflater().inflate(
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

        private void render(Marker marker, View view) {
            i = latList.indexOf(marker.getPosition());
            ImageView ava = (ImageView) view.findViewById(R.id.map_ava);
//			fb.display(ava, list.get(i).getAvaUrl());
            // imageLoader.displayImage(list.get(i).getAvaUrl(), ava);
            TextView cUi = ((TextView) view.findViewById(R.id.map_content));
            ImageView ivPho = (ImageView) view.findViewById(R.id.map_photo);
            TextView sTime = ((TextView) view.findViewById(R.id.map_sTime));
            ImageView ivSound = (ImageView) view.findViewById(R.id.map_sound);
            String sound = list.get(i).getAudUrl();
            if (!OtherUtil.isNullOrEmpty(sound)) {
                ivPho.setVisibility(View.GONE);
                cUi.setVisibility(View.GONE);
                ivSound.setBackgroundResource(R.drawable.sound0);
                ivSound.setVisibility(View.VISIBLE);
                sTime.setVisibility(View.VISIBLE);
                sTime.setText("4s");
                return;
            } else {
                sTime.setVisibility(View.GONE);
                ivSound.setVisibility(View.GONE);
            }
            String content = list.get(i).getText();
            Log.i("test", "content:" + content);
            if (!OtherUtil.isNullOrEmpty(content)) {
                cUi.setVisibility(View.VISIBLE);
                ivPho.setVisibility(View.GONE);
                SpannableString titleText = new SpannableString(content);
                titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
                        titleText.length(), 0);
                cUi.setText(titleText);
                return;
            } else {
                cUi.setVisibility(View.GONE);
            }
            String photo = list.get(i).getImgUrl();
            if (!OtherUtil.isNullOrEmpty(photo)) {
                ivPho.setVisibility(View.VISIBLE);
                cUi.setVisibility(View.GONE);
                fb.display(ivPho, photo);
                // imageLoader.displayImage(photo, ivPho);
                return;
            } else {
                ivPho.setVisibility(View.GONE);
            }
        }
    }
}