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
import com.youle.http_helper.Utility;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.LoginActivity;
import com.youle.managerUi.MeActivity;
import com.youle.managerUi.MessageActivity;
import com.youle.managerUi.RegisterActivity;
import com.youle.managerUi.SetActivity;
import com.youle.managerUi.ShopMainActivity;
import com.youle.managerUi.SlidingActivity;

public class MainLeftFragment extends Fragment implements OnClickListener{
	 RelativeLayout rHome,rMe,rCoupon,rMsg,rShop,rSet;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i("test", "Left onCreateView");
        View v = inflater.inflate(R.layout.main_left_layout, container,
                false);
        rHome = (RelativeLayout)v.findViewById(R.id.left_home);
        rMe = (RelativeLayout)v.findViewById(R.id.left_Me);
        rCoupon = (RelativeLayout)v.findViewById(R.id.left_coupon);
        rMsg = (RelativeLayout)v.findViewById(R.id.left_message);
        rShop = (RelativeLayout)v.findViewById(R.id.left_shop);
        rSet = (RelativeLayout)v.findViewById(R.id.left_setting);
        return v;
    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.i("test", "Left onActivityCreated");
		rHome.setOnClickListener(this);
		rMe.setOnClickListener(this);
		rCoupon.setOnClickListener(this);
		rMsg.setOnClickListener(this);
		rShop.setOnClickListener(this);
		rSet.setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i("test", "Left onCreate");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("test", "Left onDestroy");
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Log.i("test", "Left onHiddenChanged");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("test", "Left onPause");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("test", "Left onResume");
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i("test", "Left onStart");
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_coupon:
			startActivity(new Intent((SlidingActivity)getActivity(),CouponActivity.class));
			break;
		case R.id.left_home:
			((SlidingActivity)getActivity()).showAbove();
			break;
		case R.id.left_Me:
			if (Utility.hasToken())
			{
				startActivity(new Intent((SlidingActivity)getActivity(),MeActivity.class));
			}else
			{
				startActivity(new Intent((SlidingActivity)getActivity(),RegisterActivity.class));
				((SlidingActivity)getActivity()).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
			break;
		case R.id.left_message:
			startActivity(new Intent((SlidingActivity)getActivity(),MessageActivity.class));
			break;
		case R.id.left_shop:
			startActivity(new Intent((SlidingActivity)getActivity(),ShopMainActivity.class));
			break;
		case R.id.left_setting:
			startActivity(new Intent((SlidingActivity)getActivity(),SetActivity.class));
			break;
		}
	}
}
