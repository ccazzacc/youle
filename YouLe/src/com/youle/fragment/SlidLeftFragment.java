package com.youle.fragment;



import net.tsz.afinal.FinalBitmap;

import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.managerData.info.MeInfo;
import com.youle.managerUi.CarMainActivity;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.SlidActivity;
import com.youle.managerUi.ShopMainActivity;
import com.youle.managerUi.SysMsgActivity;
import com.youle.managerUi.SysSetActivity;
import com.youle.util.GlobalData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class SlidLeftFragment extends Fragment implements OnClickListener{
	
	private RelativeLayout rLayout, rHome, rMe, rCoupon, rMsg, rShop, rSet,rCar;
	private ImageView ivAva;
//	private TextView tvName;
	private FinalBitmap fb;
	private MeInfo meInfo;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.slid_left_view, null);
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
		if(Utility.mSession != null)
			meInfo = Utility.mSession.getMe();
		if(meInfo != null)
		{
			if (null != meInfo && meInfo.getType() == 1) {
				rShop.setVisibility(View.VISIBLE);
				rShop.setOnClickListener(this);
			} else
				rShop.setVisibility(View.GONE);
		}
		
		rSet = (RelativeLayout) v.findViewById(R.id.map_left_setting);
		rCar = (RelativeLayout)v.findViewById(R.id.map_left_car);
		ivAva = (ImageView) v.findViewById(R.id.map_left_iv_ava);
//		tvName = (TextView) v.findViewById(R.id.map_left_tvName);
		fb = FinalBitmap.create((SlidActivity) getActivity());
		fb.onResume();
		if (null != Utility.mToken) {
			fb.display(ivAva, meInfo.getAvaUrl());
//			tvName.setText(Utility.mToken.getUserName());
		}
		rHome.setOnClickListener(this);
		rMe.setOnClickListener(this);
		rCoupon.setOnClickListener(this);
		rMsg.setOnClickListener(this);
		rSet.setOnClickListener(this);
		rCar.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.map_left_coupon:
//			Intent it = new Intent((SlidActivity) getActivity(),
//					CouponActivity.class);
//			it.putExtra("tag", "coupon_me");
//			startActivity(it);
//			((SlidActivity) getActivity()).overridePendingTransition(
//					R.anim.push_left_in, R.anim.push_left_out);
			SlipMainCenter.slidIntent(3);
			SlidActivity.showMenu();
			break;
		case R.id.map_left_home:
			SlipMainCenter.slidIntent(0);
			SlidActivity.showMenu();
			break;
		case R.id.map_left_Me:
//			Intent its = new Intent((MapActivity) getActivity(),
//					MeActivity.class);
//			its.putExtra(GlobalData.U_ID, meInfo.getUserId());
//			startActivity(its);
//			((MapActivity) getActivity()).overridePendingTransition(
//					R.anim.push_left_in, R.anim.push_left_out);
//			((MapActivity) getActivity()).finish();
			SlipMainCenter.slidIntent(2);
			SlidActivity.showMenu();
			break;
		case R.id.map_left_message:
//			startActivity(new Intent((SlidActivity) getActivity(),
//					SysMsgActivity.class));
//			((SlidActivity) getActivity()).overridePendingTransition(
//					R.anim.push_left_in, R.anim.push_left_out);
			SlipMainCenter.slidIntent(5);
			SlidActivity.showMenu();
			break;
		case R.id.map_left_shop:
//			startActivity(new Intent((SlidActivity) getActivity(),
//					ShopMainActivity.class));
//			((SlidActivity) getActivity()).overridePendingTransition(
//					R.anim.push_left_in, R.anim.push_left_out);
			SlipMainCenter.slidIntent(7);
			SlidActivity.showMenu();
			break;
		case R.id.map_left_setting:
//			startActivity(new Intent((SlidActivity)getActivity(),
//					SysSetActivity.class));
//			((SlidActivity) getActivity()).overridePendingTransition(
//					R.anim.push_left_in, R.anim.push_left_out);
			SlipMainCenter.slidIntent(6);
			SlidActivity.showMenu();
			break;
		case R.id.map_left_car:
//			startActivity(new Intent((SlidActivity)getActivity(),
//					CarMainActivity.class));
//			((SlidActivity) getActivity()).overridePendingTransition(
//					R.anim.push_left_in, R.anim.push_left_out);
			SlipMainCenter.slidIntent(4);
			SlidActivity.showMenu();
			break;
		}
	}
	

}
