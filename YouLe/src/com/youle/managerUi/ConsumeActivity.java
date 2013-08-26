package com.youle.managerUi;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.info.ConsumeInfo;
import com.youle.managerData.info.ConsumeListInfo;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;

public class ConsumeActivity extends StatActivity {
	private Button btnHistory;
	private ListView lvHistory;
	private TextView tvToday, tvMonth;
	private ConsumeListInfo listInfo;
	private List<ConsumeInfo> listCon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consume_activity);
		initView();
		MyApplication.getInstance().addActivity(this);
	}

	private void initView() {
		btnHistory = (Button) findViewById(R.id.consume_btnHistory);
		lvHistory = (ListView) findViewById(R.id.consume_lv);
		tvToday = (TextView) findViewById(R.id.consume_tv_day);
		tvMonth = (TextView) findViewById(R.id.consume_tv_month);
		if(!OtherUtil.is3gWifi(this))
        	ToastUtil.show(this, R.string.net_no);
        else
        	new ConsumeTask().execute();
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConsumeActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.consume);

	}

	private class ConsumeAdapter extends BaseAdapter {
		private Context context;
		private List<ConsumeInfo> list;
		private int year, month;

		public ConsumeAdapter(Context context, List<ConsumeInfo> list) {
			this.context = context;
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			if(null != list && list.size() > month)
				this.list = list.subList(0, month);
			else
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
			ConsumeInfo info = list.get(position);
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

			String tYear = getResources().getString(R.string.year);
			vc.tvMonth.setText(String.format(tYear, year, info.getMonth()));
			vc.tvTotal.setText(info.getTotal());
			return convertView;
		}

		private class ViewCache {
			TextView tvMonth;
			TextView tvTotal;
		}
	}

	private class ConsumeTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(ConsumeActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(ConsumeActivity.this);
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				tvToday.setText(listInfo.getToday());
				tvMonth.setText(listInfo.getMonth());
				listCon = listInfo.getList();
				if (null != listCon && listCon.size() > 0) {
					btnHistory.setVisibility(View.VISIBLE);
					btnHistory.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							btnHistory.setVisibility(View.GONE);
							ConsumeAdapter adapter = new ConsumeAdapter(
									ConsumeActivity.this, listCon);
							lvHistory.setAdapter(adapter);
						}
					});
				} else
					btnHistory.setVisibility(View.GONE);
			} else
				ToastUtil.showToast(ConsumeActivity.this, result);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(ConsumeActivity.this,
					getString(R.string.please_wait));
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getConsume(ConsumeActivity.this);
			if (!OtherUtil.isNullOrEmpty(res)
					&& res.startsWith(GlobalData.RESULT_OK)) {
				listInfo = YouLe.jsonConsume(res.substring(3));
				return GlobalData.RESULT_OK;
			} else
				return res;
		}

	}
}
