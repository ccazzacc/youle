package com.youle.managerUi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.koushikdutta.async.http.*;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.service.SystemMsgService;
import com.youle.util.OtherUtil;
import com.youle.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaofuchao on 13-7-19.
 */
public class SysMessageActivity extends Activity {
    private int mPage = 1;
    private boolean mIsRefresh;
    private XListView xListView;
    private MsgAdapter msgAdapter = new MsgAdapter();
    private List<MsgInfo> msgInfoList = new ArrayList<MsgInfo>();
    private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            OtherUtil.gotoAim(SysMessageActivity.this, msgInfoList.get(i - 1).type, msgInfoList.get(i - 1).relative_id);
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
                    SlipMainCenter.slidIntent(8, false);
                    break;
            }
        }
    };
    XListView.IXListViewListener xListViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            xListView.stopLoadMore();
            mIsRefresh=true;
            mPage=1;
            getMessage();
        }

        @Override
        public void onLoadMore() {
            xListView.stopRefresh();
            mPage++;
            mIsRefresh=false;
            getMessage();
            xListView.setSelection(msgInfoList.size()-1);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_message_activity);
        initView();
    }


    private void initView() {
        SlipMainCenter.btnMsgtop.setVisibility(View.VISIBLE);
        SlipMainCenter.btnMsgtop.setBackgroundResource(R.drawable.message_tabs);
        SlipMainCenter.btnMsgtop.setOnClickListener(click);
        SlipMainCenter.lyButtom.setVisibility(View.GONE);
        SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);

        xListView = (XListView) findViewById(R.id.sys_message_listview);
        xListView.setOnItemClickListener(itemClick);
        xListView.setPullLoadEnable(true);
        xListView.setXListViewListener(xListViewListener);
        xListView.setAdapter(msgAdapter);
        getMessage();
    }

    private void getMessage() {
        String url = new StringBuffer().append(YouLe.BASE_URL).append("system_messages").append("?access_token=")
                .append(Utility.mToken.getAccess_token())
                .append("&page=").append(mPage)
                .append("&size=10")
                .toString();
        Log.i("1234", "url: " + url);
        if(mIsRefresh){
            msgInfoList = new ArrayList<MsgInfo>();
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
                    JSONArray jsonArray = jsonObject.getJSONArray("messages");
                    if(jsonArray.length()<1){
                        xListView.stopLoadMore();
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String name = new SharedPref(SysMessageActivity.this).getRadioName();
                        String content = obj.getString("content");
                        String date = YouLe.formatDate(obj.getLong("created"));
                        int type = obj.getInt("type");
                        int msg_id = obj.getInt("message_id");
                        int relative_id = obj.getInt("relative_id");
                        MsgInfo info = new MsgInfo(name, content, date, type, msg_id, relative_id);
                        msgInfoList.add(info);

                        if (type == 4) {
                            Utility.mSession.storeType(relative_id);
                        }
                    }
                    if (msgInfoList.size() > 0) {
                        msgAdapter.notifyDataSetChanged();
                        xListView.stopLoadMore();
                        xListView.stopRefresh();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
        final String clearUrl = new StringBuffer().append(YouLe.BASE_URL).append("system_messages").toString();
        AsyncHttpPost post = new AsyncHttpPost(clearUrl);
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("access_token", Utility.mToken.getAccess_token());
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().execute(post, new HttpConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, AsyncHttpResponse asyncHttpResponse) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public class MsgAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return msgInfoList.size();
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
                view = getLayoutInflater().inflate(R.layout.sys_msg_item,
                        viewGroup, false);
                holder = new ViewHolder();
                holder.tit = (TextView) view.findViewById(R.id.text_msg_tit);
                holder.date = (TextView) view.findViewById(R.id.text_msg_date);
                holder.data = (TextView) view.findViewById(R.id.text_msg_data);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.tit.setText(msgInfoList.get(i).tit);
            holder.data.setText(msgInfoList.get(i).data);
            holder.date.setText(msgInfoList.get(i).date);
            SystemMsgService.MSGbadge=0;
            return view;
        }

        private class ViewHolder {
            public TextView tit;
            public TextView data;
            public TextView date;
        }
    }

    public class MsgInfo {
        int msg_id, type, relative_id;
        private String tit, data, date;

        public MsgInfo(String tit, String data, String date, int type, int msg_id, int relative_id) {
            this.tit = tit;
            this.data = data;
            this.date = date;
            this.type = type;
            this.msg_id = msg_id;
            this.relative_id = relative_id;
        }
    }
}