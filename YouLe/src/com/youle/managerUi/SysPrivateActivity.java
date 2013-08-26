package com.youle.managerUi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.service.SystemMsgService;
import com.youle.util.OtherUtil;
import com.youle.view.XListView;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaofuchao on 13-7-19.
 */
public class SysPrivateActivity extends Activity {
    private int mPage = 1;
    private boolean mIsRefresh;
    private FromListAdapter fromListAdapter = new FromListAdapter();
    private List<FromInfo> mFromList = new ArrayList<FromInfo>();
    private FinalBitmap fb;
    private XListView xListView;
    private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(SysPrivateActivity.this, SysPrivateMsgActivity.class);
            intent.putExtra("user_id", mFromList.get(i - 1).userId);
            startActivity(intent);
        }
    };
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.header_left:
                    SlidActivity.showMenu();
                    break;
                case R.id.sysmsg_top:
                    SlipMainCenter.slidIntent(5, false);
                    break;
            }
        }
    };
    XListView.IXListViewListener xListViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            xListView.stopLoadMore();
            mIsRefresh = true;
            mPage = 1;
            getData();
        }

        @Override
        public void onLoadMore() {
            xListView.stopRefresh();
            mPage++;
            mIsRefresh = false;
            getData();
            xListView.setSelection(mFromList.size() - 1);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_private_activity);
        fb = FinalBitmap.create(SysPrivateActivity.this);
        fb.onResume();
        initView();
    }

    @Override
    protected void onResume() {
        Log.i("1234", "onResume");
        getData();
        super.onResume();
    }

    private void initView() {
        SlipMainCenter.btnMsgtop.setVisibility(View.VISIBLE);
        SlipMainCenter.btnMsgtop.setBackgroundResource(R.drawable.privateletter_tabs);
        SlipMainCenter.btnMsgtop.setOnClickListener(click);
        SlipMainCenter.lyButtom.setVisibility(View.GONE);
        SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);

        xListView = (XListView) findViewById(R.id.sys_message_listview);
        xListView.setPullLoadEnable(true);
        xListView.setOnItemClickListener(itemClick);
        xListView.setXListViewListener(xListViewListener);
        xListView.setAdapter(fromListAdapter);
        getData();
    }

    private void getData() {
        final int myUserId = Integer.parseInt(Utility.mSession.getUserId());
        String url = new StringBuffer().append(YouLe.BASE_URL).append("private_messages").append("?access_token=")
                .append(Utility.mToken.getAccess_token())
                .append("&page=").append(mPage)
                .append("&size=10").toString();
        Log.i("1234", "url: " + url);
        AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                if (mIsRefresh) {
                    mFromList = new ArrayList<FromInfo>();
                }
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("messages");
                    if(jsonArray.length()<1){
                        xListView.stopLoadMore();
                        return;
                    }
                    String username1 = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj1 = jsonArray.getJSONObject(i);
                        JSONObject obj2 = obj1.getJSONObject("from_user");
                        String username = obj2.getString("username");
                        username1 = obj2.getString("username");
                        String avatar_url = obj2.getString("avatar_url");
                        int user_id = obj2.getInt("user_id");
                        String date = obj1.getString("created_date");
                        String lcontent = obj1.getString("content");
                        if (myUserId == user_id) {
                            JSONObject obj3 = obj1.getJSONObject("to_user");
                            user_id = obj3.getInt("user_id");
                            username = obj3.getString("username");
                            avatar_url= obj3.getString("avatar_url");
                        }
                        int flags = obj1.getInt("flags");
                        int unread = obj1.getInt("unread");
                        switch (flags) {
                            case 1:
                                lcontent = obj1.getString("content");
                                break;
                            case 2:
                                lcontent = obj1.getString("image_url");
                                break;
                            case 4:
                                lcontent = obj1.getString("audio_url");
                                break;
                        }
                        FromInfo fromInfo = new FromInfo(username, avatar_url, user_id + "", date, lcontent, username1, flags, unread);
                        mFromList.add(fromInfo);
                    }
                    if (mFromList.size() > 0) {
                        fromListAdapter.notifyDataSetChanged();
                        xListView.stopLoadMore();
                        xListView.stopRefresh();
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public class FromListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFromList.size();
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
                view = getLayoutInflater().inflate(R.layout.sys_msg_acc,
                        viewGroup, false);
                holder = new ViewHolder();
                holder.sender = (TextView) view.findViewById(R.id.acc_from);
                holder.sendDate = (TextView) view.findViewById(R.id.acc_date);
                holder.lastMsg = (TextView) view.findViewById(R.id.acc_last);
                holder.avatar = (ImageView) view.findViewById(R.id.acc_avatar);
                holder.weidu = (ImageView) view.findViewById(R.id.acc_weidu);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (mFromList != null && mFromList.size() > 0) {
                holder.sender.setText(mFromList.get(i).username);

                holder.sendDate.setText(YouLe.formatDate(Long.parseLong(mFromList.get(i).date)));

                fb.display(holder.avatar, mFromList.get(i).avUrl);
//                fb.display(holder.avatar,mFromList.get(i).avUrl);
                switch (mFromList.get(i).flags) {
                    case 0:
                        holder.lastMsg.setText(mFromList.get(i).lcontent);
                        break;
                    case 2:
                        holder.lastMsg.setText(getString(R.string.is_img));
                        break;
                    case 4:
                        holder.lastMsg.setText(getString(R.string.is_aud));
                        break;
                }
                if (mFromList.get(i).unread > 0) {
                    SystemMsgService.MSGbadge = 1;
                    holder.weidu.setVisibility(View.VISIBLE);
                }
            }
            return view;
        }

        private class ViewHolder {
            public TextView sender;
            public TextView sendDate;
            public TextView lastMsg;
            public ImageView avatar;
            public ImageView weidu;
        }
    }

    private class FromInfo {
        public String username, avUrl, userId, date, lcontent, otherName;
        public int flags, unread;

        public FromInfo(String username, String avUrl, String userId, String date, String lcontent, String otherName, int flags, int unread) {
            this.userId = userId;
            this.username = username;
            this.avUrl = avUrl;
            this.date = date;
            this.lcontent = lcontent;
            this.otherName = otherName;
            this.flags = flags;
            this.unread = unread;
        }
    }
}