package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
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
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.info.CarMainInfo;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PullUpListView;
import com.youle.view.PullUpListView.PullListViewListener;

public class CarMainActivity extends StatActivity implements PullListViewListener{
	private List<CarMainInfo> listCar = new ArrayList<CarMainInfo>();
	private PullUpListView lvCar;
	private CarInfoAdapter adapter;
	private int mPage = 1;
	private int loadM = 0;
	private boolean isFirst = true;
	private GestureDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carmain_activity);
		initView();
		MyApplication.getInstance().addActivity(this);
	}
	@SuppressWarnings("deprecation")
	private void initView()
	{
		lvCar = (PullUpListView)findViewById(R.id.car_mainLv);
		lvCar.setPullLoadEnable(true);
		SlipMainCenter.lyButtom.setVisibility(View.GONE);
		SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);
		SlipMainCenter.tvName.setText(R.string.car_friend);
		SlipMainCenter.btnMsgtop.setVisibility(View.GONE);
//		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000108.png", "大众汽车帮派", "512","1","1001"));
//		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000095.png", "奥迪汽车帮派", "1117","1","1002"));
//		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000033.png", "宝马汽车帮派", "2554","1","1003"));
//		listCar.add(new CarMainInfo("http://api.pathtrip.com/avatars/128/1000069.png", "AA汽车帮派", "851","1","1004"));
//		CarInfoAdapter adapter = new CarInfoAdapter(this);
//		lvCar.setAdapter(adapter);
		geneItems(mPage);
		lvCar.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CarMainActivity.this,CarListActivity.class);
				it.putExtra(GlobalData.FORUM_ID, listCar.get(arg2).getForumId());
				startActivity(it);
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
							.get(CarMainActivity.this).getScaledMinimumFlingVelocity()) {
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
		lvCar.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				detector.onTouchEvent(event);
				return false;
			}
		});
	}

	private class ForumTask extends AsyncTask<Integer, Void, String>
	{
		private List<CarMainInfo> tList = new ArrayList<CarMainInfo>();
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (isFirst)
				CustomProgressDialog.stopProgressDialog(CarMainActivity.this);
			isFirst = false;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (isFirst)
				CustomProgressDialog.showMsg(CarMainActivity.this,getString(R.string.please_wait));
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (isFirst) {
				CustomProgressDialog.stopProgressDialog(CarMainActivity.this);
				adapter = new CarInfoAdapter(CarMainActivity.this);
			}
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				if (tList != null) {
					if (null == listCar || listCar.size() == 0)
						listCar = tList;
					else
						listCar.addAll(tList);
				}
				if (mPage > 1 && listCar != null)
					adapter.notifyDataSetChanged();
				else if (null != listCar) {
					lvCar.setAdapter(adapter);
					lvCar.setXListViewListener(CarMainActivity.this);
				}
			}else
				ToastUtil.showToast(CarMainActivity.this, result);
			isFirst = false;
			onLoad();
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getForums(CarMainActivity.this, new SharedPref(CarMainActivity.this).getRadioId(), params[0], 20);
			if (!OtherUtil.isNullOrEmpty(res)
					&& res.startsWith(GlobalData.RESULT_OK)) {
				tList = YouLe.jsonForums(res.substring(3));
				return GlobalData.RESULT_OK;
			}
			return res;
		}
		
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
			fb.display(init.ivAva, info.getLogoUrl());
			init.tvName.setText(info.getName());
			init.tvNum.setText(info.getTotalPosts());
			return convertView;
		}
		private class ViewInit{
			ImageView ivAva;
			TextView tvName;
			TextView tvNum;
		}
	}
	private void geneItems(int page) {
		if (OtherUtil.is3gWifi(CarMainActivity.this)) {
			new ForumTask().execute(page);
		} else {
			onLoad();
			ToastUtil.show(CarMainActivity.this,
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
			if (listCar != null && listCar.size() > 0) {
				mPage++;
				geneItems(mPage);
			}
		}
		
	}
}
