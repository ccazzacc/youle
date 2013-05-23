package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youle.R;
import com.youle.managerData.info.CouponInfo;

public class CouponActivity extends Activity{
	private ListView lvCoupon;
	private List<CouponInfo> listCou;
	private FinalBitmap fb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_activity);
		initView();
		fb = FinalBitmap.create(this);
	}
	private void initView()
	{
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CouponActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView)findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.coupon);
		lvCoupon = (ListView)findViewById(R.id.coupon_listview);
		listCou = new ArrayList<CouponInfo>();
		listCou.add(new CouponInfo("","一品天下钟鲶鱼午餐200元抵扣券","2013.04.12-2013.12.13","大约100m"));
		listCou.add(new CouponInfo("","一品天下红袖酒家100元抵扣券","2013.04.12-2013.12.13","大约200m"));
		listCou.add(new CouponInfo("","一品天下清粥小菜50元抵扣券","2013.04.12-2013.12.13","大约400m"));
		CouponAdapter adapter = new CouponAdapter(this, listCou);
		lvCoupon.setAdapter(adapter);
		lvCoupon.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				startActivity(new Intent(CouponActivity.this,CouponDetailActivity.class));
				overridePendingTransition(
						R.anim.push_left_in, R.anim.push_left_out);
			}
		});
	}
	private class CouponAdapter extends BaseAdapter {
		private Context context;
		private List<CouponInfo> list;

		public CouponAdapter(Context context, List<CouponInfo> list) {
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
						R.layout.coupon_list_item, null);
				vc = new ViewCache();
				vc.ivPhoto = (ImageView)convertView.findViewById(R.id.coupon_iv_photo);
				vc.tvInfo = (TextView) convertView
						.findViewById(R.id.coupon_tv_info);
				vc.tvTime = (TextView) convertView
						.findViewById(R.id.coupon_tv_time);
				vc.tvKilo = (TextView) convertView
						.findViewById(R.id.coupon_tv_kilo);
				convertView.setTag(vc);
			} else {
				vc = (ViewCache) convertView.getTag();
			}
			CouponInfo info = list.get(position);
			vc.tvInfo.setText(info.getsInfo());
			vc.tvTime.setText(info.getsTime());
			vc.tvKilo.setText(info.getsKilo());
			fb.display(vc.ivPhoto, "http://api.pathtrip.com/covers/10004601365843861.jpg");
			return convertView;
		}

		private class ViewCache {
			ImageView ivPhoto;
			TextView tvInfo;
			TextView tvTime;
			TextView tvKilo;
		}
	}
}
