package com.youle.managerUi;

import java.io.File;
import java.io.FileInputStream;

import net.tsz.afinal.bitmap.core.BitmapCommonUtils;
import net.tsz.afinal.bitmap.core.FileNameGenerator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.ImageZoomState;
import com.youle.view.ImageZoomView;
import com.youle.view.SimpleImageZoomListener;


public class ShowLargeActivity extends StatActivity {
	private Context context;
	private Bitmap bmp;
	private ImageZoomView zoomView;// 自定义的图片显示组件
	private ImageZoomState zoomState;// 图片缩放和移动状态类
	private SimpleImageZoomListener zoomListener;// 缩放事件监听器
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_large_img);
		context = this;
		Bundle bundle = this.getIntent().getExtras();
		String imageUrl = null;
		if (bundle != null) {
			imageUrl = bundle.getString(GlobalData.IMAGE_URI); // 图片
//			width = bundle.getInt(GlobalData.IMAGE_WIDTH);
//			height = bundle.getInt(GlobalData.IMAGE_HEIGHT);
		}
		zoomState = new ImageZoomState();
		zoomListener = new SimpleImageZoomListener();
		zoomListener.setZoomState(zoomState);
		zoomView = (ImageZoomView) findViewById(R.id.zoomView);
		zoomView.setImageZoomState(zoomState);
		zoomView.setOnTouchListener(zoomListener);
//		bmp = bu.reBitmap(imageUrl);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		File f = new File(BitmapCommonUtils.getDiskCacheDir(context,"afinalCache"),FileNameGenerator.generator(imageUrl)+".0");
		if(!f.exists())
		{
			f = new File(BitmapCommonUtils.getDiskCacheDir(context,"afinalCache/original"),FileNameGenerator.generator(imageUrl)+".0");
		}
		BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
		opts.inSampleSize = OtherUtil.computeSampleSize(opts, -1,
				1280*800);//3200 * 2400
		opts.inJustDecodeBounds = false;
		try {
			FileInputStream fis = new FileInputStream(f);
			bmp = BitmapFactory.decodeStream(fis, null, opts);
		}catch(Exception e){	
		}
		zoomView.setImage(bmp);
		zoomView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ShowLargeActivity.this.finish();
				overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
			}
		});
		((Button)findViewById(R.id.large_btn_download)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SavePic().execute();
			}
		});
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(null != bmp && !bmp.isRecycled())
		{
			bmp.recycle();
			bmp = null;
		}
		CustomProgressDialog.stopProgressDialog(ShowLargeActivity.this);
		System.gc();
	}
	private class SavePic extends AsyncTask<Void, Void, Void>
	{
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			{
				MediaStore.Images.Media.insertImage(getContentResolver(), bmp, System.currentTimeMillis()+"", "");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory()))); 
			CustomProgressDialog.stopProgressDialog(ShowLargeActivity.this);
			ToastUtil.show(ShowLargeActivity.this, "图片保存到 \"sdcard/DCIM/Camera/\"");
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(context, getString(R.string.please_wait));
			context = null;
		}
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			ShowLargeActivity.this.finish();
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		}
		return super.onKeyDown(keyCode, event);
	}
}
