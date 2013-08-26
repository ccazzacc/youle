package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.info.CarListInfo;
import com.youle.managerData.info.CarTopicInfo;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PullUpListView;
import com.youle.view.PullUpListView.PullListViewListener;

public class CarListActivity extends StatActivity implements PullListViewListener{
	private CarListInfo carInfo;
	private List<CarTopicInfo> carList = new ArrayList<CarTopicInfo>();
	private PullUpListView lvCar;
	private FinalBitmap fb;
	private View hView;
	private ImageView ivAva;
	private TextView tvTiezi,tvReply,tvName,tvTi;
	private int mPage = 1;
	private int loadM = 0;
	private String forumId;
	private CarInfoAdapter adapter;
	private boolean isFirst = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carlist_activity);
		fb = FinalBitmap.create(this);
		fb.onResume();
		initView();
		MyApplication.getInstance().addActivity(this);
	}
	private void initView()
	{
		forumId = getIntent().getStringExtra(GlobalData.FORUM_ID);
		Log.e("test", "forumId:"+forumId);
		lvCar = (PullUpListView)findViewById(R.id.car_listLv);
		lvCar.setPullLoadEnable(true);
		hView = LayoutInflater.from(this).inflate(
				R.layout.carlist_header, null);
		ivAva = (ImageView)hView.findViewById(R.id.carlist_hiv_ava);
		tvName = (TextView)hView.findViewById(R.id.carlist_htv_name);
		tvTiezi = (TextView)hView.findViewById(R.id.carlist_htv_tiezi);
		tvReply = (TextView)hView.findViewById(R.id.carlist_htv_reply);
		tvTi = (TextView)findViewById(R.id.car_listTvTitle);
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CarListActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		Button btnPub = (Button) findViewById(R.id.twobtn_header_right);
		btnPub.setBackgroundResource(R.drawable.bar_button_postsubject_normal);
		btnPub.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CarListActivity.this,AddPostActivity.class);
				it.putExtra("addpost", "publish");
				it.putExtra(GlobalData.FORUM_ID, forumId);
				startActivity(it);
				CarListActivity.this.finish();
			}
		});
		btnPub.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.car_friend);
//		carList.add(new CarTopicInfo("1001","明天汽油降价了！","", "","1002","sdfasdf","http://api.pathtrip.com/avatars/128/1000033.png","145", "5分钟前"));
//		carList.add(new CarTopicInfo("1000","啦啦啦啦啦！豆腐干大概入土为儿童的发放的发改委特的发生大幅的人反而佛挡杀佛是的发生的的few的发生的日是的粉色二万人的发生大幅","http://api.pathtrip.com/avatars/128/1000108.png", "","1002","第三方","http://api.pathtrip.com/avatars/128/1000033.png","145", "6分钟前"));
//		carList.add(new CarTopicInfo("1010","阿凡达是广东分公司大的说法撒旦法梵蒂冈梵蒂冈斯蒂芬豆腐干豆腐干斯蒂芬否斯蒂芬斯蒂芬但是打发似的","http://api.pathtrip.com/avatars/128/1000108.png", "","1002","啊啊","http://api.pathtrip.com/avatars/128/1000033.png","145", "8分钟前"));
//		carList.add(new CarTopicInfo("1011","月份汇丰供货商反观对手犯规","", "","1002","额额","http://api.pathtrip.com/avatars/128/1000033.png","145", "15分钟前"));
//		carInfo = new CarListInfo("http://api.pathtrip.com/avatars/128/1000033.png", "resa", "234", "234", carList);
//		CarInfoAdapter adapter = new CarInfoAdapter(this);
//		lvCar.setAdapter(adapter);
		geneItems(mPage);
//		lvCar.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				startActivity(new Intent(CarListActivity.this,CarReplyActivity.class));
////				CarListActivity.this.finish();
//			}
//		});
	}
	private class ForumTask extends AsyncTask<Integer, Void, String>
	{
		List<CarTopicInfo> tList = new ArrayList<CarTopicInfo>();
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (isFirst) {
				CustomProgressDialog.stopProgressDialog(CarListActivity.this);
				adapter = new CarInfoAdapter(CarListActivity.this);
				lvCar.addHeaderView(hView);
			}
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				if(null != carInfo)
				{
					if (isFirst) {
						fb.display(ivAva, carInfo.getAvaUrl());
						tvName.setText(carInfo.getName());
						tvTiezi.setText(carInfo.gettPost());
						tvReply.setText(carInfo.gettReply());
					}
					tList = carInfo.getList();
					if (tList != null) {
						if (null == carList || carList.size() == 0)
							carList = tList;
						else
							carList.addAll(tList);
					}
					if (mPage > 1 && carList != null)
						adapter.notifyDataSetChanged();
					else if (null != carList) {
						lvCar.setAdapter(adapter);
						lvCar.setXListViewListener(CarListActivity.this);
					}
				}
			}else
				ToastUtil.showToast(CarListActivity.this, result);
			if(null == carList || carList.size() == 0)
				tvTi.setVisibility(View.VISIBLE);
			else
				tvTi.setVisibility(View.GONE);
			isFirst = false;
			onLoad();
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getForumsTopic(CarListActivity.this, forumId, mPage);
			if (!OtherUtil.isNullOrEmpty(res)
					&& res.startsWith(GlobalData.RESULT_OK)) {
				carInfo = YouLe.jsonForumTopic(res.substring(3));
				return GlobalData.RESULT_OK;
			}
			return res;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (isFirst)
				CustomProgressDialog.stopProgressDialog(CarListActivity.this);
			isFirst = false;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (isFirst)
				CustomProgressDialog.showMsg(CarListActivity.this,
						getString(R.string.please_wait));
		}
		
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
				init.ivSound = (ImageView)convertView.findViewById(R.id.carlist_ivSound);
				init.tvName = (TextView)convertView.findViewById(R.id.carlist_tvName);
				init.tvReply = (TextView)convertView.findViewById(R.id.carlist_tvReply);
				init.tvTime = (TextView)convertView.findViewById(R.id.carlist_tvTime);
				init.ivPic = (ImageView)convertView.findViewById(R.id.carlist_ivPic);
				convertView.setTag(init);
			}else
				init = (ViewInit)convertView.getTag();
			final CarTopicInfo info = carList.get(position);
			fb.display(init.ivAva, info.getAvaUrl());
			if(OtherUtil.isNullOrEmpty(info.getContent())|| "null".equals(info.getContent()))
				init.tvContent.setVisibility(View.GONE);
			else
			{
				init.tvContent.setVisibility(View.VISIBLE);
				init.tvContent.setText(info.getContent());
			}
			init.tvName.setText(info.getUserName());
			init.tvReply.setText(info.getReply());
			init.tvTime.setText(info.getCreateTime());
			if(OtherUtil.isNullOrEmpty(info.getAudUrl()))
				init.ivSound.setVisibility(View.GONE);
			else
				init.ivSound.setVisibility(View.VISIBLE);
			if(OtherUtil.isNullOrEmpty(info.getImgUrl()))
				init.ivPic.setVisibility(View.GONE);
			else
				init.ivPic.setVisibility(View.VISIBLE);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent(CarListActivity.this,CarReplyActivity.class);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					it.putExtra(GlobalData.POST_ID, info.getPostId());
					startActivity(it);
					CarListActivity.this.finish();
				}
			});
			return convertView;
		}
		private class ViewInit{
			ImageView ivAva;
			TextView tvContent;
			ImageView ivSound;
			ImageView ivPic;
			TextView tvName;
			TextView tvReply;
			TextView tvTime;
		}
	}
	private void geneItems(int page) {
		if (OtherUtil.is3gWifi(CarListActivity.this)) {
			new ForumTask().execute(page);
		} else {
			onLoad();
			ToastUtil.show(CarListActivity.this,
					R.string.net_no);
		}
	}

	private void onLoad() {
		lvCar.stopRefresh();
		lvCar.stopLoadMore();
		loadM = 0;
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadM++;
		if(loadM == 1)
		{
			if (carList != null && carList.size() > 0) {
				mPage++;
				geneItems(mPage);
			}
		}
		
	}
}
