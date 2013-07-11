package com.youle.managerUi;

import net.tsz.afinal.FinalBitmap;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatActivity;
import com.google.zxing.WriterException;
import com.youle.R;
import com.youle.managerData.info.CouponListInfo;
import com.youle.util.GlobalData;
import com.zxing.encoding.EncodingHandler;

public class CouponDetailActivity extends StatActivity{
	private FinalBitmap fb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_detail_activity);
		initView();
	}
	private void initView()
	{
		CouponListInfo info = (CouponListInfo) this.getIntent().getSerializableExtra(GlobalData.COUPON);
		ImageView ivShopAva = (ImageView)findViewById(R.id.detail_iv_photos);
		fb = FinalBitmap.create(this);
		fb.display(ivShopAva, info.getImgUrl());
		ImageView ivCode = (ImageView)findViewById(R.id.detail_iv_ma);
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CouponDetailActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView)findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.coupon_detail);
		((TextView)findViewById(R.id.detail_tv_sName)).setText(info.getMerName());
		String exTime = getResources().getString(R.string.expire_at);
		((TextView)findViewById(R.id.detail_tv_outDate)).setText(String.format(exTime, info.getExpireAt()));
		((TextView)findViewById(R.id.detail_tv_addre)).setText(info.getMerAddress());
		((TextView)findViewById(R.id.detail_tv_info)).setText(info.getCouponName());
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
			Toast.makeText(CouponDetailActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
		}
	}
}
