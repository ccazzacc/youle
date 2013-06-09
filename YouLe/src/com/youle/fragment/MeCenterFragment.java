package com.youle.fragment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapCommonUtils;
import net.tsz.afinal.bitmap.core.FileNameGenerator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youle.R;
import com.youle.http_helper.AsyncLoader;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.info.MainInfo;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.MeSetActivity;
import com.youle.managerUi.ReleaseActivity;
import com.youle.managerUi.SlidingMeActivity;
import com.youle.util.GlobalData;
import com.youle.util.ImageUtil;
import com.youle.util.MD5;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.PicPopu;
import com.youle.view.XListView;
import com.youle.view.XListView.IXListViewListener;

public class MeCenterFragment extends Fragment implements IXListViewListener,OnClickListener{
	private XListView msgLv;
	private Context ct;
	private List<MainInfo> list;
	private FinalBitmap fb;
	private View hView;
//	
	private Button btn_left, btn_quan,btn_record,btn_set;
	private TextView tv_uName;
	private ImageView ivMava, ivCover;
	private Handler mHandler;
	private MediaPlayer mediaPlay;
	private PicPopu pop;
	private File tempFile = OtherUtil.fileCreate(
			OtherUtil.getFileName()+".jpg",false);
	private ImageUtil imageUtil;
	private Bitmap photo;
//	private boolean isActResult = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
//		Log.i("test", "Center onCreateView");
		ct = (SlidingMeActivity) getActivity();
		fb = FinalBitmap.create(ct);
		View v = inflater
				.inflate(R.layout.me_center_layout, container, false);
		
		btn_left = (Button) v.findViewById(R.id.me_center_tab_menu);
		msgLv = (XListView) v.findViewById(R.id.me_center_listview);
		hView = LayoutInflater.from(ct).inflate(
				R.layout.msg_item_header_listview, null);
		ivMava = (ImageView) hView.findViewById(R.id.msg_iv_Head_ava);
		ivCover = (ImageView) hView.findViewById(R.id.msg_header_ivPhoto);
		btn_quan = (Button) v.findViewById(R.id.me_center_tab_quan);
		btn_record = (Button)v.findViewById(R.id.me_center_tab_record);
		btn_set = (Button)v.findViewById(R.id.twobtn_header_right);
		tv_uName = (TextView)v.findViewById(R.id.twobtn_header_tv);
		tv_uName.setText("wangning");
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		Log.i("test", "Center onActivityCreated");
		imageUtil = new ImageUtil(ct);
		btn_set.setBackgroundResource(R.drawable.bar_icon_setting);
		btn_set.setVisibility(View.VISIBLE);
		btn_set.setOnClickListener(this);
		mHandler = new Handler();
		msgLv.setPullLoadEnable(true);
		btn_left.setOnClickListener(this);
		btn_quan.setOnClickListener(this);
		btn_record.setOnClickListener(this);
		list = new ArrayList<MainInfo>();
//		list.add(new MainInfo(30.64626, 101.76322,
//				"http://api.pathtrip.com/avatars/128/1000101.png", "",
//				"http://api.pathtrip.com/covers/10004891367198444.jpg", "一品天下",
//				"18:10", 4,""));
//		list.add(new MainInfo(30.69626, 101.86322,
//				"http://api.pathtrip.com/avatars/128/1000033.png",
//				"点发斯蒂芬地方风……格的个人hfgd速度儿童故事！阿尔阿文温柔妻儿啊如同如同！！", "", "天府大道", "17:23",0,""));
		fb.onResume();
		fb.display(ivMava, "http://api.pathtrip.com/avatars/128/1000108.png");
		ivMava.setOnClickListener(this);
		ivCover.setOnClickListener(this);
		ivCover.setBackgroundResource(R.drawable.photo);
		
		msgLv.addHeaderView(hView);
		MsgAdapter adapter = new MsgAdapter(ct, list);
		msgLv.setAdapter(adapter);
	}

	private class MsgAdapter extends BaseAdapter {
		private Context context;
		private List<MainInfo> list;
		private AsyncLoader asyncLoader;
		private AnimationDrawable animationDrawable;
		private List<Integer> numList = new ArrayList<Integer>();
		public MsgAdapter(Context context, List<MainInfo> list) {
			this.context = context;
			this.list = list;
			asyncLoader = new AsyncLoader();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewCache vc;
			if (convertView == null) {
				convertView = (LinearLayout) View.inflate(context,
						R.layout.msg_item_listview, null);
				vc = new ViewCache();
				vc.ivAva = (ImageView) convertView
						.findViewById(R.id.msg_iv_ava);
				vc.tvChat = (TextView) convertView
						.findViewById(R.id.msg_tv_chat);
				vc.ivLine = (ImageView) convertView
						.findViewById(R.id.msg_iv_line);
				vc.ivPhoto = (ImageView) convertView
						.findViewById(R.id.msg_iv_photo);
				vc.tvTime = (TextView) convertView
						.findViewById(R.id.msg_tv_time);
				vc.tvAdd = (TextView) convertView
						.findViewById(R.id.msg_tv_address);
				vc.ivStatus = (ImageView) convertView
						.findViewById(R.id.msg_iv_status);
				vc.ivPhotoTop = (ImageView) convertView
						.findViewById(R.id.msg_iv_photoTop);
				vc.sAnim = (ImageView)convertView.findViewById(R.id.msg_iv_sAnim);
				vc.sTime = (TextView)convertView.findViewById(R.id.msg_tv_sTime);
				vc.lSound = (LinearLayout)convertView.findViewById(R.id.msg_lout_sound);
				 vc.lyChat =
				 (LinearLayout)convertView.findViewById(R.id.msg_ltv_chat);
				convertView.setTag(vc);
			} else {
				vc = (ViewCache) convertView.getTag();
			}
			vc.ivLine.setBackgroundResource(R.drawable.line);
			final MainInfo info = list.get(position);
//			fb.display(vc.ivAva, info.getAvaUrl());
			
			if(!TextUtils.isEmpty(info.getAudUrl()))
			{	
				vc.lSound.setVisibility(View.VISIBLE);
				vc.lSound.setBackgroundResource(R.drawable.event_bg);
				vc.sAnim.setBackgroundResource(R.drawable.sound0);
				final String path = asyncLoader.loadMedia(info.getAudUrl(),
						new AsyncLoader.ImageCallback() {
							@Override
							public String mediaLoaded(String mediaUrl) {
								// TODO Auto-generated method stub
								return mediaUrl;
							}
						});
				vc.lSound.setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(final View v) {
								// TODO Auto-generated method stub
								numList.add(position);
								int x = 1;
								if (null != numList && numList.size() >= 2) {
									for (int i = numList.size() - 1, j = 0; i >= j
											&& i > 2; i--) {
										if (numList.get(i) == numList
												.get(i - 1)) {
											x++;
										}
									}
								}
								if (x % 2 == 0) {
									if ((mediaPlay != null && mediaPlay
											.isPlaying()) == true) {
										mediaPlay.stop();
									}
									mediaPlay = null;
									return;
								}
								if ((mediaPlay != null && mediaPlay.isPlaying()) == true) {
									mediaPlay.stop();
									mediaPlay = null;
								}
								File sound;
								if (OtherUtil.isNullOrEmpty(path)) {
									String uri = info.getAudUrl();
									String name = MD5.getMD5(uri)
											+ uri.substring(uri
													.lastIndexOf("."));
									sound = OtherUtil.fileCreate(name, true);
								} else {
									sound = new File(path);
								}

								if (sound.exists()) {
									vc.sAnim.setBackgroundResource(R.anim.sound_anim);
									animationDrawable = (AnimationDrawable) vc.sAnim.getBackground();
							        animationDrawable.start();
									try {
										if (mediaPlay == null)
											mediaPlay = new MediaPlayer();
										mediaPlay.reset();
										mediaPlay.setDataSource(sound
												.getAbsolutePath());
										mediaPlay.prepare();
										mediaPlay.start();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalStateException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									new Thread() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											super.run();

											Message msg = new Message();
											msg.obj = vc.sAnim;
											try {
												while ((mediaPlay != null && mediaPlay
														.isPlaying()) == true) {
												}
											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											msg.what = 1;
											// Log.i("",
											// "stop--position:"+position);
											handler.sendMessage(msg);
										}

									}.start();
								} else {

								}
							}
						});
			} else {
				vc.lSound.setVisibility(View.GONE);
			}
			
				
			
			if (!TextUtils.isEmpty(info.getText())) {
				vc.lyChat.setVisibility(View.VISIBLE);
				vc.tvChat.setVisibility(View.VISIBLE);
				vc.tvChat.setText(info.getText());
				
				// vc.lyChat.setBackgroundResource(R.drawable.event_bg);
//				vc.ivPhoto.setVisibility(View.GONE);
//				vc.ivPhotoTop.setVisibility(View.GONE);
			}else
				vc.lyChat.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(info.getImgUrl())) {
				vc.ivPhoto.setVisibility(View.VISIBLE);
				LayoutParams para;
				para = (LayoutParams) vc.ivPhotoTop.getLayoutParams();

				para.height = 200;
				para.width = 270;
				vc.ivPhoto.setLayoutParams(para);
				// vc.lyChat.setBackgroundResource(R.drawable.event_bg);
				// vc.ivPhoto.setBackgroundResource(R.drawable.event_bg);
				fb.display(vc.ivPhoto, info.getImgUrl());
				vc.ivPhotoTop.setVisibility(View.VISIBLE);
				vc.tvChat.setVisibility(View.GONE);
			}else
			{
				vc.ivPhoto.setVisibility(View.GONE);
				vc.ivPhotoTop.setVisibility(View.GONE);
			}
			LayoutParams para;
			para = (LayoutParams) vc.ivPhotoTop.getLayoutParams();

			para.height = 200;
			para.width = 270;
			vc.ivPhotoTop.setLayoutParams(para);

			vc.tvTime.setText(info.getCreated());
			switch (info.getMark()) {
			case 1:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_trafficjam);
				break;
			case 2:
				vc.ivStatus.setBackgroundResource(R.drawable.event_status_road);
				break;
			case 3:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_danger);
				break;
			case 4:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_event);
				break;
			case 5:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_unimpeded);
				break;
			}
			vc.tvAdd.setText(info.getPlace());

			return convertView;
		}

		private class ViewCache {
			ImageView ivAva;
			TextView tvChat;
			ImageView ivLine;
			ImageView ivPhoto;
			ImageView ivPhotoTop;
			TextView tvTime;
			ImageView ivStatus;
			TextView tvAdd;
			 LinearLayout lyChat;
			ImageView sAnim;
			TextView sTime;
			LinearLayout lSound;
		}
		private Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 1 && msg.obj != null) {
					System.out.println("test.........close.........");
					if(animationDrawable.isRunning())
						animationDrawable.stop();
					((ImageView) msg.obj)
							.setImageResource(R.drawable.sound0);
				}
			}

		};
	}

	private void geneItems(String page, String size, String number) {

	}

	private void onLoad() {
		msgLv.stopRefresh();
		msgLv.stopLoadMore();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Log.i("Other", "onRefresh run");
				// geneItems("1", "10", "2");
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Log.i("Other", "onRefresh run");
				// geneItems("1", "10", "2");
			}
		}, 2000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.me_center_tab_menu:
			((SlidingMeActivity) ct).showLeft();
			break;

		case R.id.me_center_tab_quan:
			startActivity(new Intent((SlidingMeActivity) ct,
					CouponActivity.class));
			((SlidingMeActivity) ct).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.me_center_tab_record:
			startActivity(new Intent((SlidingMeActivity) ct,
					ReleaseActivity.class));
			break;
		case R.id.msg_iv_Head_ava:
			if(null == photo)
			{
				pop = new PicPopu((SlidingMeActivity) ct,
						avaOnClick);
				pop.showAtLocation(
						((SlidingMeActivity) ct).findViewById(R.id.me_center_layout),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;
		case R.id.msg_header_ivPhoto:
			if(null == photo)
			{
				pop = new PicPopu(ct, coverOnClick);
				pop.showAtLocation(
						((SlidingMeActivity) ct).findViewById(R.id.me_center_layout),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;
		case R.id.twobtn_header_right:
			startActivity(new Intent((SlidingMeActivity) ct,
					MeSetActivity.class));
			((SlidingMeActivity) ct).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		fb.onPause();
		if (mediaPlay != null && mediaPlay.isPlaying()) {
			mediaPlay.stop();
			mediaPlay.release();
		}
		mediaPlay = null;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fb.onResume();
	}
	private OnClickListener coverOnClick = new OnClickListener() {

		public void onClick(View v) {
			pop.dismiss();
			switch (v.getId()) {
			case R.id.btn_first:
				if (photo != null) {
					photo = null;
				}
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
				startActivityForResult(intent,
						GlobalData.PHOTO_REQUEST_TAKEPHOTO);
				break;
			case R.id.btn_second:
				if (photo != null) {
					photo = null;
				}
				Intent intents = new Intent(Intent.ACTION_PICK, null);
				intents.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intents,
						GlobalData.PHOTO_REQUEST_GALLERY);
				break;
			}
		}
	};
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
	private String myCoverPath;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		isActResult = true;
		switch (requestCode) {
		case GlobalData.PHOTO_REQUEST_TAKEPHOTO:
			if (tempFile != null && tempFile.exists()) {
				myCoverPath = tempFile.getAbsolutePath();
				try {
					ExifInterface exif = new ExifInterface(myCoverPath);
					if ((Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH))) < 640) {
						ToastUtil.showToast(ct,
								"图片小");
						myCoverPath = null;
					} else {
						if (OtherUtil.is3gWifi(ct)) {
							new UpCoverTask()
									.execute(tempFile.getAbsolutePath());
						} else {
							ToastUtil.showToast(ct,
									getString(R.string.please_check_net));
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;

		case GlobalData.PHOTO_REQUEST_GALLERY:
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					try {
						ContentResolver resolver = ct.getContentResolver();
						Cursor cursor = resolver.query(
								uri, null, null, null,null);
						if(cursor != null){
							int colunm_index = cursor
									.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
							cursor.moveToFirst();
							String path = cursor.getString(colunm_index);
							cursor.close();
//							Log.i("test", "GALLERY path:" + path);
							if ( !OtherUtil.isNullOrEmpty(path) && (path.endsWith("jpg") || path.endsWith("png"))) {
								myCoverPath = path;
								ExifInterface exif = new ExifInterface(path);
								if ((Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH))) < 640
										&& (Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH))) < 640) {
									ToastUtil.showToast(
											ct,
											"图片小");
									myCoverPath = null;
								} else {
									if (OtherUtil.is3gWifi(ct)) {
										new UpCoverTask().execute(path);
									} else {
										ToastUtil
												.showToast(
														ct,
														getString(R.string.please_check_net));
									}
								}

							}
						}

					} catch (Exception e) {

					}
				}
			}
			break;
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
				imageUtil = new ImageUtil(ct);
		Uri duplicateUri = imageUtil.preCrop(uri, duplicatePath);
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
		if (OtherUtil.is3gWifi(ct)) {
			photo = imageUtil.getBitmap(uri);
//			ivMava.setImageBitmap(photo);
			new uploadTask().execute();
		} else {
			ToastUtil.show(ct,
					getString(R.string.please_check_net));
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(photo != null && !photo.isRecycled())
		{
			photo.recycle();
			photo = null;
			System.gc();
		}
	}
	private class UpCoverTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!TextUtils.isEmpty(result) && result.startsWith("http")) {
				// result = result.substring(0, result.lastIndexOf("?"));
				fb.display(ivCover, result);
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "";
			if (!TextUtils.isEmpty(params[0])) {
				if (Utility.isSessionValid()) {
					url = YouLe.upCover(ct, params[0]);
				} else {
					if (Utility.hasToken()) {
						String result = YouLe.refreshToken(ct,
								Utility.mToken.getRefresh_token());
						if (GlobalData.RESULT_OK.equals(result)) {
							url = YouLe.upCover(ct, params[0]);
						}
					}
				}
			}
			if (!OtherUtil.isNullOrEmpty(url) && url.startsWith("http")) {
				int i = url.lastIndexOf("?");
				if (i > 0 && i < url.length()) {
					url = url.substring(0, i);
				}
				fb.clearCache(url);
				File f = new File(BitmapCommonUtils.getDiskCacheDir(
						ct, "afinalCache"),
						FileNameGenerator.generator(url) + ".0");
				if (f.exists())
					f.delete();
				File ff = new File(BitmapCommonUtils.getDiskCacheDir(
						ct, "afinalCache/original"),
						FileNameGenerator.generator(url) + ".0");
				if (ff.exists())
					ff.delete();
				fb.flushCache();
			}
			return url;
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
				ivMava.setImageDrawable(drawable);
			else{
				ToastUtil.showToast(ct, result);
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
					url = YouLe.upAvatar(ct, filePath);
				} else {
					if (Utility.hasToken()) {
						String result = YouLe.refreshToken(
								ct,Utility.mToken.getRefresh_token());
						if (GlobalData.RESULT_OK.equals(result)) {
							url = YouLe.upAvatar(ct,
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
