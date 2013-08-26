package com.youle.managerUi;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.baidu.mobstat.StatActivity;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.ShowDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChooseCity extends StatActivity {
    private TextView mTxtCity;
    private MyBroadcastReciver reciver;
    private boolean mIsUnRegister, isRadio, isCity;
    private ListView mCityList;
    private String mLocCity;
    private MyAdapter mMyAdapter;
    private SharedPref mSharedPref;
    private String[] address;
    private String url = new StringBuffer().append(YouLe.BASE_URL).append("cities").toString();
    private List<CityInfo> mCityData = new ArrayList<CityInfo>();
    private int mCityId;

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
        MyApplication.getInstance().addActivity(this);
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
        mTxtCity.setText(getString(R.string.current_city) + mSharedPref.getCity());
        mCityList = (ListView) findViewById(R.id.city_list);
        mCityList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int postiton, long arg3) {
                if (postiton > 0) {
                    Log.i("1234", mCityData.get(postiton - 1).city_name);
                    mSharedPref.saveCity(mCityData.get(postiton - 1).city_id, mCityData.get(postiton - 1).city_name);
                    isRadio = true;
                    mCityId = mCityData.get(postiton - 1).city_id;

                } else {
                    if (isCity) {
                        mSharedPref.saveCity(mCityId, mLocCity);
                        isRadio = true;
                    } else {
                        String s=String.format(getString(R.string.trying_togo),mLocCity);
                        ToastUtil.show(ChooseCity.this,s);
//                        Intent intent = new Intent(ChooseCity.this, ShowDialog.class);
//                        intent.putExtra("showTwo", false);
//                        intent.putExtra("dialog_title", "提  示");
//                        intent.putExtra("dialog_text", "我们正在努力奔赴" + mLocCity + "，目前仅为热门城市服务！");
//                        startActivity(intent);
                        return;
                    }
                }
                getData();
                startActivity(new Intent(ChooseCity.this, SlidActivity.class));
                finish();
            }
        });
        String[] city = getResources().getStringArray(R.array.city_list);

        getData();

    }

    private void getData() {
        mLocCity = getString(R.string.locationing);
        if (isRadio) {
            url = new StringBuffer().append(YouLe.BASE_URL).append("radios?city_id=" + mCityId).toString();
            Log.i("1234", "yrl: " + url);
        }
        AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
            // Callback is invoked with any exceptions/errors, and the result, if available.
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse response, JSONObject result) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                System.out.println("I got a JSONObject: " + result);
                if (isRadio) {
                    try {
                        JSONArray jsonArray = result.getJSONArray("radios");
                        String radio_name = "";
                        int radio_id = 0;
//                        for(int i=0;i<jsonArray.length();i++){
                        JSONObject obj = jsonArray.getJSONObject(0);
                        radio_id = obj.getInt("radio_id");
                        radio_name = obj.getString("name");
//                        }
                        Log.i("1234", "电台名字：" + radio_id + " " + radio_name);
                        mSharedPref.saveRadio(radio_id, radio_name);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    try {
                        JSONArray array = result.getJSONArray("cities");
                        String[] city = new String[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj2 = array.getJSONObject(i);
                            CityInfo cityInfo = new CityInfo(obj2.getInt("city_id"), obj2.getString("name"));
                            mCityData.add(cityInfo);
                            Log.i("1234", obj2 + "");
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    mMyAdapter = new MyAdapter();
                    mCityList.setAdapter(mMyAdapter);
                }
            }
        });

    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mCityData.size() + 1;
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
            return mCityData.get(postiton).city_name;
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
                holder.layout = (LinearLayout) view.findViewById(R.id.city_item_layout);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (position > 1) {
                holder.layout.setVisibility(View.GONE);
                holder.tit.setVisibility(View.GONE);
            }
            if (position > 0) {
                holder.tit.setText(getString(R.string.hot_city));
                holder.text.setText(mCityData.get(position - 1).city_name);
                if (Pattern.compile(mCityData.get(position - 1).city_name).matcher(mLocCity).find()) {
                    Log.i("1234", Pattern.compile(mCityData.get(position - 1).city_name).matcher(mLocCity).find() + " " + mCityData.get(position - 1).city_name);
                    mCityId = mCityData.get(position - 1).city_id;
                    isCity = true;
                }

            } else {
                holder.tit.setText(getString(R.string.loc_city));
                holder.text.setText(mLocCity);
            }
            return view;
        }

        private class ViewHolder {
            public TextView tit;
            public TextView text;
            public LinearLayout layout;
        }

    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        double lat, lng;
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!mIsUnRegister) {
                    ChooseCity.this.unregisterReceiver(MyBroadcastReciver.this);
                }
                if (!address[3].equals("中国")) {
                    OtherUtil.showCloseDialog(ChooseCity.this);
                }
                mIsUnRegister = true;
                mLocCity = address[0];
                if (mMyAdapter != null)
                    mMyAdapter.notifyDataSetChanged();
            }
        };

        @Override
        public void onReceive(Context context, Intent intent) {
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
                        address = OtherUtil.getDesc(ChooseCity.this, lat, lng);
                        handler.sendEmptyMessage(0);
                        super.run();
                    }
                }.start();
            }
        }



    }

    class CityInfo {
        public int city_id;
        public String city_name;

        public CityInfo(int id, String name) {
            this.city_id = id;
            this.city_name = name;
        }
    }

}
