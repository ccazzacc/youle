package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import net.tsz.afinal.FinalBitmap;

import com.youle.R;
import com.youle.managerData.info.CarListDetailInfo;
import com.youle.managerData.info.CarListInfo;
import com.youle.util.OtherUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CarListActivity extends Activity{
	private CarListInfo carInfo;
	private List<CarListDetailInfo> carList = new ArrayList<CarListDetailInfo>();
	private ListView lvCar;
	private FinalBitmap fb;
	private View hView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carmain_activity);
		fb = FinalBitmap.create(this);
		fb.onResume();
		initView();
	}
	private void initView()
	{
		lvCar = (ListView)findViewById(R.id.car_mainLv);
		hView = LayoutInflater.from(this).inflate(
				R.layout.carlist_header, null);
		ImageView ivAva = (ImageView)hView.findViewById(R.id.carlist_hiv_ava);
		fb.display(ivAva, "http://api.pathtrip.com/avatars/128/1000095.png");
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		lvCar.addHeaderView(hView);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CarListActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		Button btnPub = (Button) findViewById(R.id.twobtn_header_right);
		btnPub.setBackgroundResource(R.drawable.bar_btn_nothing);
		btnPub.setText(R.string.publish);
		btnPub.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CarListActivity.this,AddPostActivity.class);
				it.putExtra("addpost", "publish");
				startActivity(it);
			}
		});
		btnPub.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.car_friend);
		carList.add(new CarListDetailInfo("http://api.pathtrip.com/avatars/128/1000108.png", "明天汽油降价了！","http://api.pathtrip.com/avatars/128/1000033.png","","test", "145", "5分前"));
		carList.add(new CarListDetailInfo("http://api.pathtrip.com/avatars/128/1000095.png", "啦啦啦啦啦！豆腐干大概入土为儿童的发放的发改委特的发生大幅的人反而佛挡杀佛是的发生的的few的发生的日是的粉色二万人的发生大幅","","","aaa", "552", "9分前"));
		carList.add(new CarListDetailInfo("http://api.pathtrip.com/avatars/128/1000033.png", "阿凡达是广东分公司大的说法撒旦法梵蒂冈梵蒂冈斯蒂芬豆腐干豆腐干斯蒂芬否斯蒂芬斯蒂芬但是打发似的","http://api.pathtrip.com/avatars/128/1000095.png","","热啊", "7852", "14分前"));
		carList.add(new CarListDetailInfo("http://api.pathtrip.com/avatars/128/1000069.png", "月份汇丰供货商反观对手犯规","","","而俺是", "451", "19分前"));
		carInfo = new CarListInfo("http://api.pathtrip.com/avatars/128/1000033.png", "resa", "234", "234", carList);
		CarInfoAdapter adapter = new CarInfoAdapter(this);
		lvCar.setAdapter(adapter);
		lvCar.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				startActivity(new Intent(CarListActivity.this,CarToatlActivity.class));
				CarListActivity.this.finish();
			}
		});
	}
	private class CarInfoAdapter extends BaseAdapter{
		private Context context;
		public CarInfoAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return carList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return carList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewInit init;
			if(convertView == null)
			{
				convertView = (RelativeLayout)View.inflate(context, R.layout.carlist_list_item, null);
				init = new ViewInit();
				init.ivAva = (ImageView)convertView.findViewById(R.id.carlist_ivAva);
				init.tvContent = (TextView)convertView.findViewById(R.id.carlist_tvContent);
				init.ivPhoto = (ImageView)convertView.findViewById(R.id.carlist_ivPhoto);
				init.tvName = (TextView)convertView.findViewById(R.id.carlist_tvName);
				init.tvReply = (TextView)convertView.findViewById(R.id.carlist_tvReply);
				init.tvTime = (TextView)convertView.findViewById(R.id.carlist_tvTime);
				convertView.setTag(init);
			}else
				init = (ViewInit)convertView.getTag();
			CarListDetailInfo info = carList.get(position);
			fb.display(init.ivAva, info.getAvaUrl());
			init.tvContent.setText(info.getContent());
			init.tvName.setText(info.getName());
			init.tvReply.setText(info.getReply());
			init.tvTime.setText(info.getTime());
			if(OtherUtil.isNullOrEmpty(info.getPhotoUrl()))
				init.ivPhoto.setVisibility(View.INVISIBLE);
			else
			{
				init.ivPhoto.setVisibility(View.VISIBLE);
				fb.display(init.ivPhoto, info.getPhotoUrl());
			}
			return convertView;
		}
		private class ViewInit{
			ImageView ivAva;
			TextView tvContent;
			ImageView ivPhoto;
			TextView tvName;
			TextView tvReply;
			TextView tvTime;
		}
	}
}
