package com.youle.managerUi;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.youle.R;
import com.youle.managerData.database_helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaofuchao on 13-5-20.
 */
public class SysMsgActivity extends Activity {
    private MyAdapter mMyAdapter;
    public List<MsgInfo> msgInfoList=new ArrayList<MsgInfo>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_msg_activity);

        getData();
        initView();
    }

    private void getData() {
        DatabaseHelper db = new DatabaseHelper(SysMsgActivity.this);
        db.open();
        db.insertData(1, "恭喜", "采用了你的路况信息", "YouLe", "2013-5-20",1);
        Cursor cursor = db.fetchAllData();
        Log.i("1234", "getCount " + cursor.getCount());
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            MsgInfo info=new MsgInfo(cursor.getString(cursor.getColumnIndex("title")),cursor.getString(cursor.getColumnIndex("data")),cursor.getString(cursor.getColumnIndex("date")),0);
            msgInfoList.add(info);
        }
        cursor.close();
        db.close();
    }

    private void initView() {

        ListView listView = (ListView) findViewById(R.id.listView_msg);
        listView.setDividerHeight(0);
        listView.setCacheColorHint(0);
        mMyAdapter = new MyAdapter();
        listView.setAdapter(mMyAdapter);
        listView.setOnItemClickListener(itemClick);
        ((TextView) findViewById(R.id.twobtn_header_tv)).setText(R.string.message);
        Button buttonBack=(Button)findViewById(R.id.twobtn_header_left);
        buttonBack.setVisibility(View.VISIBLE);
        buttonBack.setBackgroundResource(R.drawable.bar_icon_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    AdapterView.OnItemClickListener itemClick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("1234",msgInfoList.get(i).data);

        }
    };
    public class MsgInfo{
        private String tit,data,date;
        private int type;
        public MsgInfo(String tit,String data,String date,int type){
            this.tit=tit;
            this.data=data;
            this.date=date;
            this.type=type;
        }
    }
    public class MyAdapter extends BaseAdapter {


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
            return view;
        }

        private class ViewHolder {
            public TextView tit;
            public TextView data;
            public TextView date;
        }
    }
}