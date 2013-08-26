package com.youle.managerUi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PicPopu;
import com.youle.view.ShowDialog;

public class AddPostActivity extends StatActivity implements OnClickListener,
		OnLongClickListener, OnTouchListener {
	private boolean isPub = false, isRecording, isClick;
	private ImageView ivPic;
	private PicPopu pop;
	private Bitmap photo;
	private File tempFile = OtherUtil.fileCreate("comment.jpg", false);
	private CheckBox cbText;
	private RelativeLayout rlSound;
	private TextView tvSound;
	private EditText etCom;
	private Button btnRecord, btnPlay;
	private File fSound;
	private MediaRecorder mr;
	private MediaPlayer mediaPlay;
	private AudioManager audioService;
	private int audioVolume, mAudTime;
	private long mRecordTime, mRecordTimeEnd;
	private int i = 1;
	private String forumId, reName, postId, post;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpost_activity);
		initView();
		MyApplication.getInstance().addActivity(this);
	}

	private void initView() {
		String status = this.getIntent().getStringExtra("addpost");
		forumId = getIntent().getStringExtra(GlobalData.FORUM_ID);
		if (!OtherUtil.isNullOrEmpty(status) && status.equals("publish")) {
			isPub = true;
		} else
			isPub = false;
		etCom = (EditText) findViewById(R.id.addpost_etComment);
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_close_normal);
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		Button btnPub = (Button) findViewById(R.id.twobtn_header_right);
		btnPub.setBackgroundResource(R.drawable.bar_button_done_normal);
		btnPub.setOnClickListener(this);
		btnPub.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		LinearLayout lyName = (LinearLayout) findViewById(R.id.addpost_lyName);
		TextView tvReName = (TextView) findViewById(R.id.addpost_tvReName);
		if (isPub) {
			tvTitle.setText(R.string.publish);
			lyName.setVisibility(View.GONE);
		} else {
			postId = this.getIntent().getStringExtra(GlobalData.POST_ID);
			post = this.getIntent().getStringExtra("post");
			reName = this.getIntent().getStringExtra("reply");
			if(OtherUtil.isNullOrEmpty(reName))
			{
				lyName.setVisibility(View.GONE);
				tvTitle.setText(R.string.reply_title);
			}
			else
			{
				tvTitle.setText(R.string.reply_tiezi);
				lyName.setVisibility(View.VISIBLE);
				tvReName.setText(reName);
			}
		}
		rlSound = (RelativeLayout) findViewById(R.id.addpost_rlSound);
		tvSound = (TextView) findViewById(R.id.addpost_tvSound);
		cbText = (CheckBox) findViewById(R.id.addpost_cbSound);
		btnPlay = (Button) findViewById(R.id.addpost_btn_play);
		btnPlay.setOnClickListener(this);
		btnRecord = (Button) findViewById(R.id.addpost_btn_record);
		btnRecord.setOnLongClickListener(this);
		btnRecord.setOnTouchListener(this);
		cbText.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					if (null != fSound && fSound.exists())
						fSound.delete();
					tvSound.setVisibility(View.VISIBLE);
					rlSound.setVisibility(View.VISIBLE);
					btnRecord.setVisibility(View.VISIBLE);
					btnRecord.requestFocus();
					btnPlay.setVisibility(View.GONE);
					etCom.setText("");
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(AddPostActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				} else
				{
					rlSound.setVisibility(View.GONE);
					if (null != fSound && fSound.exists())
						fSound.delete();
				}
			}
		});
		ivPic = (ImageView) findViewById(R.id.addpost_ivPhoto);
		ivPic.setOnClickListener(this);
		etCom.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
					cbText.setChecked(false);
			}
		});
		etCom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cbText.setChecked(false);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.twobtn_header_left:
			if(!OtherUtil.isNullOrEmpty(etCom.getText().toString()) || !OtherUtil.isNullOrEmpty(tempPath) || (null != fSound && fSound.exists()))
			{
				Intent intent = new Intent(AddPostActivity.this, ShowDialog.class);
	            intent.putExtra("showTwo", true);
	            intent.putExtra("dialog_title", "温馨提示");
	            intent.putExtra("dialog_text", "您真的丢弃当前编辑的内容吗？");
	            intent.putExtra("yes", "确定");
	            intent.putExtra("no", "取消");
	            startActivityForResult(intent, 0);
			}else
			{
				if (isPub) {
					Intent it = new Intent(AddPostActivity.this,
							CarListActivity.class);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					startActivity(it);
				} else {
					Intent it = new Intent(AddPostActivity.this,
							CarReplyActivity.class);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					it.putExtra(GlobalData.POST_ID, post);
					startActivity(it);
				}
				AddPostActivity.this.finish();
			}
			break;
		case R.id.twobtn_header_right:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
			.hideSoftInputFromWindow(AddPostActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			if(!OtherUtil.is3gWifi(this))
	        	ToastUtil.show(this, R.string.net_no);
	        else
	        	new PostTask().execute();
			break;
		case R.id.addpost_ivPhoto:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(AddPostActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			pop = new PicPopu(AddPostActivity.this, itemOnClick);
			pop.showAtLocation(
					AddPostActivity.this.findViewById(R.id.addpost_act),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.addpost_btn_play:
			if (mediaPlay == null)
				mediaPlay = new MediaPlayer();
			if ((mediaPlay != null && mediaPlay.isPlaying()) == true)
				i++;
			if (i % 2 == 1) {
				btnPlay.setBackgroundResource(R.drawable.car_stop);
				try {
					mediaPlay.reset();
					mediaPlay.setDataSource(fSound.getAbsolutePath());
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
						try {
							while ((mediaPlay != null && mediaPlay.isPlaying()) == true) {
							}
						} catch (Exception e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						}
						msg.what = 1;
						handler.sendMessage(msg);
					}

				}.start();
			} else {
				if ((mediaPlay != null && mediaPlay.isPlaying()) == true) {
					mediaPlay.stop();
					mediaPlay = null;
				}
				i = 1;
				btnPlay.setBackgroundResource(R.drawable.car_play);
			}
			break;
		}
	}

	private class PostTask extends AsyncTask<Void, Void, String> {
		String aud = null, txt = null;

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.i("test", "tempPath:" + tempPath + " aud:" + aud + " time:"
					+ mAudTime + " txt:" + txt);
			if (!OtherUtil.isNullOrEmpty(txt) || !OtherUtil.isNullOrEmpty(aud)
					|| !OtherUtil.isNullOrEmpty(tempPath)) {
				Log.i("test", "isPub:"+isPub);
				if (isPub) {
					return YouLe.pubTopic(AddPostActivity.this, forumId, txt,
							tempPath, aud, mAudTime);
				} else {
					if(OtherUtil.isNullOrEmpty(reName))
						return YouLe.replyTopic(AddPostActivity.this, postId,txt,
								tempPath, aud, mAudTime);
					else
						return YouLe.replyTopic(AddPostActivity.this, postId,
							new StringBuffer().append("^").append(reName)
									.append("$").append(txt).toString(),
							tempPath, aud, mAudTime);
				}
			}

			return null;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(AddPostActivity.this);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(AddPostActivity.this);
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				if (isPub) {
					Intent it = new Intent(AddPostActivity.this,
							CarListActivity.class);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					startActivity(it);
				} else {
					Intent it = new Intent(AddPostActivity.this,
							CarReplyActivity.class);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					it.putExtra(GlobalData.POST_ID, post);
					startActivity(it);
				}
				AddPostActivity.this.finish();
			} else
				ToastUtil.showToast(AddPostActivity.this, result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (null != fSound && fSound.exists()) {
				aud = fSound.getAbsolutePath();
				Log.e("test", "aud:" + aud);
				if (null == mediaPlay)
					mediaPlay = new MediaPlayer();
				if ((mediaPlay != null && mediaPlay.isPlaying()) == true)
					mediaPlay.stop();
				try {
					mediaPlay.reset();
					mediaPlay.setDataSource(aud);
					mediaPlay.prepare();
					mAudTime = mediaPlay.getDuration() / 1000;
				} catch (IOException e) {
				}
			}
			txt = etCom.getText().toString();
			CustomProgressDialog.showMsg(AddPostActivity.this, getString(R.string.please_wait));
		}

	}
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				i = 1;
				btnPlay.setBackgroundResource(R.drawable.car_play);
			}
		}

	};
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
		case 0:
			String yes = null;
			if(null != data)
			   yes = data.getStringExtra("yes");
			if(!OtherUtil.isNullOrEmpty(yes))
			{
				if (isPub) {
					Intent it = new Intent(AddPostActivity.this,
							CarListActivity.class);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					startActivity(it);
				} else {
					Intent it = new Intent(AddPostActivity.this,
							CarReplyActivity.class);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					it.putExtra(GlobalData.POST_ID, post);
					startActivity(it);
				}
				AddPostActivity.this.finish();
			}
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
				if (!OtherUtil.isNullOrEmpty(path)) {
					ExifInterface exif;
					try {
						exif = new ExifInterface(path);
						int picWidth = Integer.valueOf(exif
								.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
						int picHeight = Integer.valueOf(exif
								.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
						// if (picWidth < 320 && picHeight < 320) {
						// ToastUtil.showToast(AddPostActivity.this, "");
						// photo = null;
						// } else {
						tempPath = OtherUtil.saveBitmap(path);
						ivPic.setImageBitmap(photo);
						// }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
		String sd = OtherUtil.getSDcard();
		File destDir = new File(sd + "/.cache/");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		fSound = new File(destDir.getPath() + "/" + "YouLeRecord.amr");
		Log.i("test", "fi" + fSound.getAbsolutePath());
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
		// new GetVolume().start();
	}

	private void pauseMusic() {
		audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioVolume = audioService.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioService.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
	}

	// private class GetVolume extends Thread {
	// public GetVolume() {
	// isRecording = true;
	// }
	//
	// @Override
	// public void run() {
	// super.run();
	// // 记录时长
	// mRecordTime = System.currentTimeMillis();
	// while (isRecording) {
	// int vuSize = 0;
	// if (mr != null) {
	// vuSize = 100 * mr.getMaxAmplitude() / 32768;
	// try {
	// sleep(150);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (isClick)
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				isClick = false;
				mRecordTimeEnd = System.currentTimeMillis();
				if (mr != null) {
					// 恢复音量
					audioService.setStreamVolume(AudioManager.STREAM_MUSIC,
							audioVolume, 0);
					mr.stop();
					mr.release();
					mr = null;
					if ((mRecordTimeEnd - mRecordTime) > 1000) {
						btnRecord.setVisibility(View.GONE);
						btnPlay.setVisibility(View.VISIBLE);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isPub) {
				Intent it = new Intent(AddPostActivity.this,
						CarListActivity.class);
				it.putExtra(GlobalData.FORUM_ID, forumId);
				startActivity(it);
			} else {
				Intent it = new Intent(AddPostActivity.this,
						CarReplyActivity.class);
				it.putExtra(GlobalData.FORUM_ID, forumId);
				it.putExtra(GlobalData.POST_ID, post);
				startActivity(it);
			}
			AddPostActivity.this.finish();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != photo)
			photo = null;
	}

}
