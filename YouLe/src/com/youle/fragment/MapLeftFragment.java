package com.youle.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.youle.R;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.MainActivity;
import com.youle.managerUi.MeActivity;
import com.youle.managerUi.ShopMainActivity;
import com.youle.managerUi.SlidingMapActivity;
import com.youle.managerUi.SlidingMapActivity;
import com.youle.managerUi.SysMsgActivity;
import com.youle.managerUi.SysSetActivity;

public class MapLeftFragment extends Fragment implements OnClickListener {
	RelativeLayout rHome, rMe, rCoupon, rMsg, rShop, rSet;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
//		Log.i("test", "Left onCreateView");
		View v = inflater.inflate(R.layout.map_left_layout, container, false);
		rHome = (RelativeLayout) v.findViewById(R.id.me_left_home);
		rMe = (RelativeLayout) v.findViewById(R.id.me_left_Me);
		rCoupon = (RelativeLayout) v.findViewById(R.id.me_left_coupon);
		rMsg = (RelativeLayout) v.findViewById(R.id.me_left_message);
		rShop = (RelativeLayout) v.findViewById(R.id.me_left_shop);
		rSet = (RelativeLayout) v.findViewById(R.id.me_left_setting);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		Log.i("test", "Left onActivityCreated");
		rHome.setOnClickListener(this);
		rMe.setOnClickListener(this);
		rCoupon.setOnClickListener(this);
		rMsg.setOnClickListener(this);
		rShop.setOnClickListener(this);
		rSet.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.me_left_coupon:
			startActivity(new Intent((SlidingMapActivity) getActivity(),
					CouponActivity.class));
			break;
		case R.id.me_left_home:
			startActivity(new Intent((SlidingMapActivity) getActivity(),
					MainActivity.class));
			((SlidingMapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			((SlidingMapActivity) getActivity()).finish();
			break;
		case R.id.me_left_Me:
			startActivity(new Intent((SlidingMapActivity) getActivity(),
					MeActivity.class));
			((SlidingMapActivity) getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			((SlidingMapActivity) getActivity()).finish();
			break;
		case R.id.me_left_message:
			startActivity(new Intent((SlidingMapActivity) getActivity(),
					SysMsgActivity.class));
			break;
		case R.id.me_left_shop:
			startActivity(new Intent((SlidingMapActivity) getActivity(),
					ShopMainActivity.class));
			break;
		case R.id.me_left_setting:
			startActivity(new Intent((SlidingMapActivity) getActivity(),
					SysSetActivity.class));
			break;
		}
	}
}
