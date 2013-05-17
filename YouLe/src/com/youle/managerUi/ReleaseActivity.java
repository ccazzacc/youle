package com.youle.managerUi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.youle.R;
import com.youle.util.OtherUtil;

public class ReleaseActivity extends Activity {
	private Button mBtnAdd, mBtnTxt, mBtnSay, mBtnRecord, mBtnCamera,mBtnXX,mBtnPic;
	private EditText mEditSay;
	private TableLayout mOtherLayout;
	private MediaRecorder mr;
	private RelativeLayout mLayoutRecord;
	private ProgressBar mProgressBar;
	private AudioManager audioService;
	private int audioVolume,mIntentType;
	private String mIntentString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_activity);
		initView();
	}

	private void initView() {
		mOtherLayout = (TableLayout) findViewById(R.id.relativeLayout_other);
		mLayoutRecord = (RelativeLayout) findViewById(R.id.RelativeLayout_record);
		mBtnAdd = (Button) findViewById(R.id.button_add);
		mBtnAdd.setOnClickListener(click);
		mBtnTxt = (Button) findViewById(R.id.button_txt);
		mBtnTxt.setOnClickListener(click);
		mBtnSay = (Button) findViewById(R.id.button_say);
		mBtnSay.setOnTouchListener(touch);
		mBtnSay.setOnLongClickListener(longClick);
		mEditSay = (EditText) findViewById(R.id.edit_say);
		mBtnRecord = (Button) findViewById(R.id.button_record);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mBtnCamera = (Button) findViewById(R.id.button_camera);
		mBtnCamera.setOnClickListener(click);
		mBtnXX=(Button)findViewById(R.id.button_xx);
		mBtnXX.setOnClickListener(click);
		mBtnPic=(Button)findViewById(R.id.button_photos);
		mBtnPic.setOnClickListener(click);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap bmp = null;
		ImageView im = (ImageView)findViewById(R.id.im);
		switch (resultCode) {
		case RESULT_OK:
			super.onActivityResult(requestCode, resultCode, data);
			if (data != null&&requestCode==1) {
				mIntentString=data.getData()+"";
				mIntentType=1;
				send();
//				Log.i("1234", data.getData()+"");
//				Bundle extras = data.getExtras();
//				bmp =(Bitmap) extras.get("data");
//				im.setImageBitmap(bmp);
			}else if(requestCode==2){
				mIntentString=data.getData()+"";
				mIntentType=2;
				send();
//				try {
//					bmp=BitmapFactory.decodeStream(ReleaseActivity.this.getContentResolver().openInputStream(data.getData()));
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				im.setImageBitmap(bmp);
//				Log.i("1234", ""+data.getData());
			}
			break;
		}
	}

	OnClickListener click = new OnClickListener() {
		boolean isTxt = true;

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_add:
				mOtherLayout.setVisibility(View.VISIBLE);
				if(mEditSay.getVisibility()==View.VISIBLE){
					//发送
					mIntentString=mEditSay.getText()+"";
					mIntentType=3;
					send();
				}
				break;
			case R.id.button_txt:
				if (isTxt) {
					v.setBackgroundResource(R.drawable.button_recording_d);
					mBtnAdd.setBackgroundResource(R.drawable.button_send);
					mBtnSay.setVisibility(View.GONE);
					mEditSay.setVisibility(View.VISIBLE);
					mEditSay.requestFocus();
					isTxt = false;
				} else {
					v.setBackgroundResource(R.drawable.button_text_a);
					mBtnAdd.setBackgroundResource(R.drawable.bar_button_add);
					mBtnSay.setVisibility(View.VISIBLE);
					mEditSay.setVisibility(View.GONE);
					isTxt = true;
				}
				break;
			case R.id.button_camera:
				Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
				startActivityForResult(i, Activity.DEFAULT_KEYS_DIALER);
				break;
			case R.id.button_xx:
				mOtherLayout.setVisibility(View.GONE);
				break;
			case R.id.button_photos:
				Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
				innerIntent.setType("image/*"); // 查看类型
				Intent wrapperIntent = Intent.createChooser(innerIntent, null);
				startActivityForResult(wrapperIntent, 2);
				break;
			}
		}
	};
	OnLongClickListener longClick = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			Log.i("1234", "1234");
			mLayoutRecord.setVisibility(View.VISIBLE);
			record();
			return false;
		}

	};
	private void send(){
		Intent intent=new Intent();
		intent.putExtra("type", mIntentType);
		intent.putExtra("txt", mIntentString);
		intent.setClass(ReleaseActivity.this, ReleaseOkActivity.class);
		startActivity(intent);
	}
	File file;
	OnTouchListener touch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				Log.i("1234", "finalize");
				if (mr != null) {
					mLayoutRecord.setVisibility(View.GONE);
					// 恢复音量
					audioService.setStreamVolume(AudioManager.STREAM_MUSIC,
							audioVolume, 0);
					// ////////////
					MediaPlayer mPlayer = new MediaPlayer();
					try {
						// 设置要播放的文件05-16 11:59:58.657: E/MediaPlayer(15259):
						// Unable to to create media player

						Log.i("1234", file.getAbsolutePath());
						mPlayer.setDataSource("/sdcard/STK/20130516_120550.amr");
						mPlayer.prepare();
						// 播放之
						mPlayer.start();
					} catch (IOException e) {
						Log.e("1234", "prepare() failed");
					}
					// ///////////
					mr.stop();
					mr.release();
					mr = null;
				}
				break;
			}

			return false;
		}
	};

	private void record() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 10, 100 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);

		file = new File("/sdcard/"
				+ "STK/"
				+ new DateFormat().format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA)) + ".amr");
		// 创建录音对象
		mr = new MediaRecorder();
		// 从麦克风源进行录音
		mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		// 设置输出格式
		mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		// 设置编码格式
		mr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		// 设置输出文件

		mr.setOutputFile(file.getAbsolutePath());
		try {
			// 创建文件
			file.createNewFile();
			// 准备录制
			mr.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		// 暂停音乐
		pauseMusic();
		// 开始录制
		mr.start();
		new GetVolume().start();
	}

	private void pauseMusic() {
		Log.i("1234", "pauseMusic");
		// 原生系统可以
		// Intent freshIntent = new Intent();
		// freshIntent.setAction("com.android.music.musicservicecommand.pause");
		// freshIntent.putExtra("command", "pause");
		// ReleaseActivity.this.sendBroadcast(freshIntent);
		audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioVolume = audioService.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioService.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
	}

	private class GetVolume extends Thread {

		public GetVolume() {
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			while (true) {
				int vuSize = 0;
				if (mr != null) {
					vuSize = 100 * mr.getMaxAmplitude() / 32768;
					// Log.i("1234",
					// "麦克风音量大小： " + vuSize + " sdf  "
					// + mr.getMaxAmplitude());
					mProgressBar.setProgress(vuSize);
					try {
						sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					return;
				}
			}

		}

	}

}
