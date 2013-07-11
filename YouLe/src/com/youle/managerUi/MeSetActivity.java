package com.youle.managerUi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalBitmap;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.info.MeInfo;
import com.youle.util.GlobalData;
import com.youle.util.ImageUtil;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PicPopu;

public class MeSetActivity extends StatActivity implements OnClickListener{
	private ImageView ivAva;
	private EditText etName,etAge;
	private PicPopu pop;
	private ImageUtil imageUtil;
	private Bitmap photo;
	private Button btnSex;
	private boolean isActResult = false,isMan = true;
	private File tempFile = OtherUtil.fileCreate(
			OtherUtil.getFileName()+".png",false);
	private FinalBitmap fb;
	private int i = 0;
	
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
		fb = FinalBitmap.create(this);
		fb.onResume();
		imageUtil = new ImageUtil(MeSetActivity.this);
		Button btnBack = (Button)findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView)findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.me_info);
		((Button)findViewById(R.id.meset_btn)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.meset_layoutAva)).setOnClickListener(this);
		ivAva = (ImageView)findViewById(R.id.meset_ava);
		btnSex = (Button)findViewById(R.id.meset_btn_sex);
		btnSex.setOnClickListener(this);
		etName = (EditText)findViewById(R.id.meset_et_name);
		etAge = (EditText)findViewById(R.id.meset_et_age);
		etAge.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) {
					i++;
					if(i%2==1)
						new SetMeTask().execute();
				}
				return false;
			}
		});
		new GetMeTask().execute();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.twobtn_header_left:
			MeSetActivity.this.finish();
			break;

		case R.id.meset_layoutAva:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
			.hideSoftInputFromWindow(MeSetActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			pop = new PicPopu(MeSetActivity.this,
					avaOnClick);
			pop.showAtLocation(
				MeSetActivity.this.findViewById(R.id.meset),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.meset_btn_sex:
			if(isMan)
			{
				isMan = false;
				btnSex.setBackgroundResource(R.drawable.woman);
			}else
			{
				isMan = true;
				btnSex.setBackgroundResource(R.drawable.man);
			}
			break;
		case R.id.meset_btn:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
			.hideSoftInputFromWindow(MeSetActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			new SetMeTask().execute();
			break;
		}
	}
	private class GetMeTask extends AsyncTask<Void, Void, String>
	{
		private MeInfo info;
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(MeSetActivity.this);
			if(!OtherUtil.isNullOrEmpty(result) && result.startsWith(GlobalData.RESULT_OK)&& null != info)
			{
				etName.setText(info.getName());
				fb.display(ivAva, info.getAvaUrl());
				if(info.getGender()==0)
				{
					isMan = false;
					btnSex.setBackgroundResource(R.drawable.woman);
				}else
				{
					isMan = true;
					btnSex.setBackgroundResource(R.drawable.man);
				}
				etAge.setText(info.getAge());
			}else
			{
				ToastUtil.showToast(MeSetActivity.this, result);
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(MeSetActivity.this);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getMeInfo(MeSetActivity.this);
			if(res.startsWith(GlobalData.RESULT_OK))
			{
				info = YouLe.jsonUserInfo(res.substring(3),true);
				return GlobalData.RESULT_OK;
			}else
				return res;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(MeSetActivity.this, getString(R.string.please_wait));
		}
		
	}
	private class SetMeTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(MeSetActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(MeSetActivity.this);
			if(!OtherUtil.isNullOrEmpty(result) && result.startsWith(GlobalData.RESULT_OK))
			{
				ToastUtil.show(MeSetActivity.this,
						getString(R.string.fix_ok));
				MeSetActivity.this.finish();
			}else
			{
				ToastUtil.show(MeSetActivity.this,
						result);
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String gender;
			if(isMan)
				gender = "1";
			else
				gender = "0";
			return YouLe.setMe(MeSetActivity.this, etName.getText().toString(), gender, etAge.getText().toString());
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(MeSetActivity.this, getString(R.string.please_wait));
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
//			ivAva.setImageBitmap(photo);
			new uploadTask().execute();
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
	private class uploadTask extends AsyncTask<Void, Void, String>
	{
		private Drawable drawable;
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!OtherUtil.isNullOrEmpty(result) && drawable != null)
				ivAva.setImageDrawable(drawable);
			else{
				ToastUtil.showToast(MeSetActivity.this, result);
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String url = null;
			File file = OtherUtil.fileCreate("ava.jpg", false);
			String filePath =file.getAbsolutePath();
			OtherUtil.saveFile(photo, file);
			if (!TextUtils.isEmpty(filePath)) {
				if (Utility.isSessionValid()) {
					url = YouLe.upAvatar(MeSetActivity.this, filePath);
				} else {
					if (Utility.hasToken()) {
						String result = YouLe.refreshToken(
								MeSetActivity.this,Utility.mToken.getRefresh_token());
						if (GlobalData.RESULT_OK.equals(result)) {
							url = YouLe.upAvatar(MeSetActivity.this,
									filePath);
						}
					}
				}
				if(photo != null && !photo.isRecycled())
				{
					photo.recycle();
					photo = null;
					System.gc();
				}
				// 下载头像
				if (!TextUtils.isEmpty(url)) {
					if(url.startsWith("http")|| url.startsWith(" http"))
					{
						try {
							drawable = Drawable.createFromStream(
									new URL(url).openStream(), "iamgeSync");
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				} 
			}
			return url;
		}
		
	}
}