package com.youle.fragment;


import android.content.Intent;
import android.os.AsyncTask;
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
import com.youle.http_helper.YouLe;
import com.youle.managerUi.CarMainActivity;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.LoginActivity;
import com.youle.managerUi.MainActivity;
import com.youle.managerUi.MeActivity;
import com.youle.managerUi.RegisterActivity;
import com.youle.managerUi.MeSetActivity;
import com.youle.managerUi.ShopMainActivity;
import com.youle.managerUi.SlidingActivity;
import com.youle.managerUi.SysMsgActivity;
import com.youle.managerUi.SysSetActivity;
import com.youle.util.GlobalData;
import com.youle.util.ToastUtil;

public class MainLeftFragment extends Fragment implements OnClickListener{
	 RelativeLayout rHome,rMe,rCoupon,rMsg,rShop,rSet,rCar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        Log.i("test", "Left onCreateView");
        View v = inflater.inflate(R.layout.main_left_layout, container,
                false);
        rHome = (RelativeLayout)v.findViewById(R.id.left_home);
        rMe = (RelativeLayout)v.findViewById(R.id.left_Me);
        rCoupon = (RelativeLayout)v.findViewById(R.id.left_coupon);
        rMsg = (RelativeLayout)v.findViewById(R.id.left_message);
        rShop = (RelativeLayout)v.findViewById(R.id.left_shop);
        rSet = (RelativeLayout)v.findViewById(R.id.left_setting);
        rCar = (RelativeLayout)v.findViewById(R.id.left_car);
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
		rCar.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_coupon:
			Intent it = new Intent((SlidingActivity)getActivity(),CouponActivity.class);
			it.putExtra("tag", "coupon_me");
			startActivity(it);
			((SlidingActivity)getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.left_home:
			((SlidingActivity)getActivity()).showAbove();
			break;
		case R.id.left_Me:
			if (Utility.hasToken())
			{
				if(!Utility.isSessionValid())
				{
					new RefreshTask().execute();
				}else
				{
					startActivity(new Intent((SlidingActivity)getActivity(),MeActivity.class));
					((SlidingActivity)getActivity()).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
					((SlidingActivity)getActivity()).finish();
				}
				
			}else
			{
				startActivity(new Intent((SlidingActivity)getActivity(),LoginActivity.class));
				((SlidingActivity)getActivity()).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
			break;
		case R.id.left_message:
			startActivity(new Intent((SlidingActivity)getActivity(),SysMsgActivity.class));
			((SlidingActivity)getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.left_shop:
			startActivity(new Intent((SlidingActivity)getActivity(),ShopMainActivity.class));
			((SlidingActivity)getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.left_setting:
			startActivity(new Intent((SlidingActivity)getActivity(),SysSetActivity.class));
			((SlidingActivity)getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.left_car:
			startActivity(new Intent((SlidingActivity)getActivity(),CarMainActivity.class));
			((SlidingActivity)getActivity()).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
	}
	private class RefreshTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return YouLe.refreshToken((SlidingActivity)getActivity(), Utility.mToken.getRefresh_token());
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.equals(GlobalData.RESULT_OK)) {
				startActivity(new Intent((SlidingActivity)getActivity(),MeActivity.class));
				((SlidingActivity)getActivity()).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				((SlidingActivity)getActivity()).finish();
			}else
			{
				ToastUtil.showToast((SlidingActivity)getActivity(), result);
			}

		}
	}
}
