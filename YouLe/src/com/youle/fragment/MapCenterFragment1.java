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
import com.amap.api.maps.*;
import com.amap.api.maps.AMap.*;
import com.amap.api.maps.model.*;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.info.MainInfo;
import com.youle.managerUi.MapActivity;
import com.youle.managerUi.SlidingActivity;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

public class MapCenterFragment1 extends Fragment implements OnClickListener,
		OnMarkerClickListener, OnInfoWindowClickListener, OnMapLoadedListener,
		LocationSource, AMapLocationListener {
	private Context ct;
	private AMap aMap;
	private List<MainInfo> list;
	private List<LatLng> latList = new ArrayList<LatLng>();
	private LocationManagerProxy mAMapLocManager = null;
	private OnLocationChangedListener mListener;
	private Location loc;
	private FinalBitmap fb;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_center_view, null);
		initView(view);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ct = (MapActivity) getActivity();
		fb = FinalBitmap.create(ct);
		fb.onResume();
	}

	/**
	 * 初始化AMap对象
	 */
	private void initView(View view) {
		Button btn = (Button) view.findViewById(R.id.map_cBtn_list);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MapActivity) getActivity()).showMenu();
			}
		});
		new GetInfoTask().execute();

	}

    private AMap.OnMapLongClickListener longClick=new AMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {

        }
    };
	
	
	private class GetInfoTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getInfoList(ct, 0, 0, 0, 10);
			if(res.startsWith(GlobalData.RESULT_OK))
			{
				list = YouLe.jsonInfo(res);
				if(null != list && list.size()>0)
					for(int i=0,j=list.size();i<j;i++)
					{
						latList.add(list.get(i).getLatLng());
					}
				return GlobalData.RESULT_OK;
			}else
				return res;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!OtherUtil.isNullOrEmpty(result) && result.startsWith(GlobalData.RESULT_OK))
			{
				if (aMap == null) {
					aMap = ((SupportMapFragment) ((MapActivity) ct)
							.getSupportFragmentManager().findFragmentById(R.id.map))
							.getMap();
					if (checkReady(aMap)) {
						 setUpMap();
					}
				}
			}else
			{
				ToastUtil.showToast(ct, result);
			}
		}
	}
	
	private void setUpMap() {
		mAMapLocManager = LocationManagerProxy.getInstance((MapActivity) ct);
		aMap.setLocationSource(this);
		aMap.setMyLocationEnabled(true);
		UiSettings uiSet = aMap.getUiSettings();
		uiSet.setMyLocationButtonEnabled(true);
		uiSet.setCompassEnabled(true);
		uiSet.setScaleControlsEnabled(true);
		drawMarkers();
		aMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		aMap.setOnMarkerClickListener(this);
        aMap.setOnMapLongClickListener(longClick);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setOnMapLoadedListener(this);
		if (null != loc) {
			CameraPosition camera = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(), loc.getLongitude()))
					.zoom(16).bearing(45).tilt(0).build();
			changeCamera(CameraUpdateFactory.newCameraPosition(camera));
		}
	}

	private void changeCamera(CameraUpdate update) {
		changeCamera(update, null);
	}

	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		aMap.animateCamera(update, 1000, callback);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		deactivate();
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

//	/**
//	 * 清空地图上所有已经标注的marker
//	 */
//	public void onClearMap(View view) {
//		if (OtherUtil.checkReady((MapActivity) ct, aMap)) {
//			aMap.clear();
//		}
//	}
//
//	/**
//	 * 重新标注所有的marker
//	 */
//	public void onResetMap(View view) {
//		if (OtherUtil.checkReady((MapActivity) ct, aMap)) {
//			aMap.clear();
//			drawMarkers();//
//		}
//	}
//
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	private int i = 0;

	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if (null != location)
			loc = location;
		if (mListener != null) {
			mListener.onLocationChanged(location);
		}
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		Message msg = new Message();
		msg.obj = latLng;
		if (handler != null) {
			i++;
			handler.sendMessage(msg);
		}
	}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (i == 1)
				aMap.moveCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition.Builder()
								.target((LatLng) msg.obj).zoom(16).bearing(0)
								.tilt(0).build()));
		}
	};
	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
		if (mAMapLocManager == null) {
			mAMapLocManager = LocationManagerProxy
					.getInstance((SlidingActivity) ct);
		}
		// Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
		mAMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 10, this);
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		// 设置所有maker显示在View中
		LatLngBounds.Builder bounds = new LatLngBounds.Builder();
		for (int i = 0; i < list.size(); i++) {
			bounds.include(list.get(i).getLatLng());
		}
		LatLngBounds aaaa = bounds.build();
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(aaaa, 20));
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		ToastUtil.show((MapActivity) ct, "你点击了Info  Window");
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		private final View mContents;

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
		private int i;
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
			Log.i("test", "content:"+content);
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
}
