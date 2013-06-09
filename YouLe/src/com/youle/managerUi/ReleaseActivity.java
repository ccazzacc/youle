package com.youle.managerUi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.youle.R;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;

import java.io.File;
import java.io.IOException;

public class ReleaseActivity extends Activity {
    public ImageView mImageVol;
    OnClickListener click = new OnClickListener() {
        boolean isTxt = true;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_add:
                    mOtherLayout.setVisibility(View.VISIBLE);
                    if (mEditSay.getVisibility() == View.VISIBLE) {
                        //发送
                        mIntentString = mEditSay.getText() + "";
                        mIntentType = 3;
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
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoPath));
                    startActivityForResult(i, Activity.DEFAULT_KEYS_DIALER);
                    break;
                case R.id.button_xx:
//                    mOtherLayout.setVisibility(View.GONE);
                    finish();
                    break;
                case R.id.button_photos:
                    Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
                    innerIntent.setType("image/*"); // 查看类型
                    Intent wrapperIntent = Intent.createChooser(innerIntent, null);
                    startActivityForResult(wrapperIntent, 2);
                    break;
                case R.id.rB1:
                    mEvenType = 1;
                    setCheck(mRB1);
                    break;
                case R.id.rB2:
                    mEvenType = 2;
                    setCheck(mRB2);
                    break;
                case R.id.rB3:
                    mEvenType = 3;
                    setCheck(mRB3);
                    break;
                case R.id.rB4:
                    mEvenType = 4;
                    setCheck(mRB4);
                    break;
                case R.id.rB5:
                    mEvenType = 5;
                    setCheck(mRB5);
                    break;
            }
        }

        private void setCheck(RadioButton rb) {

            mRB1.setChecked(false);
            mRB2.setChecked(false);
            mRB3.setChecked(false);
            mRB4.setChecked(false);
            mRB5.setChecked(false);
            rb.setChecked(true);
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
    File file;
    OnTouchListener touch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    mRecordTimeEnd = System.currentTimeMillis();
                    Log.i("1234", "finalize: " + (mRecordTimeEnd - mRecordTime));
                    if (mr != null) {
                        mLayoutRecord.setVisibility(View.GONE);
                        // 恢复音量
                        audioService.setStreamVolume(AudioManager.STREAM_MUSIC,
                                audioVolume, 0);
                        mr.stop();
                        mr.release();
                        mr = null;
                        if ((mRecordTimeEnd - mRecordTime) > 1000) {
                            mIntentType = 0;
                            mIntentString = file.getAbsolutePath();
                            send();
                        } else {
                            isRecording = false;
                            ToastUtil.show(ReleaseActivity.this, R.string.recorde_short);
                        }
                    }
                    break;
            }

            return false;
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int vol = msg.what;
            Log.i("1234", "vol: " + vol);
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
    private Button mBtnAdd, mBtnTxt, mBtnSay, mBtnRecord, mBtnCamera, mBtnXX, mBtnPic;
    private EditText mEditSay;
    private TableLayout mOtherLayout;
    private MediaRecorder mr;
    private RelativeLayout mLayoutRecord;
    private AudioManager audioService;
    private int audioVolume, mIntentType, mEvenType;
    private String mIntentString;
    private RadioButton mRB1, mRB2, mRB3, mRB4, mRB5;
    private File mPhotoPath;
    private long mRecordTime, mRecordTimeEnd;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_activity2);
        initView();
    }

    private void initView() {
        File path = new File(OtherUtil.getSDcard()
                + "/.cache/");
        if (!path.exists()) {
            path.mkdir();
        }
        mPhotoPath = new File(path, "upload.png");


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
        mEditSay.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(ReleaseActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mBtnRecord = (Button) findViewById(R.id.button_record);
        mBtnCamera = (Button) findViewById(R.id.button_camera);
        mBtnCamera.setOnClickListener(click);
        mBtnXX = (Button) findViewById(R.id.button_xx);
        mBtnXX.setOnClickListener(click);
        mBtnPic = (Button) findViewById(R.id.button_photos);
        mBtnPic.setOnClickListener(click);
        mRB1 = (RadioButton) findViewById(R.id.rB1);
        mRB2 = (RadioButton) findViewById(R.id.rB2);
        mRB3 = (RadioButton) findViewById(R.id.rB3);
        mRB4 = (RadioButton) findViewById(R.id.rB4);
        mRB5 = (RadioButton) findViewById(R.id.rB5);
        mRB1.setOnClickListener(click);
        mRB2.setOnClickListener(click);
        mRB3.setOnClickListener(click);
        mRB4.setOnClickListener(click);
        mRB5.setOnClickListener(click);
        mImageVol = (ImageView) findViewById(R.id.imageVol);

    }
    private class Cav extends View{

        public Cav(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bmp = null;
        switch (resultCode) {
            case RESULT_OK:
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1) {
                    Log.i("1234", "data.getData() " + mPhotoPath.getAbsolutePath());
                    mIntentString = mPhotoPath.getAbsolutePath();
                    mIntentType = 1;
                    send();
//				Log.i("1234", data.getData()+"");
//				Bundle extras = data.getExtras();
//				bmp =(Bitmap) extras.get("data");
//				im.setImageBitmap(bmp);
                } else if (requestCode == 2) {
                    mIntentString = data.getData() + "";
                    mIntentType = 2;
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

    private void send() {
        Intent intent = new Intent();
        intent.putExtra("even", mEvenType);
        intent.putExtra("type", mIntentType);
        intent.putExtra("txt", mIntentString);
        intent.setClass(ReleaseActivity.this, ReleaseOkActivity.class);
        startActivity(intent);
    }

    private void record() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {10, 100}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);
        String sd = OtherUtil.getSDcard();

        File destDir = new File(sd
                + "/.cache/");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        file = new File(destDir.getPath() + "/"
                + "YouLeRecord.amr");
        Log.i("1234", "fi" + file.getAbsolutePath());
        // 创建录音对象
        mr = new MediaRecorder();
        // 从麦克风源进行录音
        mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 设置输出格式
        mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        // 设置编码格式
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
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
            Log.i("1234", "kaishi");
            isRecording = true;
        }

        @Override
        public void run() {
            super.run();
            //记录时长
            mRecordTime = System.currentTimeMillis();
            while (isRecording) {
                int vuSize = 0;
                if (mr != null) {
                    vuSize = 100 * mr.getMaxAmplitude() / 32768;
//                    Log.i("1234",
//                    "麦克风音量大小： " + vuSize + " sdf  "
//                    + mr.getMaxAmplitude());
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

}
