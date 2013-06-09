package com.youle.managerUi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youle.R;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.PicPopu;

public class AddPostActivity extends Activity implements OnClickListener,
		OnLongClickListener, OnTouchListener {
	private boolean isPub = false, isRecording, isClick;
	private ImageView ivPic, ivDel, mImageVol, ivSoundDel;
	private PicPopu pop;
	private Bitmap photo;
	private File tempFile = OtherUtil.fileCreate("comment.jpg", false);
	private CheckBox cbText;
	private RelativeLayout rlSound;
	private TextView tvSound;
	private Button btnRecord;
	private File fSound;
	private MediaRecorder mr;
	private AudioManager audioService;
	private int audioVolume, mIntentType, mEvenType;
	private String mIntentString;
	private long mRecordTime, mRecordTimeEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpost_activity);
		initView();
	}

	private void initView() {
		String status = this.getIntent().getStringExtra("addpost");
		if (!OtherUtil.isNullOrEmpty(status) && status.equals("publish")) {
			isPub = true;
		} else
			isPub = false;
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_icon_back);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		Button btnPub = (Button) findViewById(R.id.twobtn_header_right);
		btnPub.setText(R.string.publish);
		btnPub.setBackgroundResource(R.drawable.bar_btn_nothing);
		btnPub.setOnClickListener(this);
		btnPub.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		LinearLayout lyName = (LinearLayout) findViewById(R.id.addpost_lyName);
		if (isPub) {
			tvTitle.setText(R.string.publish);
			lyName.setVisibility(View.GONE);
		} else {
			lyName.setVisibility(View.VISIBLE);
			tvTitle.setText(R.string.reply);
		}
		rlSound = (RelativeLayout) findViewById(R.id.addpost_rlSound);
		tvSound = (TextView) findViewById(R.id.addpost_tvSound);
		cbText = (CheckBox) findViewById(R.id.addpost_cbSound);
		mImageVol = (ImageView) findViewById(R.id.addpost_imageVol);
		cbText.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					tvSound.setVisibility(View.VISIBLE);
					rlSound.setVisibility(View.VISIBLE);
				} else
					rlSound.setVisibility(View.GONE);
			}
		});
		ivPic = (ImageView) findViewById(R.id.addpost_ivPhoto);
		ivPic.setOnClickListener(this);
		ivDel = (ImageView) findViewById(R.id.addpost_ivDel);
		ivDel.setOnClickListener(this);
		btnRecord = (Button) findViewById(R.id.addpost_btn_record);
		btnRecord.setOnLongClickListener(this);
		btnRecord.setOnTouchListener(this);
		ivSoundDel = (ImageView) findViewById(R.id.addpost_ivSoundDel);
		ivSoundDel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.twobtn_header_left:
			AddPostActivity.this.finish();
			break;
		case R.id.twobtn_header_right:
			AddPostActivity.this.finish();
			break;
		case R.id.addpost_ivPhoto:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(AddPostActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			if (null == photo) {
				pop = new PicPopu(AddPostActivity.this, itemOnClick);
				pop.showAtLocation(
						AddPostActivity.this.findViewById(R.id.addpost_act),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;
		case R.id.addpost_ivDel:
			if (photo != null) {
				photo = null;
			}
			ivPic.setImageBitmap(null);
			ivPic.setBackgroundResource(R.drawable.avatars);
			ivDel.setVisibility(View.GONE);
			break;
		case R.id.addpost_ivSoundDel:
			if (null != fSound && fSound.exists()) {
				fSound.delete();
				isRecording = false;
				ivSoundDel.setVisibility(View.GONE);
				tvSound.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	private OnClickListener itemOnClick = new OnClickListener() {

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
	private String tempPath;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case GlobalData.PHOTO_REQUEST_TAKEPHOTO:
			if (tempFile != null && tempFile.exists()) {
				managePic(tempFile.getAbsolutePath());
			}
			break;

		case GlobalData.PHOTO_REQUEST_GALLERY:
			backPhotoGallery(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void backPhotoGallery(Intent data) {
		if (data != null) {

			Uri uri = data.getData();
			if (uri != null) {
				try {
					ContentResolver resolver = getContentResolver();
					Cursor cursor = resolver.query(uri, null, null, null, null);
					if (cursor != null) {
						int colunm_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(colunm_index);
						cursor.close();
						if (path.endsWith("jpg") || path.endsWith("png")) {
							managePic(path);
						} else {
							// Log.i("test", "pic lost");
						}
					}

				} catch (Exception e) {
					Log.i("test", e.toString());
				}
			}

		}
	}

	private void managePic(String path) {
		try {
			try {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, opts);
				opts.inSampleSize = OtherUtil.computeSampleSize(opts, -1,
						1024 * 720);// 1920*1080
				opts.inJustDecodeBounds = false;
				FileInputStream fis = new FileInputStream(path);
				photo = ThumbnailUtils.extractThumbnail(
						BitmapFactory.decodeStream(fis, null, opts), 96, 96,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				try {
					if (!OtherUtil.isNullOrEmpty(path)) {
						ExifInterface exif = new ExifInterface(path);
						int picWidth = Integer.valueOf(exif
								.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
						int picHeight = Integer.valueOf(exif
								.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
						if (picWidth < 640 && picHeight < 400) {
							ToastUtil.showToast(AddPostActivity.this, "");
							photo = null;
						} else {
							tempPath = path;
							ivPic.setImageBitmap(photo);
							ivDel.setVisibility(View.VISIBLE);
						}
					} else
						ivDel.setVisibility(View.GONE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			Log.i("test", e.toString());
		}

	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		if (null == fSound || !fSound.exists()) {
			isClick = true;
			record();
		} else
			isClick = false;
		return false;
	}

	private void record() {
		tvSound.setVisibility(View.GONE);
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 10, 100 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);
		fSound = OtherUtil.fileCreate("yl.amr", false);
		Log.i("1234", "fi" + fSound.getAbsolutePath());
		mr = new MediaRecorder();
		mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mr.setOutputFile(fSound.getAbsolutePath());
		try {
			fSound.createNewFile();
			mr.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pauseMusic();
		mr.start();
		new GetVolume().start();
	}

	private void pauseMusic() {
		audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioVolume = audioService.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioService.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
	}

	private class GetVolume extends Thread {
		public GetVolume() {
			isRecording = true;
		}

		@Override
		public void run() {
			super.run();
			// 记录时长
			mRecordTime = System.currentTimeMillis();
			while (isRecording) {
				int vuSize = 0;
				if (mr != null) {
					vuSize = 100 * mr.getMaxAmplitude() / 32768;
					handler.sendEmptyMessage(vuSize);
					try {
						sleep(150);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int vol = msg.what;
			Log.i("test", "vol: " + vol);
			if (vol < 5) {
				mImageVol.setBackgroundResource(R.drawable.vol1);
			} else if (vol > 5 && vol < 20) {
				mImageVol.setBackgroundResource(R.drawable.vol2);
			} else if (vol > 20 && vol < 36) {
				mImageVol.setBackgroundResource(R.drawable.vol3);
			} else if (vol > 36 && vol < 55) {
				mImageVol.setBackgroundResource(R.drawable.vol4);
			} else if (vol > 55 && vol < 70) {
				mImageVol.setBackgroundResource(R.drawable.vol5);
			} else if (vol > 70) {
				mImageVol.setBackgroundResource(R.drawable.vol6);
			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (isClick)
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				isClick = false;
				mRecordTimeEnd = System.currentTimeMillis();
				if (mr != null) {
					ivSoundDel.setVisibility(View.VISIBLE);
					// 恢复音量
					audioService.setStreamVolume(AudioManager.STREAM_MUSIC,
							audioVolume, 0);
					mr.stop();
					mr.release();
					mr = null;
					if ((mRecordTimeEnd - mRecordTime) > 1000) {
						mIntentType = 0;
						mIntentString = fSound.getAbsolutePath();

					} else {
						isRecording = false;
						ToastUtil.show(AddPostActivity.this,
								R.string.recorde_short);
					}
				}
				break;
			}

		return false;
	}
}
