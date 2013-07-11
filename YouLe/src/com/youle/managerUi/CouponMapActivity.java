package com.youle.managerUi;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
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
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.MultipartFormDataBody;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import net.tsz.afinal.FinalBitmap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaofuchao on 13-6-20.
 */
public class CouponMapActivity extends FragmentActivity implements
        LocationSource, AMapLocationListener {
    AMap.OnMarkerClickListener markerClick = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow();
            LatLng latlng = new LatLng(marker.getPosition().latitude + 0.01, marker.getPosition().longitude);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

            return true;
        }


    };
    AMap.OnInfoWindowClickListener infoWindowClick = new AMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            /*marker.hideInfoWindow();*/
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                getData();
            }
        }
    };
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private FinalBitmap fb;
    private double lng, lat;
    private List<MerInfo> mMerList = new ArrayList<MerInfo>();
    private List<CouInfo> mCouList = new ArrayList<CouInfo>();
    private int mTemp = 1;

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_map_activity);
        fb = FinalBitmap.create(this);
        fb.onResume();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deactivate();
    }

    private void initView() {
        Button back = (Button) findViewById(R.id.header_left);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CouponMapActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
        Button tolist = (Button) findViewById(R.id.header_right);
        tolist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(CouponMapActivity.this, CouponActivity.class));
                CouponMapActivity.this.finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        TextView title = (TextView) findViewById(R.id.header_tv);
        title.setText(getString(R.string.coupon));
        back.setBackgroundResource(R.drawable.bar_button_back_normal);
        back.setVisibility(View.VISIBLE);
        tolist.setBackgroundResource(R.drawable.bar_button_list_normal);

        if (aMap == null) {
            aMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (aMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mAMapLocationManager = LocationManagerProxy
                .getInstance(this);
        LatLng latLng = new SharedPref(CouponMapActivity.this).getLatLng();
        if (latLng != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            lat = latLng.latitude;
            lng = latLng.longitude;
            getData();
        }
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setOnMarkerClickListener(markerClick);

        aMap.setOnInfoWindowClickListener(infoWindowClick);
        aMap.setInfoWindowAdapter(new MapInfoWindowAdapter());
    }

    private void getData() {
        String url = new StringBuffer().append(YouLe.BASE_URL).append("coupons")
                .append("?access_token=").append(Utility.mToken.getAccess_token())
                .append("&map=1&radio_id=")
                .append(new SharedPref(CouponMapActivity.this).getRadioId())
                .append("&distance=").append(6000)
                .append("&lng=").append(lng)
                .append("&lat=").append(lat).toString();
        Log.i("1234", "get url: " + url);


        AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Log.i("1234", jsonObject + "");
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("coupons");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
//                        MerInfo mi=new MerInfo(obj.getString(""),);
                        JSONArray array = obj.getJSONArray("location");
                        Log.i("1234", "location: " + array.get(0) + " lat " + array.get(1));
                        int category_id = obj.getInt("category_id");
                        double lat = array.getDouble(1);
                        double lng = array.getDouble(0);
                        int merchant_id = obj.getInt("merchant_id");
                        MerInfo mi = new MerInfo(category_id, lat, lng, merchant_id);
                        mMerList.add(mi);
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                addMarker();
            }
        });


    }

    private void addMarker() {
        for (int i = 0; i < mMerList.size(); i++) {
            switch (mMerList.get(i).category_id) {
                case 1:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_diet)));
                    break;
                case 16:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_shop)));
                    break;
                case 31:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_entertainment)));
                    break;
                case 46:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_beauty)));
                    break;
                case 57:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_marry)));
                    break;
                case 65:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_motion)));
                    break;
                case 80:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_hotels)));
                    break;
                case 88:
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMerList.get(i).merLat, mMerList.get(i).merLng))
                            .title(mMerList.get(i).mer_id + "")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.pin_life)));
                    break;
            }
        }


    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null) {
            mListener.onLocationChanged(aMapLocation);
            lat = aMapLocation.getLatitude();
            lng = aMapLocation.getLongitude();
            handler.sendEmptyMessage(mTemp);
            mTemp++;

        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
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

    private class MapInfoWindowAdapter implements AMap.InfoWindowAdapter {
        private final View mContents;
        private int i;
        private String cname, type, clogo_url,phone;

        MapInfoWindowAdapter() {
            mContents = getLayoutInflater().inflate(
                    R.layout.coupon_map_infowindow, null);
            mContents.setMinimumHeight(200);
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

        private void render(final Marker marker, View view) {
            final ImageView iv = (ImageView) view.findViewById(R.id.infowindow_av);
            final TextView name = (TextView) view.findViewById(R.id.infowindow_name);
            final TextView textPhone = (TextView) view.findViewById(R.id.infowindow_phone);
            final TextView textType = (TextView) view.findViewById(R.id.infowindow_type);
            final ListView list = (ListView) view.findViewById(R.id.infowindow_list);
            ImageView ivClose = (ImageView) view.findViewById(R.id.infowindow_close);

            ivClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    marker.hideInfoWindow();
                }
            });

            String url = new StringBuffer().append(YouLe.BASE_URL).append("coupons/")
                    .append(marker.getTitle() + "?radio_id=")
                    .append(new SharedPref(CouponMapActivity.this).getRadioId())
                    .append("&access_token=").append(Utility.mToken.getAccess_token())
                    .toString();
            Log.i("1234", url);
            AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
                @Override
                public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    Log.i("1234", jsonObject + "");
                    try {
                        cname = jsonObject.getString("name");
                        type = jsonObject.getString("category");
                        phone=jsonObject.getString("phone");
                        clogo_url = jsonObject.getString("logo_url");
                        Log.i("1234", cname + " " + MapInfoWindowAdapter.this.type + " " + clogo_url);
                        JSONArray array = jsonObject.getJSONArray("coupons");
                        mCouList = new ArrayList<CouInfo>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            int total = object.getInt("total");
                            String cname = object.getString("name");
                            int clevel = object.getInt("level");
                            int remains = object.getInt("remains");
                            int delivery_id = object.getInt("delivery_id");
                            int coupon_id = object.getInt("coupon_id");
                            String image_url = object.getString("image_url");
                            String summary = object.getString("summary");
                            String expire_at = object.getString("expire_at");
                            boolean get_status = object.getBoolean("get_status");

                            mCouList.add(new CouInfo(total, cname, clevel, remains, delivery_id, coupon_id, image_url, summary, expire_at, get_status));
                        }
                        Log.i("1234", "obj:: " + mCouList.size() + " " + mCouList.get(0).cname);


                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    fb.display(iv, clogo_url/*"http://www.qqw21.com/article/uploadpic/2012-9/201291710164622171.jpg"*/);
                    name.setText(cname);
                    textType.setText(type + "");
                    textPhone.setText(phone);
                    if (mCouList.size() == 0) {
                        list.setVisibility(View.GONE);
                    } else {
                        list.setAdapter(new MyAdapter());
                    }
                }
            });


        }

        public class MyAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return mCouList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(final int i, View convertView, ViewGroup viewGroup) {
                View view = null;
                final ViewHolder holder;
                if (convertView == null || view == null) {
                    view = getLayoutInflater().inflate(R.layout.coupon_map_infolist,
                            viewGroup, false);
                    holder = new ViewHolder();
                    holder.tit = (TextView) view.findViewById(R.id.info_list_txt);
                    holder.btn = (ImageView) view.findViewById(R.id.info_list_btn);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                holder.tit.setText(mCouList.get(i).cname);
                if (mCouList.get(i).get_status) {
                    holder.btn.setBackgroundResource(R.drawable.rob_button);
                } else {
                    holder.btn.setBackgroundResource(R.drawable.rob_reopened);
                }
                holder.btn.setTag(i + "");
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //抢优惠券
                        String s = view.getTag() + "";
                        Log.i("1234", "click: ss  " + s);
                        view.setBackgroundResource(R.drawable.rob_reopened);
                        getCoupons(Integer.parseInt(s));
                    }


                });
                return view;
            }

            private void getCoupons(int tag) {
                String url = new StringBuffer().append(YouLe.BASE_URL).append("coupons").toString();
                AsyncHttpPost post = new AsyncHttpPost(url);
                MultipartFormDataBody body = new MultipartFormDataBody();
                body.addStringPart("access_token", Utility.mToken.getAccess_token());
                body.addStringPart("delivery_id", mCouList.get(tag).delivery_id + "");
                post.setBody(body);
                AsyncHttpClient.getDefaultInstance().execute(post, new AsyncHttpClient.JSONObjectCallback() {
                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        Log.i("1234", "JSONObjectCallback: " + jsonObject);

                    }
                });

            }

            private class ViewHolder {
                public TextView tit;
                public ImageView btn;
            }
        }
    }

    private class MerInfo {
        public int category_id;
        public double merLat;
        public double merLng;
        public int mer_id;

        public MerInfo(int category_id, double lat, double lng, int mer_id) {
            this.category_id = category_id;
            this.merLat = lat;
            this.merLng = lng;
            this.mer_id = mer_id;

        }
    }

    private class CouInfo {
        public int total;
        public String cname;
        public int clevel;
        public int remains;
        public int delivery_id;
        public int coupon_id;
        public String image_url;
        public String summary;
        public String expire_at;
        public boolean get_status;

        public CouInfo(int total, String name, int level, int remains, int delivery_id, int coupon_id,
                       String image_url, String summary, String expire_at, boolean get_status) {
            this.total = total;
            this.cname = name;
            this.clevel = level;
            this.remains = remains;
            this.delivery_id = delivery_id;
            this.coupon_id = coupon_id;
            this.image_url = image_url;
            this.summary = summary;
            this.expire_at = expire_at;
            this.get_status = get_status;
        }

    }
}