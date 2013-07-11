package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.info.CouponListInfo;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;

public class CouponActivity extends StatActivity{
	private ListView lvCoupon;
	private List<CouponListInfo> listCou;
	private FinalBitmap fb;
	private String tag;
	private TextView tvNo;
	private GestureDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_activity);
		initView();
		fb = FinalBitmap.create(this);
		fb.onResume();
	}
	@SuppressWarnings("deprecation")
	private void initView()
	{
		lvCoupon = (ListView)findViewById(R.id.coupon_listview);
		tvNo = (TextView)findViewById(R.id.coupon_tv_no);
		listCou = new ArrayList<CouponListInfo>();
		tag = this.getIntent().getStringExtra("tag");
		LinearLayout header = (LinearLayout)findViewById(R.id.coupon_header);
		if(!OtherUtil.isNullOrEmpty(tag) && tag.equals("coupon_me"))
		{
			header.setVisibility(View.GONE);
			SlipMainCenter.tvName.setText(R.string.coupon_me);
			SlipMainCenter.lyButtom.setVisibility(View.GONE);
			SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);
			SlipMainCenter.btnMsgtop.setVisibility(View.GONE);
			new GetMeCouponTask().execute();
			lvCoupon.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Intent it = new Intent(CouponActivity.this,CouponDetailActivity.class);
					it.putExtra(GlobalData.COUPON, listCou.get(arg2));
					startActivity(it);
					overridePendingTransition(
							R.anim.push_left_in, R.anim.push_left_out);
				}
			});
			detector = new GestureDetector(new OnGestureListener() {

				public boolean onSingleTapUp(MotionEvent e) {
					// TODO Auto-generated method stub
					return false;
				}

				public void onShowPress(MotionEvent e) {
					// TODO Auto-generated method stub

				}

				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					// TODO Auto-generated method stub
					return false;
				}

				public void onLongPress(MotionEvent e) {
					// TODO Auto-generated method stub

				}

				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					// TODO Auto-generated method stub
					try {
						if (Math.abs(velocityX) > ViewConfiguration
								.get(CouponActivity.this).getScaledMinimumFlingVelocity()) {
							if (e1.getX() - e2.getX() > 200 && Math.abs(velocityX) > 10) {
								SlidActivity.showMenu();
							} else if (e2.getX() - e1.getX() > 200
									&& Math.abs(velocityX) > 10) {
								SlidActivity.showMenu();
							}
						}
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}

				public boolean onDown(MotionEvent e) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			lvCoupon.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					detector.onTouchEvent(event);
					return false;
				}
			});
			tvNo.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					detector.onTouchEvent(event);
					return true;
				}
			});
		}else
		{
			header.setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.twobtn_header_tv)).setText(R.string.coupon);
			Button btnMap = (Button)findViewById(R.id.twobtn_header_right);
			btnMap.setBackgroundResource(R.drawable.bar_button_map_normal);
			Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
			btnBack.setVisibility(View.VISIBLE);
			btnBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			btnMap.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(CouponActivity.this,CouponMapActivity.class));
					CouponActivity.this.finish();
				}
			});
			btnMap.setVisibility(View.VISIBLE);
			new GetCouponTask().execute();
		}
		
		
	}

	private class GetMeCouponTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getMeCoupon(CouponActivity.this, 1,20);
			if(res.startsWith(GlobalData.RESULT_OK))
			{
				listCou = YouLe.jsonMeCoupon(CouponActivity.this,res.substring(3));
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
				if(null == listCou || listCou.size() == 0)
					tvNo.setVisibility(View.VISIBLE);
				else
					tvNo.setVisibility(View.GONE);
				CouponAdapter adapter = new CouponAdapter(CouponActivity.this,listCou);
				lvCoupon.setAdapter(adapter);
			}else
			{
				ToastUtil.showToast(CouponActivity.this, result);
			}
				
		}
		
	}
	private class GetCouponTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			LatLng lg = new SharedPref(CouponActivity.this).getLatLng();
			String res = YouLe.getCoupon(CouponActivity.this, "0", new SharedPref(CouponActivity.this).getRadioId()+"", 8000, lg.longitude, lg.latitude);
			if(res.startsWith(GlobalData.RESULT_OK))
			{
				listCou = YouLe.jsonCouponList(res.substring(3));
				return GlobalData.RESULT_OK;
			}else
				return res;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!OtherUtil.isNullOrEmpty(result) && result.startsWith(GlobalData.RESULT_OK) && null != listCou)
			{
				CouponAdapter adapter = new CouponAdapter(CouponActivity.this,listCou);
				lvCoupon.setAdapter(adapter);
			}else
			{
				ToastUtil.showToast(CouponActivity.this, result);
			}
				
		}
		
	}
	private class CouponAdapter extends BaseAdapter {
		private Context context;
		private List<CouponListInfo> list;

		public CouponAdapter(Context context,List<CouponListInfo> list) {
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
				convertView = (RelativeLayout) View.inflate(context,
						R.layout.coupon_list_item, null);
				vc = new ViewCache();
				vc.tvSname = (TextView) convertView
						.findViewById(R.id.coupon_tv_sName);
				vc.ivPhoto = (ImageView)convertView.findViewById(R.id.coupon_iv_photos);
				vc.tvInfo = (TextView) convertView
						.findViewById(R.id.coupon_tv_info);
				vc.tvTime = (TextView) convertView
						.findViewById(R.id.coupon_tv_time);
				vc.tvAddress = (TextView) convertView
						.findViewById(R.id.coupon_tv_addre);
				vc.tvDistance = (TextView) convertView
						.findViewById(R.id.coupon_tv_distance);
				convertView.setTag(vc);
			} else {
				vc = (ViewCache) convertView.getTag();
			}
			CouponListInfo info = list.get(position);
			fb.display(vc.ivPhoto,info.getImgUrl());
			vc.tvInfo.setText(info.getCouponName());
			String exTime = getResources().getString(R.string.expire_at);
			vc.tvTime.setText(String.format(exTime, info.getExpireAt()));
			vc.tvSname.setText(info.getMerName());
			fb.display(vc.ivPhoto, info.getImgUrl());
			if(!OtherUtil.isNullOrEmpty(tag) && tag.equals("coupon_me"))
				vc.tvAddress.setVisibility(View.GONE);
			else
			{
				vc.tvAddress.setVisibility(View.VISIBLE);
				vc.tvAddress.setText(info.getMerAddress());
			}
			String distance = getResources().getString(R.string.distance);
			vc.tvDistance.setText(String.format(distance,info.getDistance()));
			return convertView;
		}

		private class ViewCache {
			ImageView ivPhoto;
			TextView tvSname;
			TextView tvInfo;
			TextView tvAddress;
			TextView tvTime;
			TextView tvDistance;
		}
	}
}
