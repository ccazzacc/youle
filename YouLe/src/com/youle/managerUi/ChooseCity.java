package com.youle.managerUi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import org.json.JSONObject;

public class ChooseCity extends Activity {
    private TextView mTxtCity;
    private MyBroadcastReciver reciver;
    private boolean mIsUnRegister;
    private ListView mCityList;
    private String mLocCity;
    private MyAdapter mMyAdapter;
    private SharedPref mSharedPref;
    private String[] address;
    private String url = new StringBuffer().append(YouLe.BASE_URL).append("cities").toString();

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
        mTxtCity.setText("当前城市：" + mSharedPref.getCity());
        mCityList = (ListView) findViewById(R.id.city_list);
        mCityList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int postiton, long arg3) {
                if (postiton > 0) {
                    mSharedPref.saveCity(mMyAdapter.getCity(postiton - 1));
                } else {
                    mSharedPref.saveCity(mLocCity);
                }
                startActivity(new Intent(ChooseCity.this, MainActivity.class));
                finish();
            }
        });
        String[] city = getResources().getStringArray(R.array.city_list);
        mMyAdapter = new MyAdapter(city);
        mCityList.setAdapter(mMyAdapter);
        getData();

    }
    private void getData(){
        AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
            // Callback is invoked with any exceptions/errors, and the result, if available.
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse response, JSONObject result) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                System.out.println("I got a JSONObject: " + result);
            }
        });
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
        double lat, lng;
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ChooseCity.this.unregisterReceiver(MyBroadcastReciver.this);
                mIsUnRegister = true;
                mLocCity = address[0];
                mMyAdapter.notifyDataSetChanged();

            }
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            Log.i("1234", "onReceive  " + action);
            if (action.equals(GlobalData.BROADCAST_COUNTER_ACTION)) {
                // AMapLocation location=(AMapLocation)intent.getExtras();
                Bundle bundle = intent.getExtras();
                lat = bundle.getDouble("lat");
                lng = bundle.getDouble("lng");
                new Thread() {

                    @Override
                    public void run() {
                        address = OtherUtil.getDesc(ChooseCity.this,
                                lat, lng);
                        handler.sendEmptyMessage(0);
                        super.run();
                    }
                }.start();

            }

        }

    }

}
