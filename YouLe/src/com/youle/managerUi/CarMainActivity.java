package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.youle.R;
import com.youle.managerData.info.CarMainInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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

public class CarMainActivity extends Activity{
	private List<CarMainInfo> listCar = new ArrayList<CarMainInfo>();
	private ListView lvCar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carmain_activity);
		initView();
	}
	private void initView()
	{
		lvCar = (ListView)findViewById(R.id.car_mainLv);
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CarMainActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.car_friend);
		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000108.png", "大众汽车帮派", "51283456556"));
		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000095.png", "奥迪汽车帮派", "2334345621"));
		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000033.png", "宝马汽车帮派", "255454545262"));
		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000069.png", "AA汽车帮派", "851235345566"));
		CarInfoAdapter adapter = new CarInfoAdapter(this);
		lvCar.setAdapter(adapter);
		lvCar.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				startActivity(new Intent(CarMainActivity.this,CarListActivity.class));
			}
		});
	}
	private class CarInfoAdapter extends BaseAdapter{
		private Context context;
		private FinalBitmap fb;
		public CarInfoAdapter(Context context) {
			this.context = context;
			fb = FinalBitmap.create(context);
			fb.onResume();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listCar.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listCar.get(position);
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
				convertView = (RelativeLayout)View.inflate(context, R.layout.carmain_list_item, null);
				init = new ViewInit();
				init.ivAva = (ImageView)convertView.findViewById(R.id.carMain_ivAva);
				init.tvName = (TextView)convertView.findViewById(R.id.carMain_tvName);
				init.tvNum = (TextView)convertView.findViewById(R.id.carMain_tvNum);
				convertView.setTag(init);
			}else
				init = (ViewInit)convertView.getTag();
			CarMainInfo info = listCar.get(position);
			fb.display(init.ivAva, info.getAvaUrl());
			init.tvName.setText(info.getName());
			init.tvNum.setText(info.getNum());
			return convertView;
		}
		private class ViewInit{
			ImageView ivAva;
			TextView tvName;
			TextView tvNum;
		}
	}
}
