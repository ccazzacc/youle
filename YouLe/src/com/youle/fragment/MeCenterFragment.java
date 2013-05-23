package com.youle.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youle.R;
import com.youle.managerData.info.MainInfo;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.ReleaseActivity;
import com.youle.managerUi.SlidingMeActivity;
import com.youle.managerUi.SlidingMeActivity;
import com.youle.view.XListView;
import com.youle.view.XListView.IXListViewListener;

public class MeCenterFragment extends Fragment implements IXListViewListener,OnClickListener{
	private XListView msgLv;
	private Context ct;
	private List<MainInfo> list;
	private FinalBitmap fb;
	private View hView;
//	
	private Button btn_left, btn_quan,btn_record;
	private ImageView ivMava, ivCover;
	private Handler mHandler;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
//		Log.i("test", "Center onCreateView");
		ct = (SlidingMeActivity) getActivity();
		fb = FinalBitmap.create(ct);
		View v = inflater
				.inflate(R.layout.me_center_layout, container, false);
		btn_left = (Button) v.findViewById(R.id.me_center_tab_menu);
		msgLv = (XListView) v.findViewById(R.id.me_center_listview);
		hView = LayoutInflater.from(ct).inflate(
				R.layout.msg_item_header_listview, null);
		ivMava = (ImageView) hView.findViewById(R.id.msg_iv_Head_ava);
		ivCover = (ImageView) hView.findViewById(R.id.msg_header_ivPhoto);
		btn_quan = (Button) v.findViewById(R.id.me_center_tab_quan);
		btn_record = (Button)v.findViewById(R.id.me_center_tab_record);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		Log.i("test", "Center onActivityCreated");
		mHandler = new Handler();
		msgLv.setPullLoadEnable(true);
		btn_left.setOnClickListener(this);
		btn_quan.setOnClickListener(this);
		btn_record.setOnClickListener(this);
		list = new ArrayList<MainInfo>();
		list.add(new MainInfo(
				"http://api.pathtrip.com/avatars/128/1000101.png", "",
				"http://api.pathtrip.com/covers/10004891367198444.jpg", "一品天下",
				"18:10", 4,""));
		fb.display(ivMava, "http://api.pathtrip.com/avatars/128/1000108.png");
		ivCover.setBackgroundResource(R.drawable.photo);
		msgLv.addHeaderView(hView);
		MsgAdapter adapter = new MsgAdapter(ct, list);
		msgLv.setAdapter(adapter);
	}

	private class MsgAdapter extends BaseAdapter {
		private Context context;
		private List<MainInfo> list;

		public MsgAdapter(Context context, List<MainInfo> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewCache vc;
			if (convertView == null) {
				convertView = (LinearLayout) View.inflate(context,
						R.layout.msg_item_listview, null);
				vc = new ViewCache();
				vc.ivAva = (ImageView) convertView
						.findViewById(R.id.msg_iv_ava);
				vc.tvChat = (TextView) convertView
						.findViewById(R.id.msg_tv_chat);
				vc.ivLine = (ImageView) convertView
						.findViewById(R.id.msg_iv_line);
				vc.ivPhoto = (ImageView) convertView
						.findViewById(R.id.msg_iv_photo);
				vc.tvTime = (TextView) convertView
						.findViewById(R.id.msg_tv_time);
				vc.tvAdd = (TextView) convertView
						.findViewById(R.id.msg_tv_address);
				vc.ivStatus = (ImageView) convertView
						.findViewById(R.id.msg_iv_status);
				vc.ivPhotoTop = (ImageView) convertView
						.findViewById(R.id.msg_iv_photoTop);
				// vc.lyChat =
				// (LinearLayout)convertView.findViewById(R.id.msg_ly_chat);
				convertView.setTag(vc);
			} else {
				vc = (ViewCache) convertView.getTag();
			}
			vc.ivLine.setBackgroundResource(R.drawable.line);
			MainInfo info = list.get(position);
			fb.display(vc.ivAva, info.getAvaUrl());
			if (!TextUtils.isEmpty(info.getMsgContent())) {
				vc.tvChat.setVisibility(View.VISIBLE);
				vc.tvChat.setText(info.getMsgContent());
				vc.tvChat.setBackgroundResource(R.drawable.event_bg);
				// vc.lyChat.setBackgroundResource(R.drawable.event_bg);
				vc.ivPhoto.setVisibility(View.GONE);
				vc.ivPhotoTop.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(info.getImgUrl())) {
				vc.ivPhoto.setVisibility(View.VISIBLE);
				LayoutParams para;
				para = (LayoutParams) vc.ivPhotoTop.getLayoutParams();

				para.height = 200;
				para.width = 270;
				vc.ivPhoto.setLayoutParams(para);
				// vc.lyChat.setBackgroundResource(R.drawable.event_bg);
				// vc.ivPhoto.setBackgroundResource(R.drawable.event_bg);
				fb.display(vc.ivPhoto, info.getImgUrl());
				vc.ivPhotoTop.setVisibility(View.VISIBLE);
				vc.tvChat.setVisibility(View.GONE);
			}
			LayoutParams para;
			para = (LayoutParams) vc.ivPhotoTop.getLayoutParams();

			para.height = 200;
			para.width = 270;
			vc.ivPhotoTop.setLayoutParams(para);

			vc.tvTime.setText(info.getTime());
			switch (info.getStatus()) {
			case 0:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_trafficjam);
				break;
			case 1:
				vc.ivStatus.setBackgroundResource(R.drawable.event_status_road);
				break;
			case 2:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_danger);
				break;
			case 3:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_event);
				break;
			case 4:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_unimpeded);
				break;
			}
			vc.tvAdd.setText(info.getAddress());

			return convertView;
		}

		private class ViewCache {
			ImageView ivAva;
			TextView tvChat;
			ImageView ivLine;
			ImageView ivPhoto;
			ImageView ivPhotoTop;
			TextView tvTime;
			ImageView ivStatus;
			TextView tvAdd;
			// LinearLayout lyChat;
		}
	}

	private void geneItems(String page, String size, String number) {

	}

	private void onLoad() {
		msgLv.stopRefresh();
		msgLv.stopLoadMore();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Log.i("Other", "onRefresh run");
				// geneItems("1", "10", "2");
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Log.i("Other", "onRefresh run");
				// geneItems("1", "10", "2");
			}
		}, 2000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.me_center_tab_menu:
			((SlidingMeActivity) ct).showLeft();
			break;

		case R.id.me_center_tab_quan:
			startActivity(new Intent((SlidingMeActivity) ct,
					CouponActivity.class));
			((SlidingMeActivity) ct).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.me_center_tab_record:
			startActivity(new Intent((SlidingMeActivity) ct,
					ReleaseActivity.class));
			break;
		}
	}

}
