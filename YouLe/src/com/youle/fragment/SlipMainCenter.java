package com.youle.fragment;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.managerData.MyApplication;
import com.youle.managerData.info.MainInfo;
import com.youle.managerUi.*;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

public class SlipMainCenter extends Fragment {
    protected static LocalActivityManager mLocalActivityManager;
    private static FrameLayout mBoday;
    private static Context ct;
    public static Button btnRight,btnLeft,btnMsgtop;
    public static TextView tvName,tvSubName;
    public static LinearLayout lyButtom;
    private int flag = 0;
    private static String mainStr;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ct = (SlidActivity) getActivity();
    }
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slid_main, null);
        btnLeft =(Button)view.findViewById(R.id.header_left);
        btnLeft.setVisibility(View.VISIBLE);
        tvName = (TextView)view.findViewById(R.id.header_tv);
        tvSubName = (TextView)view.findViewById(R.id.header_tv_bottom);
        lyButtom = (LinearLayout)view.findViewById(R.id.header_ly_bottom);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SlidActivity.showMenu();
            }
        });
        btnRight =(Button)view.findViewById(R.id.header_right);
        btnMsgtop=(Button)view.findViewById(R.id.sysmsg_top);
        mLocalActivityManager = new LocalActivityManager((SlidActivity)ct, true);
        mBoday=(FrameLayout)view.findViewById(R.id.frame);
        Bundle states = savedInstanceState != null? (Bundle) savedInstanceState.getBundle("one") : null;
        mLocalActivityManager.dispatchCreate(states);
        mainStr = ((SlidActivity) ct).getIntent().getStringExtra("mainStr");
        flag = ((SlidActivity) ct).getIntent().getIntExtra("flag", 0);
		if(flag == 0 && null != mainStr && !OtherUtil.isNullOrEmpty(mainStr))
			slidIntent(flag,true);
		else
			slidIntent(0,false);
        return view;
    }
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		flag = ((SlidActivity) ct).getIntent().getIntExtra("flag", 0);
		if(flag == 7)
			slidIntent(flag,true);
		if(flag != 0)
			slidIntent(flag,false);
	}

	public static void slidIntent(int i,boolean isData)
	{
		Intent intent = null;
		switch (i) {
		case 0:
			intent = new Intent(ct, RoadMapActivity.class);
			if(isData)
				intent.putExtra("mainStr", mainStr);
			break;
		case 1:
			intent = new Intent(ct, MainActivity.class);
			break;
		case 2:
			intent = new Intent(ct, MeActivity.class);
			if(null != Utility.mSession && !OtherUtil.isNullOrEmpty(Utility.mSession.getUserId()))
				intent.putExtra(GlobalData.U_ID, Utility.mSession.getUserId());
			break;
		case 3:
			intent = new Intent(ct,
					CouponActivity.class);
			intent.putExtra("tag", "coupon_me");
			break;
		case 4:
			intent = new Intent(ct,
					CarMainActivity.class);
			break;
		case 5:
			intent = new Intent(ct,
					SysMessageActivity.class);
			break;
		case 6:
			intent = new Intent(ct,
					SysSetActivity.class);
			break;
		case 7:
			intent = new Intent(ct,
					ShopMainActivity.class);
			if(isData)
				intent.putExtra("scan_data", GlobalData.RESULT);
			break;
        case 8:
            intent = new Intent(ct,
                    SysPrivateActivity.class);
            break;
        case 9:
            intent = new Intent(ct,
                    HotActivity.class);
            break;
		}
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        View v = mLocalActivityManager.startActivity("one", intent).getDecorView();
        mBoday.removeAllViews();
        mBoday.addView(v);
	}

}