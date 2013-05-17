package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import com.youle.R;
import com.youle.managerData.info.ConsumeInfo;
import com.youle.managerData.info.CouponInfo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CouponActivity extends Activity{
	private ListView lvCoupon;
	private List<CouponInfo> listCou;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_activity);
		initView();
	}
	private void initView()
	{
		lvCoupon = (ListView)findViewById(R.id.coupon_listview);
		listCou = new ArrayList<CouponInfo>();
		listCou.add(new CouponInfo("","一品天下钟鲶鱼午餐200元抵扣券","2013.04.12-2013.12.13","大约100m"));
		listCou.add(new CouponInfo("","一品天下钟鲶鱼午餐200元抵扣券","2013.04.12-2013.12.13","大约100m"));
		listCou.add(new CouponInfo("","一品天下钟鲶鱼午餐200元抵扣券","2013.04.12-2013.12.13","大约100m"));
		CouponAdapter adapter = new CouponAdapter(this, listCou);
		lvCoupon.setAdapter(adapter);
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
				convertView = (RelativeLayout) View.inflate(context,
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
