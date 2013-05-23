package com.youle.managerUi;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.youle.R;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

public class ChooseCity extends Activity {
	private TextView mTxtCity;
	private MyBroadcastReciver reciver;
	private boolean mIsUnRegister;
	private ListView mCityList;
	private String mLocCity;
	private MyAdapter mMyAdapter;
	private SharedPref mSharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_city_activity);
		mSharedPref = new SharedPref(ChooseCity.this);
		initView();
		
		OtherUtil.getLocation(ChooseCity.this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalData.BROADCAST_COUNTER_ACTION);
		reciver = new MyBroadcastReciver();
		this.registerReceiver(reciver, intentFilter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (!mIsUnRegister) {
			this.unregisterReceiver(reciver);
		}

	}

	private void initView() {
		mTxtCity = (TextView) findViewById(R.id.text_city);
		mTxtCity.setText("当前城市："+mSharedPref.getCity());
		mCityList = (ListView) findViewById(R.id.city_list);
		mCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int postiton, long arg3) {
                if (postiton > 0) {
					mSharedPref.saveCity(mMyAdapter.getCity(postiton - 1));
				}else{
					mSharedPref.saveCity(mLocCity);
				}
                startActivity(new Intent(ChooseCity.this,MainActivity.class));
                finish();
			}
		});
		String[] city = getResources().getStringArray(R.array.city_list);
		mMyAdapter = new MyAdapter(city);
		mCityList.setAdapter(mMyAdapter);

	}

	public class MyAdapter extends BaseAdapter {
		String[] city;

		public MyAdapter(String[] city) {
			this.city = city;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return city.length + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getCity(int postiton) {
			return city[postiton];
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			final ViewHolder holder;
			if (convertView == null || view == null) {
				view = getLayoutInflater().inflate(R.layout.city_list_item,
						parent, false);
				holder = new ViewHolder();
				holder.tit = (TextView) view.findViewById(R.id.city_item_tit);
				holder.text = (TextView) view.findViewById(R.id.city_item);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (position > 1) {
				holder.tit.setVisibility(View.GONE);
			}
			if (position > 0) {
				holder.tit.setText("热门城市");
				holder.text.setText(city[position - 1]);
			} else {
				holder.tit.setText("定位城市");
				if (mLocCity == null) {
					holder.text.setText(getString(R.string.locationing));
				} else {
					holder.text.setText(mLocCity);
				}

			}
			return view;
		}

		private class ViewHolder {
			public TextView tit;
			public TextView text;
		}

	}

	public class MyBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.i("1234", "onReceive  " + action);
			if (action.equals(GlobalData.BROADCAST_COUNTER_ACTION)) {
				// AMapLocation location=(AMapLocation)intent.getExtras();
				Bundle bundle = intent.getExtras();
				Log.i("1234", "onReceive  " + bundle.getDouble("lat") + " "
						+ bundle.getDouble("lng"));
				if (getDesc(bundle.getDouble("lat"), bundle.getDouble("lng"))) {
					ChooseCity.this.unregisterReceiver(this);
					mIsUnRegister = true;
				}
			}

		}

		// 05-14 11:21:32.287: I/1234(6600): onReceive 30.7078 104.006

		private boolean getDesc(double lat, double lng) {
			Geocoder coder = new Geocoder(ChooseCity.this);
			List<Address> address = null;
			try {
				address = coder.getFromLocation(30.7078, 104.006, 3);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("1234", "没取到~" + e.getMessage());
				OtherUtil.getLocation(ChooseCity.this);
				return false;
			}
			if (address != null && address.size() > 0) {
				Address addres = address.get(0);
				String addressName = addres.getAdminArea()
						+ addres.getSubLocality() + addres.getThoroughfare();
				Log.i("1234", "" + addres.getLocality());
				mLocCity = addres.getLocality();
				mMyAdapter.notifyDataSetChanged();
				return true;
			}
			return false;
		}

	}

}
