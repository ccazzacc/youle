package com.youle.managerUi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.managerData.MyApplication;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CircleProgress;

import java.io.File;
import java.io.IOException;

public class ReleaseActivity extends StatActivity {
    //    public ImageView mImageVol;
    OnClickListener click = new OnClickListener() {
        boolean isTxt = true;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.release_btnAdd:
                    mOtherLayout.setVisibility(View.VISIBLE);
                    mBtnXX.setVisibility(View.VISIBLE);

                    break;
                case R.id.release_btnQuit:
                    mOtherLayout.setVisibility(View.GONE);
                    v.setVisibility(View.GONE);
                    break;
                case R.id.release_send:
                    //发送
                    mIntentString = mEditInput.getText() + "";
                    mIntentType = 3;
                    send();
                    break;
                case R.id.button_txt:
                    if (isTxt) {
                        v.setBackgroundResource(R.drawable.button_recording_d);
                        mLayoutInput.setVisibility(View.VISIBLE);
                        mLayoutRecord.setVisibility(View.GONE);
                        mEditInput.setFocusable(true);
                        mEditInput.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        isTxt = false;
                    } else {
                        v.setBackgroundResource(R.drawable.button_text_a);
                        mLayoutInput.setVisibility(View.GONE);
                        mLayoutRecord.setVisibility(View.VISIBLE);
                        isTxt = true;
                    }
                    break;

                case R.id.button_camera:
                    Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoPath));
                    startActivityForResult(i, Activity.DEFAULT_KEYS_DIALER);
                    break;
//                case R.id.button_xx:
////                    mOtherLayout.setVisibility(View.GONE);
//                    finish();
//                    break;
                case R.id.button_photos:
                    Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
                    innerIntent.setType("image/*"); // 查看类型
                    Intent wrapperIntent = Intent.createChooser(innerIntent, null);
                    startActivityForResult(wrapperIntent, 2);
                    break;
                case R.id.release_btn1:
                    setEven(0);
                    break;
                case R.id.release_btn2:
                    setEven(1);
                    break;
                case R.id.release_btn3:
                    setEven(2);
                    break;
                case R.id.release_btn4:
                    setEven(3);
                    break;
                case R.id.release_btn5:
                    setEven(4);
                    break;
                case R.id.release_btn6:
                    setEven(5);
                    break;
                case R.id.release_close:
//                    startActivity(new Intent(ReleaseActivity.this, SlidActivity.class));
                    finish();
//                    ReleaseActivity.this.overridePendingTransition(
//                            R.anim.push_right_in, R.anim.push_right_out);

                    break;
                case R.id.release_btnRecord:
                    if(GlobalData.isIn_Vehicle){
                        if(isRecord){
                            mTextInfo.setText("点击开始录音");
                            stopRecord();
                            isRecord=false;

                        }else{
                            mTextInfo.setText("停止录音并上传");
                            record();
                            isRecord=true;

                        }

                    }
                    break;

            }
        }

        private void setEven(int witch) {
            mEvenType = witch;
            int[] drP = {R.drawable.crowd_pressed, R.drawable.accident_pressed, R.drawable.danger_pressed, R.drawable.road_pressed, R.drawable.often_pressed, R.drawable.webcam_pressed};
            int[] drN = {R.drawable.crowd_normal, R.drawable.accident_normal, R.drawable.danger_normal, R.drawable.road_normal, R.drawable.often_normal, R.drawable.webcam_normal};

            for (int i = 0; i < 6; i++) {
                if (i == witch) {
                    mEven[i].setBackgroundResource(drP[witch]);
                } else {
                    mEven[i].setBackgroundResource(drN[i]);
                }
            }
        }
    };
    OnLongClickListener longClick = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            Log.i("1234", "1234");
//            mLayoutRecord.setVisibility(View.VISIBLE);
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
                    stopRecord();
                    break;
            }

            return false;
        }
    };
    private void stopRecord(){
        long mRecordTimeEnd = System.currentTimeMillis();
        Log.i("1234", "finalize: " + (mRecordTimeEnd - mRecordTime));
        if (mr != null) {
//                        mLayoutRecord.setVisibility(View.GONE);
            mCircleProgressBar1.stopCartoom();
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
    }

    private Button mBtnXX;
    private Button[] mEven = new Button[6];
    private EditText mEditInput;
    private TextView mTextInfo;
    private TableLayout mOtherLayout;
    private MediaRecorder mr;
    private RelativeLayout mLayoutInput, mLayoutRecord;
    private AudioManager audioService;
    private int audioVolume, mIntentType, mEvenType = 1;
    private String mIntentString;
    private File mPhotoPath;
    private long mRecordTime;
    private boolean isRecording,isRecord;
    private CircleProgress mCircleProgressBar1;
    private TextView mTextView;
    private double lat = 0, lng = 0;
    private float spd = 0;
    private String address;
    private MyBroadcastReciver reciver;
    private TextWatcher mTextWatcher = new TextWatcher() {

        private int editStart;
        private int editEnd;

        public void afterTextChanged(Editable s) {
            editStart = mEditInput.getSelectionStart();
            editEnd = mEditInput.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            mEditInput.removeTextChangedListener(mTextWatcher);

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            while (calculateLength(s.toString()) > 140) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            // mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
            mEditInput.setSelection(editStart);

            // 恢复监听器
            mEditInput.addTextChangedListener(mTextWatcher);

            setLeftCount();
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_activity2);
        MyApplication.getInstance().addActivity(this);
        initView();

        OtherUtil.getLocation(ReleaseActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalData.BROADCAST_COUNTER_ACTION);
        reciver = new MyBroadcastReciver();
        this.registerReceiver(reciver, intentFilter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==4){
            startActivity(new Intent(ReleaseActivity.this, SlidActivity.class));
            ReleaseActivity.this.overridePendingTransition(
                    R.anim.push_right_in, R.anim.push_right_out);
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }

    private void initView() {
        File path = new File(OtherUtil.getSDcard()
                + "/.cache/");
        if (!path.exists()) {
            path.mkdir();
        }
        mPhotoPath = new File(path, "upload.jpg");
        mLayoutRecord = (RelativeLayout) findViewById(R.id.release_layout_record);
        mLayoutInput = (RelativeLayout) findViewById(R.id.release_input);
        mOtherLayout = (TableLayout) findViewById(R.id.relativeLayout_other);
//        mLayoutRecord = (RelativeLayout) findViewById(R.id.RelativeLayout_record);
        mTextInfo=(TextView)findViewById(R.id.relase_txt_info);
        Button mBtnAdd = (Button) findViewById(R.id.release_btnAdd);
        mBtnAdd.setOnClickListener(click);
        mBtnXX = (Button) findViewById(R.id.release_btnQuit);
        mBtnXX.setOnClickListener(click);
        Button mBtnTxt = (Button) findViewById(R.id.button_txt);
        mBtnTxt.setOnClickListener(click);
        Button mBtnRecord = (Button) findViewById(R.id.release_btnRecord);
        mBtnRecord.setOnTouchListener(touch);
        if (GlobalData.isIn_Vehicle) {
            mTextInfo.setText("点击开始录音");
            mBtnRecord.setOnClickListener(click);
            mBtnAdd.setVisibility(View.GONE);
            TextView textView1=(TextView)findViewById(R.id.textView1);
            TextView textView2=(TextView)findViewById(R.id.textView2);
            TextView textView3=(TextView)findViewById(R.id.textView3);
            TextView textView4=(TextView)findViewById(R.id.textView4);
            TextView textView5=(TextView)findViewById(R.id.textView5);
            TextView textView6=(TextView)findViewById(R.id.textView6);
            textView6.setVisibility(View.GONE);
            textView5.setVisibility(View.GONE);
            textView4.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
        } else {
            mBtnRecord.setOnLongClickListener(longClick);
        }
        mEditInput = (EditText) findViewById(R.id.release_edit_input);
        mEditInput.addTextChangedListener(mTextWatcher);
        mEditInput.setSelection(mEditInput.length()); // 将光标移动最后一个字符后面
        mEditInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(ReleaseActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mTextView = (TextView) findViewById(R.id.release_size);
        setLeftCount();
//        mBtnRecord = (Button) findViewById(R.id.button_record);
        Button mBtnCamera = (Button) findViewById(R.id.button_camera);
        mBtnCamera.setOnClickListener(click);
//        mBtnXX = (Button) findViewById(R.id.button_xx);
//        mBtnXX.setOnClickListener(click);
        Button mBtnPic = (Button) findViewById(R.id.button_photos);
        mBtnPic.setOnClickListener(click);
//        mImageVol = (ImageView) findViewById(R.id.imageVol);
        mEven[0] = (Button) findViewById(R.id.release_btn1);
        mEven[1] = (Button) findViewById(R.id.release_btn2);
        mEven[2] = (Button) findViewById(R.id.release_btn3);
        mEven[3] = (Button) findViewById(R.id.release_btn4);
        mEven[4] = (Button) findViewById(R.id.release_btn5);
        mEven[5] = (Button) findViewById(R.id.release_btn6);
        mEven[0].setOnClickListener(click);
        mEven[1].setOnClickListener(click);
        mEven[2].setOnClickListener(click);
        mEven[3].setOnClickListener(click);
        mEven[4].setOnClickListener(click);
        mEven[5].setOnClickListener(click);
        Button mBtnSend = (Button) findViewById(R.id.release_send);
        mBtnSend.setOnClickListener(click);
        Button close = (Button) findViewById(R.id.release_close);
        close.setOnClickListener(click);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1) {
                    Log.i("1234", "data.getData() " + mPhotoPath.getAbsolutePath());
                    mIntentString = mPhotoPath.getAbsolutePath();
                    mIntentType = 1;
                    send();
                } else if (requestCode == 2) {
                    mIntentString = data.getData() + "";
                    mIntentType = 2;
                    send();
                }
                break;
        }
    }

    private void send() {
        Intent intent = new Intent();
        intent.putExtra("even", mEvenType);
        intent.putExtra("type", mIntentType);
        intent.putExtra("txt", mIntentString);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("address", address);
        intent.setClass(ReleaseActivity.this, ReleaseOkActivity.class);
        startActivity(intent);
//        finish();
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
        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mr.setOutputFile(file.getAbsolutePath());
        try {
            // 创建文件
            file.createNewFile();
            mr.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 暂停音乐
        pauseMusic();
        mr.start();

        mCircleProgressBar1 = (CircleProgress) findViewById(R.id.roundBar1);
        mCircleProgressBar1.setMainProgress(0);
        mCircleProgressBar1.startCartoom(60);

        new GetVolume().start();
    }

    private void pauseMusic() {
        Log.i("1234", "pauseMusic");
        audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioVolume = audioService.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioService.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    /**
     * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
     */
    private void setLeftCount() {
        mTextView.setText(String.valueOf((140 - getInputCount())));
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(mEditInput.getText().toString());
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
//                    handler.sendEmptyMessage(vuSize);
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

    public class MyBroadcastReciver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(GlobalData.BROADCAST_COUNTER_ACTION)) {
                // AMapLocation location=(AMapLocation)intent.getExtras();
                Bundle bundle = intent.getExtras();
                Log.i("1234", "onReceive  " + bundle.getDouble("lat") + " "
                        + bundle.getDouble("lng"));
                lat = bundle.getDouble("lat");
                lng = bundle.getDouble("lng");
                spd = bundle.getFloat("spd");
                new Thread() {

                    @Override
                    public void run() {
                       String[] add = OtherUtil.getDesc(ReleaseActivity.this,
                                lat, lng);
                        address=add[2];
                        Log.i("1234", address);
                        Message msg=new Message();
                        msg.obj=add[3];
                        handler.sendMessage(msg);
                        super.run();
                    }
                }.start();

            }
        }
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String s= (String) msg.obj;
                if(!s.equals("中国")){
                    OtherUtil.showCloseDialog(ReleaseActivity.this);
                }
            }
        };

    }


}
