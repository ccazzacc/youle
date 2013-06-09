package com.youle.managerUi;

import android.app.Activity;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.search.core.AMapException;
import com.amap.api.search.geocoder.Geocoder;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.MultipartFormDataBody;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.OtherUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApplyShopActivity extends Activity {
    private String mApplyUrl = new StringBuffer().append(YouLe.BASE_URL).append("merchants").toString();
    private EditText merchants_name, merchants_link, merchants_phone, merchants_address;
    private Geocoder coder;
    private String url = new StringBuffer().append(YouLe.BASE_URL).append("categories/all").toString();
    private List<String> mList1 = new ArrayList<String>();
    private List<Categories> mList2 = new ArrayList<Categories>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applyshop_activity);
        initView();
        coder = new Geocoder(this);
    }

    private void initView() {
        SharedPref sharedPref = new SharedPref(ApplyShopActivity.this);
        merchants_name = (EditText) findViewById(R.id.applyshop_etShop);
        merchants_link = (EditText) findViewById(R.id.applyshop_etLinkman);
        merchants_phone = (EditText) findViewById(R.id.applyshop_etPhone);
        merchants_address = (EditText) findViewById(R.id.applyshop_etAdress);

        TextView textCity = (TextView) findViewById(R.id.applyshop_txtCity);
        if (OtherUtil.isNullOrEmpty(sharedPref.getLocCity())) {
            textCity.setText(sharedPref.getCity());
        } else {
            textCity.setText(sharedPref.getLocCity());
        }
        Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
        btnBack.setBackgroundResource(R.drawable.bar_icon_back);
        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ApplyShopActivity.this.finish();
            }
        });
        btnBack.setVisibility(View.VISIBLE);
        TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
        tvTitle.setText(R.string.apply_shop);
        Button btnCommit = (Button) findViewById(R.id.applyshop_btnCommit);
        btnCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (OtherUtil.isNullOrEmpty(merchants_name.getText().toString()) || OtherUtil.isNullOrEmpty(merchants_link.getText().toString()) || OtherUtil.isNullOrEmpty(merchants_phone.getText().toString()) || OtherUtil.isNullOrEmpty(merchants_address.getText().toString())) {
                    return;
                } else {
                    send();
                }
//                ApplyShopActivity.this.finish();
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.applyshop_imgSer);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "四川省成都市东陂北二路687号";
                getLatlon(name);
            }
        });
        getData();
    }

    private void getData() {
        AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
            // Callback is invoked with any exceptions/errors, and the result, if available.
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse response, JSONObject result) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                try {
                    JSONArray array=result.getJSONArray("categories");
                    for(int i=0;i<array.length();i++){
                        JSONObject obj2 = array.getJSONObject(i);
                        mList1.add(obj2.getString("name"));
//                        Log.i("1234",obj2.getString("name")+"");
                        JSONArray array2=obj2.getJSONArray("sub_categories");
                        for(int ii=0;ii<array2.length();ii++){
                            JSONObject obj3 = array2.getJSONObject(ii);
//                            Log.i("1234",obj2.getInt("category_id")+" "+obj3.getInt("category_id")+" "+obj3.getString("name"));
                            Categories categories=new Categories(obj2.getInt("category_id"),obj3.getInt("category_id"),obj3.getString("name"));
                            mList2.add(categories);

                        }
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                for(int i=0;i<mList2.size();i++){
                    Log.i("1234","list” "+mList2.get(i).fstId+" "+mList2.get(i).sedId+" "+mList2.get(i).cname);
                }

            }
        });
    }

    private void send() {

        AsyncHttpPost post = new AsyncHttpPost(mApplyUrl);
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("access_token", Utility.mToken.getAccess_token());
        body.addStringPart("user_id", Utility.mToken.getUser_id());
        body.addStringPart("city_id", "");
        body.addStringPart("category_id", "");
        body.addStringPart("name", merchants_name.getText().toString());
        body.addStringPart("address", merchants_address.getText().toString());
        body.addStringPart("phone", merchants_phone.getText().toString());
        body.addStringPart("contact", merchants_link.getText().toString());
        body.addStringPart("license", "");
        body.addStringPart("lng", "");
        body.addStringPart("lat", "");
        body.addStringPart("img", "");
        body.addStringPart("summary", "");
        body.addStringPart("memo", "");
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().execute(post, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Log.i("1234", "JSONObjectCallback: " + jsonObject);
            }
        });

    }

    // 地理编码
    public void getLatlon(final String name) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    List<Address> address = coder.getFromLocationName(name, 3);
                    if (address != null && address.size() > 0) {
                        Address addres = address.get(0);
                        String addressName = addres.getLatitude() + ","
                                + addres.getLongitude();
                        Log.i("1234", addressName);
                    }
                } catch (AMapException e) {

                }

            }
        });
        t.start();
    }
    private class Categories{
        public int fstId;
        public int sedId;
        public String cname;
        public Categories(int f,int s,String c){
            fstId=f;
            sedId=s;
            cname=c;
        }
    }
}
