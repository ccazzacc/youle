package com.youle.managerUi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.managerData.info.ConsumeInfo;

public class ConsumeActivity extends StatActivity{
	private Button btnHistory;
	private ListView lvHistory;
	private List<ConsumeInfo> listCon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consume_activity);
		initView();
		
	}
	private void initView()
	{
		btnHistory = (Button)findViewById(R.id.consume_btnHistory);
		lvHistory = (ListView)findViewById(R.id.consume_lv);
		btnHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnHistory.setVisibility(View.GONE);
				listCon = new ArrayList<ConsumeInfo>();
				listCon.add(new ConsumeInfo("2013年2月", "56"));
				listCon.add(new ConsumeInfo("2013年3月", "87"));
				listCon.add(new ConsumeInfo("2013年4月", "99"));
				ConsumeAdapter adapter = new ConsumeAdapter(ConsumeActivity.this, listCon);
				lvHistory.setAdapter(adapter);
			}
		});
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConsumeActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView)findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.consume);
		
	}
	private class ConsumeAdapter extends BaseAdapter {
		private Context context;
		private List<ConsumeInfo> list;

		public ConsumeAdapter(Context context, List<ConsumeInfo> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewCache vc;
			if (convertView == null) {
				convertView = (LinearLayout) View.inflate(context,
						R.layout.consume_list_item, null);
				vc = new ViewCache();
				vc.tvMonth = (TextView) convertView
						.findViewById(R.id.consume_item_month);
				vc.tvTotal = (TextView) convertView
						.findViewById(R.id.consume_item_total);
				convertView.setTag(vc);
			} else {
				vc = (ViewCache) convertView.getTag();
			}
			ConsumeInfo info = list.get(position);
			vc.tvMonth.setText(info.getMonth());
			vc.tvTotal.setText(info.getTotal());
			
			return convertView;
		}

		private class ViewCache {
			TextView tvMonth;
			TextView tvTotal;
		}
	}
}
