package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.managerData.info.CarTotalInfo;
import com.youle.util.OtherUtil;

public class CarToatlActivity extends StatActivity{
	private ListView lvCar;
	private List<CarTotalInfo> listCar = new ArrayList<CarTotalInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carmain_activity);
		initView();
	}
	private void initView()
	{
		((LinearLayout)findViewById(R.id.carmain_header)).setVisibility(View.VISIBLE);
		lvCar = (ListView)findViewById(R.id.car_mainLv);
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CarToatlActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		Button btnReply = (Button) findViewById(R.id.twobtn_header_right);
		btnReply.setBackgroundResource(R.drawable.bar_button_reply_normal);
		btnReply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CarToatlActivity.this,AddPostActivity.class);
				it.putExtra("addpost", "reply");
				startActivity(it);
			}
		});
		btnReply.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.total);
		listCar.add(new CarTotalInfo("http://api.pathtrip.com/avatars/128/1000101.png", "test:", "","我梵蒂冈豆腐干分发电公司打工是的发生的发生的发生大幅的few热污染的短发散发", "3天前", "", ""));
		listCar.add(new CarTotalInfo("http://api.pathtrip.com/avatars/128/1000069.png", "一帆风顺", "刺儿","士大夫敢死队风格热天we古典风格的说法的发生的发生的的贵妇狗发生大", "3天前", "http://api.pathtrip.com/covers/10003421360983697.jpg", ""));
		listCar.add(new CarTotalInfo("http://api.pathtrip.com/avatars/128/1000095.png", "天下无敌", "高调炫富","的发生的发生的的发生大", "3天前", "", ""));
		CarInfoAdapter adapter = new CarInfoAdapter(this);
		lvCar.setAdapter(adapter);
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
				convertView = (RelativeLayout)View.inflate(context, R.layout.cartotal_list_item, null);
				init = new ViewInit();
				init.ivAva = (ImageView)convertView.findViewById(R.id.cartotal_ivAva);
				init.tvContent = (TextView)convertView.findViewById(R.id.cartotal_tvContent);
				init.tvName = (TextView)convertView.findViewById(R.id.cartotal_tvName);
				init.tvTime = (TextView)convertView.findViewById(R.id.cartotal_tvTime);
				init.ivPho = (ImageView)convertView.findViewById(R.id.cartotal_ivPho);
				init.tvNreply = (TextView)convertView.findViewById(R.id.cartotal_tvNreply);
				init.tvRname = (TextView)convertView.findViewById(R.id.cartotal_tvRName);
				convertView.setTag(init);
			}else
				init = (ViewInit)convertView.getTag();
			CarTotalInfo info = listCar.get(position);
			fb.display(init.ivAva, info.getAvaUrl());
			init.tvContent.setText(info.getContent());
			init.tvName.setText(info.getuName());
			if(OtherUtil.isNullOrEmpty(info.getReName()))
			{
				init.tvNreply.setVisibility(View.GONE);
				init.tvRname.setVisibility(View.GONE);
			}else
			{
				init.tvNreply.setVisibility(View.VISIBLE);
				init.tvRname.setVisibility(View.VISIBLE);
				init.tvRname.setText(info.getReName());
			}
			init.tvTime.setText(info.getTime());
			if(OtherUtil.isNullOrEmpty(info.getPhoUrl()))
				init.ivPho.setVisibility(View.GONE);
			else
			{
				init.ivPho.setVisibility(View.VISIBLE);
				fb.display(init.ivPho, info.getPhoUrl());
			}
			return convertView;
		}
		private class ViewInit{
			ImageView ivAva;
			TextView tvContent;
			TextView tvName;
			TextView tvTime;
			ImageView ivPho;
			TextView tvNreply;
			TextView tvRname;
			
		}
	}
}
