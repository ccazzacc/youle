package com.youle.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.youle.R;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.ReleaseActivity;
import com.youle.managerUi.SlidingMapActivity;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;

public class MapCenterFragment extends Fragment implements OnClickListener,OnMarkerClickListener, OnInfoWindowClickListener,OnMapLoadedListener{
	private Context ct;
	private FinalBitmap fb;
//	
	private Button btn_left, btn_quan,btn_record,btn_refresh;
	private AMap aMap;
	private static final LatLng marker1 = new LatLng(30.64626, 101.76322);
	private static final LatLng marker2 = new LatLng(30.20827, 101.16322);
	private static final LatLng marker3 = new LatLng(30.48128, 101.35322);
	private static final LatLng marker4 = new LatLng(30.17929, 101.56322);
	private static final LatLng marker5 = new LatLng(30.035292, 101.27322);
	private static final LatLng marker6 = new LatLng(30.005729, 101.70322);
	private static final LatLng marker7 = new LatLng(30.368298, 101.66322);
	private static final LatLng marker8 = new LatLng(30.149299, 101.44322);
	private static final LatLng marker9 = new LatLng(30.199995, 101.74322);
	private static final LatLng marker10 = new LatLng(30.2542996, 101.09322);
	List<LatLng> latList = new ArrayList<LatLng>();
	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		private final View mContents;
		CustomInfoWindowAdapter() {
			mContents = ((SlidingMapActivity) ct).getLayoutInflater().inflate(
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
			((ImageView) view.findViewById(R.id.badge)).setImageResource(R.drawable.ic_launcher);
			ImageView ivStaus = (ImageView) view.findViewById(R.id.custom_iv_staus);
			String title = marker.getTitle();
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				SpannableString titleText = new SpannableString(title);
				titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
						titleText.length(), 0);
				titleUi.setText(titleText);
			} else {
				titleUi.setText("");
			}
			String snippet = marker.getSnippet();
			if (snippet != null) {
				Log.i("test", "snippet:"+snippet);
				ivStaus.setVisibility(View.VISIBLE);
				fb.display(ivStaus, snippet);
			} else {
				ivStaus.setVisibility(View.GONE);
			}
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
//		Log.i("test", "Center onCreateView");
		ct = (SlidingMapActivity)getActivity();
		fb = FinalBitmap.create(ct);
		View v = inflater
				.inflate(R.layout.map_center_layout, container, false);
		init();
		btn_left = (Button) v.findViewById(R.id.me_center_tab_menu);
		btn_quan = (Button) v.findViewById(R.id.me_center_tab_quan);
		btn_record = (Button)v.findViewById(R.id.me_center_tab_record);
		btn_refresh = (Button)v.findViewById(R.id.header_right);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		Log.i("test", "Center onActivityCreated");
		btn_left.setOnClickListener(this);
		btn_quan.setOnClickListener(this);
		btn_record.setOnClickListener(this);
		btn_refresh.setBackgroundResource(R.drawable.bar_icon_refresh);
		btn_refresh.setOnClickListener(this);
	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		latList.add(marker1);
		latList.add(marker2);
		latList.add(marker3);
		latList.add(marker4);
		latList.add(marker5);
		latList.add(marker6);
		latList.add(marker7);
		latList.add(marker8);
		latList.add(marker9);
		latList.add(marker10);
		if (aMap == null) {
			aMap = ((SupportMapFragment) ((SlidingMapActivity) ct).getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (OtherUtil.checkReady((SlidingMapActivity) ct, aMap)) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		// 隐藏缩放按钮
		aMap.getUiSettings().setZoomControlsEnabled(false);
		drawMarkers();//
		// 设置自定义InfoWindow样式
		aMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		// 为marker事件设置监听器
		aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setOnMapLoadedListener(this);

	}

	/**
	 * 绘制系统默认的10种marker背景图片
	 */
	public void drawMarkers() {
		aMap.addMarker(new MarkerOptions()
				.position(marker1)
				.title("Marker1 aa")
				.snippet("http://api.pathtrip.com/covers/10004891367198444.jpg")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_status_danger))); // 设置此marker可以拖拽
																			// add
		// 2.0.1
		aMap.addMarker(new MarkerOptions()
				.position(marker2)
				.title("Marker2 ")
				.snippet("http://api.pathtrip.com/covers/10001641359541616.jpg")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_event)));
		aMap.addMarker(new MarkerOptions()
				.position(marker3)
				.title("Marker3 ")
				.snippet("http://api.pathtrip.com/covers/10003421360983697.jpg")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_road)));
		aMap.addMarker(new MarkerOptions()
				.position(marker4)
				.title("Marker4 ")
				.snippet("http://api.pathtrip.com/covers/10004891367198444.jpg")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_trafficjam)));
		aMap.addMarker(new MarkerOptions()
				.position(marker5)
				.title("Marker5 ")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_unimpeded)));
		aMap.addMarker(new MarkerOptions()
				.position(marker6)
				.title("Marker6 ")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_danger)));
		aMap.addMarker(new MarkerOptions()
				.position(marker7)
				.title("Marker7 ")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_road)));
		aMap.addMarker(new MarkerOptions()
				.position(marker8)
				.title("Marker8 ")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_trafficjam)));
		aMap.addMarker(new MarkerOptions()
				.position(marker9)
				.title("Marker9 ")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_unimpeded)));
		aMap.addMarker(new MarkerOptions()
				.position(marker10)
				.title("Marker10 ")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pin_status_danger)));
	}

	/**
	 * 清空地图上所有已经标注的marker
	 */
	public void onClearMap(View view) {
		if (OtherUtil.checkReady((SlidingMapActivity) ct, aMap)) {
			aMap.clear();
		}
	}

	/**
	 * 重新标注所有的marker
	 */
	public void onResetMap(View view) {
		if (OtherUtil.checkReady((SlidingMapActivity) ct, aMap)) {
			aMap.clear();
			drawMarkers();//
		}
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		return false;

	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		ToastUtil.show((SlidingMapActivity) ct, "你点击了Info  Window");
	}


	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在View中
		LatLngBounds.Builder bounds = new LatLngBounds.Builder();
		for(int i = 0;i< latList.size();i++)
		{
			bounds.include(latList.get(i));
		}
		LatLngBounds aaaa = bounds.build();
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(aaaa, 20));
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.me_center_tab_menu:
			((SlidingMapActivity) ct).showLeft();
			break;

		case R.id.me_center_tab_quan:
			startActivity(new Intent((SlidingMapActivity) ct,
					CouponActivity.class));
			((SlidingMapActivity) ct).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.me_center_tab_record:
			startActivity(new Intent((SlidingMapActivity) ct,
					ReleaseActivity.class));
			break;
		case R.id.header_right:
			break;
		}
	}


}
