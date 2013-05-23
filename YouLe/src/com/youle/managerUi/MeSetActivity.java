package com.youle.managerUi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youle.R;
import com.youle.util.GlobalData;
import com.youle.util.ImageUtil;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.PicPopu;

public class MeSetActivity extends Activity implements OnClickListener{
	private ImageView ivAva;
	private PicPopu pop;
	private ImageUtil imageUtil;
	private Bitmap photo;
	private boolean isActResult = false;
	File tempFile = OtherUtil.fileCreate(
			OtherUtil.getFileName()+".png",false);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meset_activity);
		initView();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (isActResult) {
			isActResult = false;
		}
	}
	private void initView()
	{
		imageUtil = new ImageUtil(MeSetActivity.this);
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView)findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.set);
		((LinearLayout)findViewById(R.id.meset_layoutAva)).setOnClickListener(this);
		ivAva = (ImageView)findViewById(R.id.meset_ava);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.twobtn_header_left:
			MeSetActivity.this.finish();
			break;

		case R.id.meset_layoutAva:
			pop = new PicPopu(MeSetActivity.this,
					avaOnClick);
			pop.showAtLocation(
				MeSetActivity.this.findViewById(R.id.meset),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		}
	}
	private OnClickListener avaOnClick = new OnClickListener() {

		public void onClick(View v) {
			pop.dismiss();
			switch (v.getId()) {
			case R.id.btn_first:
				try {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 指定调用相机拍照后照片的储存路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(tempFile));
					startActivityForResult(intent,
							GlobalData.AVA_REQUEST_TAKEPHOTO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("test", "camera e:" + e.toString());
				}
				break;
			case R.id.btn_second:
				getLocalImage(GlobalData.AVA_REQUEST_GALLERY);
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		isActResult = true;
		switch (requestCode) {
		case GlobalData.AVA_REQUEST_GALLERY:
			if (data != null) {
				readLocalImage(data.getData(), true);
			}
			break;
		case GlobalData.AVA_REQUEST_TAKEPHOTO:
			if (tempFile.exists()) {
				readLocalImage(Uri.fromFile(tempFile), false);
			}
			break;
		case GlobalData.REQ_CODE_PHOTO_CROP:
			if (data != null) {
				readCropImage(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}
	private void getLocalImage(int reqCode) {
		// 抓下异常是防止有的机器不支持ACTION_PICK或ACTION_GET_CONTENT的动作
		try {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, reqCode);
		} catch (Exception e1) {
//			Log.i("test", "机器不支持ACTION_PICK " + e1.toString());
			try {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, reqCode);
			} catch (Exception e2) {
//				Log.i("test", "机器不支持ACTION_GET_CONTENT " + e2.toString());
			}
		}
	}
	
	/**
	 * 此处写方法描述
	 * 
	 * @Title: readLocalImage
	 * @param data
	 * @return void
	 */
	private void readLocalImage(Uri uri, boolean isGallay) {
		if (uri == null) {
			return;
		}
		if (uri != null) {
//			Log.i("test", "uri.toString()----"+uri.toString());
			if (isGallay)
				startPhotoCrop(uri, null, GlobalData.REQ_CODE_PHOTO_CROP); // 图片裁剪
			else
				startPhotoCrop(uri, tempFile.getAbsolutePath(),
						GlobalData.REQ_CODE_PHOTO_CROP); // 照照片裁剪
		}
	}

	/**
	 * 开始裁剪
	 * 
	 * @Title: startPhotoCrop
	 * @param uri
	 * @param duplicatePath
	 * @param reqCode
	 * @return void
	 */
	private void startPhotoCrop(Uri uri, String duplicatePath, int reqCode) {
		if(null != uri && !OtherUtil.isNullOrEmpty(uri.toString()))
		{
			if (imageUtil == null)
				imageUtil = new ImageUtil(MeSetActivity.this);
//		Log.i("test", "dfa--------------"+uri.toString());
		Uri duplicateUri = imageUtil.preCrop(uri, duplicatePath);
//		Log.i("test", "duplicateUri--------------"+duplicateUri.toString());
		Intent intent = new Intent(GlobalData.ACTION_CROP_IMAGE);
		intent.putExtra(GlobalData.IMAGE_URI, uri);
		startActivityForResult(intent, reqCode);
		}
	}
	/**
	 * @Title: readCropImage
	 * @param data
	 * @return void
	 */
	@SuppressWarnings("deprecation")
	private void readCropImage(Intent data) {
		if (data == null) {
			return;
		}
		Uri uri = data.getParcelableExtra(GlobalData.CROP_IMAGE_URI);
//		Log.i("test", "uri=========================>" + uri.toString());
		if (OtherUtil.is3gWifi(MeSetActivity.this)) {
			photo = imageUtil.getBitmap(uri);
			ivAva.setImageBitmap(photo);
//			new uploadTask().execute();
		} else {
			ToastUtil.show(MeSetActivity.this,
					getString(R.string.please_check_net));
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(photo != null && !photo.isRecycled())
		{
			photo.recycle();
			photo = null;
			System.gc();
		}
	}
	
}