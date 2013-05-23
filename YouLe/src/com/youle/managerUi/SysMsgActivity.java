package com.youle.managerUi;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.youle.R;
import com.youle.managerData.database_helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaofuchao on 13-5-20.
 */
public class SysMsgActivity extends Activity {
    private DatabaseHelper db;
    private ListView mLst;
    private Cursor mCursor;
    private MyAdapter mMyAdapter;
    public List<MsgInfo> msgInfoList=new ArrayList<MsgInfo>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_msg_activity);

        getData();
        initView();
    }

    private void getData() {
        db = new DatabaseHelper(SysMsgActivity.this);
        db.open();
        //db.insertData(1, "恭喜", "你发的路况被用了，发你一张优惠券！快谢恩吧2！你发的路况被用了，发你一张优惠券！快谢恩吧2", "YouLe", "2013-5-20");
        mCursor = db.fetchAllData();
        Log.i("1234","getCount "+mCursor.getCount());
        mCursor.moveToFirst();
        while(mCursor.moveToNext()){
            MsgInfo info=new MsgInfo(mCursor.getString(mCursor.getColumnIndex("title")),mCursor.getString(mCursor.getColumnIndex("data")),mCursor.getString(mCursor.getColumnIndex("date")));
            msgInfoList.add(info);
        }
        mCursor.close();
//        db.close();
    }

    private void initView() {
        mLst = (ListView) findViewById(R.id.listView_msg);
        mLst.setDividerHeight(0);
        mLst.setCacheColorHint(0);
        mMyAdapter = new MyAdapter();
        mLst.setAdapter(mMyAdapter);

    }
    public class MsgInfo{
        private String tit,data,date;
        public MsgInfo(String tit,String data,String date){
            this.tit=tit;
            this.data=data;
            this.date=date;
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