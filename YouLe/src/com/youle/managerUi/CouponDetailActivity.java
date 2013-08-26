package com.youle.managerUi;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.google.zxing.WriterException;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.info.CarTopicInfo;
import com.youle.managerData.info.CouponListInfo;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.zxing.encoding.EncodingHandler;

public class CouponDetailActivity extends StatActivity{
	private FinalBitmap fb;
	private String ucId;
	private TextView tvMerName,tvToTime,tvAddress,tvInfo;
	private ImageView ivShopAva, ivCode;
	private Gallery gallery;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_detail_activity);
		initView();
		MyApplication.getInstance().addActivity(this);
	}
	private void initView()
	{
		ivShopAva = (ImageView)findViewById(R.id.detail_iv_photos);
		fb = FinalBitmap.create(this);
		fb.onResume();
		ivCode = (ImageView)findViewById(R.id.detail_iv_ma);
		gallery = (Gallery)findViewById(R.id.coupon_gallery);
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != ucId && !OtherUtil.isNullOrEmpty(ucId))
				{
					Intent intent = new Intent(CouponDetailActivity.this,SlidActivity.class);
					intent.putExtra("flag", 3);
					startActivity(intent);
				}
				CouponDetailActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView)findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.coupon_detail);
		tvMerName = (TextView)findViewById(R.id.detail_tv_sName);
		tvToTime = (TextView)findViewById(R.id.detail_tv_outDate);
		tvAddress = (TextView)findViewById(R.id.detail_tv_addre);
		tvInfo = (TextView)findViewById(R.id.detail_tv_info);
		ucId = this.getIntent().getStringExtra("uc_id");
		if(!OtherUtil.is3gWifi(this))
        	ToastUtil.show(this, R.string.net_no);
        else if(null != ucId && !OtherUtil.isNullOrEmpty(ucId))
		{
			new TheCouTask().execute();
		}else
		{
			List<CouponListInfo> listCou = (List<CouponListInfo>) this.getIntent().getSerializableExtra(GlobalData.COUPON);
			if(null != listCou && listCou.size() > 0)
			{
				int i = this.getIntent().getIntExtra("pos", 0);
				PageAdapter pageAdapter=new PageAdapter(CouponDetailActivity.this, listCou);
				gallery.setAdapter(pageAdapter);
				gallery.setSelection(i);
			}
		}
		
	}
	private void showInfo(CouponListInfo info)
	{
		if(null != info)
		{
			fb.display(ivShopAva, info.getImgUrl());
			tvMerName.setText(info.getMerName());
			String exTime = getResources().getString(R.string.expire_at);
			tvToTime.setText(String.format(exTime, info.getExpireAt()));
			tvAddress.setText(info.getMerAddress());
			tvInfo.setText(info.getCouponName());
			if (!info.getQrCode().equals("")) {
				Bitmap qrCodeBitmap;
				try { 
					qrCodeBitmap = EncodingHandler.createQRCode(info.getQrCode(), 400);
					ivCode.setImageBitmap(qrCodeBitmap);
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				ToastUtil.show(CouponDetailActivity.this, "Text can not be empty");
			}
		}
	}
	private class TheCouTask extends AsyncTask<Void, Void, String>
	{
		CouponListInfo infos;
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result = YouLe.getTheCoupon(CouponDetailActivity.this, ucId);
			if(!OtherUtil.isNullOrEmpty(result) && result.startsWith(GlobalData.RESULT_OK))
			{
				infos = YouLe.jsonTheCoupon(CouponDetailActivity.this, result.substring(3));
				return GlobalData.RESULT_OK;
			}
			return result;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!OtherUtil.isNullOrEmpty(result) && result.startsWith(GlobalData.RESULT_OK))
			{
				if(null != infos)
					showInfo(infos);
			}else
				ToastUtil.showToast(CouponDetailActivity.this, result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(null != ucId && !OtherUtil.isNullOrEmpty(ucId))
			{
				Intent intent = new Intent(CouponDetailActivity.this,SlidActivity.class);
				intent.putExtra("flag", 3);
				startActivity(intent);
			}
			CouponDetailActivity.this.finish();
		}
		return true;
	}
	private class PageAdapter extends BaseAdapter{
		private Context context;
		private List<CouponListInfo> listCou;
		public PageAdapter(Context context,List<CouponListInfo> listCou) { 
			this.context = context;
			this.listCou = listCou;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listCou.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listCou.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewInit init;
			if(convertView == null)
			{
				convertView = (LinearLayout)View.inflate(context, R.layout.coupon_detail_item, null);
				init = new ViewInit();
				init.ivShopAva = (ImageView)convertView.findViewById(R.id.detail_iv_photos);
				init.tvMerName = (TextView)convertView.findViewById(R.id.detail_tv_sName);
				init.tvToTime = (TextView)convertView.findViewById(R.id.detail_tv_outDate);
				init.tvAddress = (TextView)convertView.findViewById(R.id.detail_tv_addre);
				init.tvInfo = (TextView)convertView.findViewById(R.id.detail_tv_info);
				init.ivCode = (ImageView)convertView.findViewById(R.id.detail_iv_ma);
				convertView.setTag(init);
			}else
				init = (ViewInit)convertView.getTag();
			final CouponListInfo info = listCou.get(position);
			fb.display(init.ivShopAva, info.getImgUrl());
			init.tvMerName.setText(info.getMerName());
			init.tvAddress.setText(info.getMerAddress());
			init.tvInfo.setText(info.getCouponName());
			String exTime = getResources().getString(R.string.expire_at);
			init.tvToTime.setText(String.format(exTime, info.getExpireAt()));
			if (!info.getQrCode().equals("")) {
				Bitmap qrCodeBitmap;
				try {
					qrCodeBitmap = EncodingHandler.createQRCode(info.getQrCode(), 400);
					init.ivCode.setImageBitmap(qrCodeBitmap);
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				ToastUtil.show(CouponDetailActivity.this, "Text can not be empty");
			}
			
			return convertView;
		}
		private class ViewInit{
			TextView tvMerName,tvToTime,tvAddress,tvInfo;
			ImageView ivShopAva, ivCode;
		}
	}
}
