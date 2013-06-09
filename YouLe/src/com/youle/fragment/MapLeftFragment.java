package com.youle.fragment;



import com.youle.R;
import com.youle.managerUi.CarMainActivity;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.MainActivity;
import com.youle.managerUi.MapActivity;
import com.youle.managerUi.MeActivity;
import com.youle.managerUi.ShopMainActivity;
import com.youle.managerUi.SysMsgActivity;
import com.youle.managerUi.SysSetActivity;
import com.youle.util.GlobalData;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MapLeftFragment extends Fragment implements OnClickListener{
	
	private RelativeLayout rLayout, rHome, rMe, rCoupon, rMsg, rShop, rSet,rCar;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_left_view, null);
		initView(view);
		return view;
	}

	private void initView(View v){
		rLayout = (RelativeLayout)v.findViewById(R.id.map_leftLayout);
		LayoutParams pa = new RelativeLayout.LayoutParams((int)(GlobalData.screenWidth - GlobalData.screenDensity*50), LayoutParams.FILL_PARENT);
		rLayout.setLayoutParams(pa);
		rHome = (RelativeLayout) v.findViewById(R.id.map_left_home);
		rMe = (RelativeLayout) v.findViewById(R.id.map_left_Me);
		rCoupon = (RelativeLayout) v.findViewById(R.id.map_left_coupon);
		rMsg = (RelativeLayout) v.findViewById(R.id.map_left_message);
		rShop = (RelativeLayout) v.findViewById(R.id.map_left_shop);
		rSet = (RelativeLayout) v.findViewById(R.id.map_left_setting);
		rCar = (RelativeLayout)v.findViewById(R.id.map_left_car);
		rHome.setOnClickListener(this);
		rMe.setOnClickListener(this);
		rCoupon.setOnClickListener(this);
		rMsg.setOnClickListener(this);
		rShop.setOnClickListener(this);
		rSet.setOnClickListener(this);
		rCar.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.map_left_coupon:
			startActivity(new Intent((MapActivity) getActivity(),
					CouponActivity.class));
			((MapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.map_left_home:
			startActivity(new Intent((MapActivity) getActivity(),
					MainActivity.class));
			((MapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			((MapActivity) getActivity()).finish();
			break;
		case R.id.map_left_Me:
			startActivity(new Intent((MapActivity) getActivity(),
					MeActivity.class));
			((MapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			((MapActivity) getActivity()).finish();
			break;
		case R.id.map_left_message:
			startActivity(new Intent((MapActivity) getActivity(),
					SysMsgActivity.class));
			((MapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.map_left_shop:
			startActivity(new Intent((MapActivity) getActivity(),
					ShopMainActivity.class));
			((MapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.map_left_setting:
			startActivity(new Intent((MapActivity)getActivity(),
					SysSetActivity.class));
			((MapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.map_left_car:
			startActivity(new Intent((MapActivity)getActivity(),
					CarMainActivity.class));
			((MapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
	}
	

}
