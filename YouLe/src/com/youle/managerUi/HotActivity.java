package com.youle.managerUi;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.OtherUtil;
import com.youle.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HotActivity extends StatActivity {
    private XListView xListView;
    private HotAdapter hotAdapter = new HotAdapter();
    private int mPage = 1;
    private boolean mIsRefresh;
    private List<HotInfo> hotInfoList = new ArrayList<HotInfo>();
    AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("1234", hotInfoList.get(i-1).uri);
            Intent intent=new Intent();
            intent.putExtra("replies",hotInfoList.get(i-1).replies);
            intent.putExtra("topic_id",hotInfoList.get(i-1).topic_id);
            intent.putExtra("uri",hotInfoList.get(i-1).uri);
            intent.setClass(HotActivity.this,HotDetailActivity.class);
            startActivity(intent);
        }
    };
    XListView.IXListViewListener xListViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            xListView.stopLoadMore();
            mIsRefresh = true;
            mPage = 1;
            getHot();
        }

        @Override
        public void onLoadMore() {
            xListView.stopRefresh();
            mPage++;
            mIsRefresh = false;
            getHot();
            xListView.setSelection(hotInfoList.size() - 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hot_activity);
        MyApplication.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);
        SlipMainCenter.lyButtom.setVisibility(View.GONE);
        SlipMainCenter.tvName.setText(getString(R.string.topic));

        xListView = (XListView) findViewById(R.id.hot_listview);
        xListView.setOnItemClickListener(itemClick);
        xListView.setPullLoadEnable(true);

        xListView.setXListViewListener(xListViewListener);
        xListView.setAdapter(hotAdapter);

        getHot();

    }

    public void getHot() {
        String url = new StringBuffer().append(YouLe.BASE_URL).append("hot_topics").append("?access_token=")
                .append(Utility.mToken.getAccess_token())
                .append("&radio_id=").append(new SharedPref(HotActivity.this).getRadioId())
                .append("&page=").append(mPage)
                .append("&size=10")
                .toString();
        Log.i("1234", "url: " + url);
        if (mIsRefresh) {
            hotInfoList = new ArrayList<HotInfo>();
        }
        AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Log.i("1234", jsonObject + "");
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("topics");
                    if (jsonArray.length() < 1) {
                        xListView.stopLoadMore();
                        xListView.stopRefresh();
                        TextView textView = (TextView) findViewById(R.id.hot_tv);
                        textView.setVisibility(View.VISIBLE);
                        return;
                    }
                    int count = jsonArray.length();
                    for (int i = 0; i < count; i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int topicId = obj.getInt("topic_id");
                        int radioId = obj.getInt("radio_id");
                        int userId = obj.getInt("user_id");
                        String username = obj.getString("username");
                        String subject = obj.getString("subject");
                        String uri = obj.getString("uri");
                        String summary = obj.getString("summary");
                        int created = obj.getInt("created");
                        int replies = obj.getInt("replies");
                        HotInfo info = new HotInfo(subject, uri, "", created, replies, topicId, radioId, userId, username,summary);
                        hotInfoList.add(info);
                    }
                    if (hotInfoList.size() > 0) {
                        hotAdapter.notifyDataSetChanged();
                        xListView.stopLoadMore();
                        xListView.stopRefresh();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        });

    }

    private class HotAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return hotInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = null;
            final ViewHolder holder;
            if (convertView == null || view == null) {
                view = getLayoutInflater().inflate(R.layout.hot_item,
                        viewGroup, false);
                holder = new ViewHolder();
                holder.tit = (TextView) view.findViewById(R.id.hot_title);
                holder.date = (TextView) view.findViewById(R.id.hot_date);
                holder.summary = (TextView) view.findViewById(R.id.hot_con);
                holder.count = (TextView) view.findViewById(R.id.hot_count);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.tit.setText(hotInfoList.get(i).subject);
            holder.summary.setText(hotInfoList.get(i).summary);
            holder.count.setText(hotInfoList.get(i).replies+getString(R.string.posts));
            holder.date.setText(YouLe.formatDate(hotInfoList.get(i).created));
//            holder.ivAv
            return view;
        }

        private class ViewHolder {
            public TextView tit;
            public TextView summary;
            public TextView date;
            public TextView count;
        }
    }

    private class HotInfo {
        public String subject, uri, av, userName,summary;
        public long created;
        public int topic_id, radioId, userId, replies;

        public HotInfo(String title, String uri, String av, int date, int count, int topic_id, int radioId, int userId, String userName,String summary) {
            this.subject = title;
            this.av = av;
            this.uri = uri;
            this.created = date;
            this.replies = count;
            this.topic_id = topic_id;
            this.radioId = radioId;
            this.userId = userId;
            this.userName = userName;
            this.summary = summary;
        }
    }

}
