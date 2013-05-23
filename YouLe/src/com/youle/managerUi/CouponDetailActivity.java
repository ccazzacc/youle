package com.youle.managerUi;

import net.tsz.afinal.FinalBitmap;

import com.google.zxing.WriterException;
import com.youle.R;
import com.zxing.encoding.EncodingHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CouponDetailActivity extends Activity{
	private FinalBitmap fb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_detail_activity);
		ImageView ivShopAva = (ImageView)findViewById(R.id.detail_iv_shopAva);
		fb = FinalBitmap.create(this);
		fb.display(ivShopAva, "http://api.pathtrip.com/avatars/256/1000033.png");
		String contentString = "100";
		ImageView ivCode = (ImageView)findViewById(R.id.detail_iv_ma);
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
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
		if (!contentString.equals("")) {
			
			Bitmap qrCodeBitmap;
			try {
				qrCodeBitmap = EncodingHandler.createQRCode(contentString, 250);
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
