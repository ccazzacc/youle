package com.youle.managerUi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.AbstractWeibo.ShareParams;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import com.youle.R;
import com.youle.service.UpdataService;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ReleaseOkActivity extends Activity {
    OnClickListener click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_share_sina:
                    if (isShareSina) {
                        v.setBackgroundResource(R.drawable.buton_sinaweibo_selected);
                        isShareSina = false;
                    } else {
                        pb.setVisibility(View.VISIBLE);
                        mSWeibo = AbstractWeibo.getWeibo(ReleaseOkActivity.this,
                                SinaWeibo.NAME);
                        mSWeibo.setWeiboActionListener(weiboListener);
                        mSWeibo.showUser(null);
                    }
                    break;
                case R.id.btn_share_qq:
                    if (isShareQQ) {
                        v.setBackgroundResource(R.drawable.buton_tencent_selected);
                        isShareQQ = false;
                    } else {
                        pb.setVisibility(View.VISIBLE);
                        mQWeibo = AbstractWeibo.getWeibo(ReleaseOkActivity.this,
                                TencentWeibo.NAME);
                        mQWeibo.setWeiboActionListener(weiboListener);
                        mQWeibo.showUser(null);
                    }
                    break;
                case R.id.btn_re_ok_send:
                    upload();
                    break;
                case R.id.button_play:
                    mPlayer.start();
                    break;
                case R.id.btn_re_ok_canel:
                    finish();
                    break;
            }
        }
    };
    WeiboActionListener weiboListener = new WeiboActionListener() {

        @Override
        public void onError(AbstractWeibo arg0, int arg1, Throwable arg2) {
            Log.i("1234", "onError");
        }

        @Override
        public void onComplete(AbstractWeibo weibo, int arg1,
                               HashMap<String, Object> res) {
            if (weibo.getId() == 1) {
                Log.i("1234", "111 " + weibo.getDb().get("nickname") + " id: "
                        + weibo.getDb().getWeiboId());
                handler.sendEmptyMessage(1);

            } else {
                handler.sendEmptyMessage(2);
                Log.i("1234", "111 " + weibo.getDb().get("nickname"));
            }

        }

        @Override
        public void onCancel(AbstractWeibo arg0, int arg1) {
            Log.i("1234", "onCancel");
        }
    };
    private MyBroadcastReciver reciver;
    private TextView mTxtLoc, mTxtSay;
    private ImageView mIvRoad;
    private Button mShareSina, mShareQQ, mBtnUpload, mBtnPlay, mBtnUp, mBtnCanel;
    private boolean mIsUnRegister, isShareSina, isShareQQ;
    private String[] address;
    private String mAudPath,mImgPath,mTxt;
    private int mType, mEvenType,mAudTime;
    private AbstractWeibo mSWeibo, mQWeibo;
    private ProgressBar pb;
    private MediaPlayer mPlayer;
    private double lat = 0, lng = 0;
    private float spd = 0;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mTxtLoc.setText(" " + address[0] + address[1] + address[2]);
                    break;
                case 1:
                    mShareSina
                            .setBackgroundResource(R.drawable.buton_sinaweibo_normal);
                    isShareSina = true;
                    pb.setVisibility(View.GONE);
                    break;
                case 2:
                    mShareQQ.setBackgroundResource(R.drawable.buton_tencent_normal);
                    isShareQQ = true;
                    pb.setVisibility(View.GONE);
                    break;
                default:

                    break;
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_ok_activity);
        AbstractWeibo.initSDK(this);

        getData();
        initView();

        OtherUtil.getLocation(ReleaseOkActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalData.BROADCAST_COUNTER_ACTION);
        reciver = new MyBroadcastReciver();
        this.registerReceiver(reciver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (!mIsUnRegister) {
            this.unregisterReceiver(reciver);
        }
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    private void getData() {
        mTxtSay = (TextView) findViewById(R.id.text_say);
        mIvRoad = (ImageView) findViewById(R.id.iv_road);
        mBtnPlay = (Button) findViewById(R.id.button_play);
        Bitmap bmp = null;
        mEvenType = getIntent().getIntExtra("even", 0);
        mType = getIntent().getIntExtra("type", 0);
        String txtSay = getIntent().getStringExtra("txt");

        switch (mType) {
            case 0:
                mAudPath=txtSay;
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(txtSay);
                    mPlayer.prepare();
                    mAudTime=mPlayer.getDuration()/1000;
                    Log.i("1234", mAudTime + "");
                } catch (IOException e) {
                    Log.e("1234", "prepare() failed");
                }

                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPlay.setOnClickListener(click);
                break;
            case 1:
                bmp = BitmapFactory.decodeFile(txtSay);
                mImgPath=txtSay;
                mIvRoad.setImageBitmap(bmp);
//                bmp.recycle();
                break;
            case 2:
                Uri uri = Uri.parse(txtSay);
                Log.i("1234", "uri " + getPath(uri));
                mIvRoad.setVisibility(View.VISIBLE);
                mImgPath=OtherUtil.saveBitmap(getPath(uri));
                bmp = BitmapFactory.decodeFile(mImgPath);
                mIvRoad.setImageBitmap(bmp);
//                bmp.recycle();
                break;
            case 3:
                mTxtSay.setVisibility(View.VISIBLE);
                mTxt=txtSay;
                mTxtSay.setText(txtSay);
                break;
            default:
                break;
        }

    }

    private void initView() {
        mTxtLoc = (TextView) findViewById(R.id.text_me_loc);
        mShareSina = (Button) findViewById(R.id.btn_share_sina);
        mShareQQ = (Button) findViewById(R.id.btn_share_qq);
        mShareQQ.setOnClickListener(click);
        mShareSina.setOnClickListener(click);
        pb = (ProgressBar) findViewById(R.id.pb_share);
        mBtnUpload = (Button) findViewById(R.id.btn_re_ok_send);
        mBtnUpload.setOnClickListener(click);
        mBtnCanel = (Button) findViewById(R.id.btn_re_ok_canel);
        mBtnCanel.setOnClickListener(click);
    }

    private void upload() {
        String place;
        Intent intent=new Intent(ReleaseOkActivity.this, UpdataService.class);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);
        intent.putExtra("spd",spd);
        if(!OtherUtil.isNullOrEmpty(address[0])){
        place=address[0]+address[1]+address[2];
        }else{
            place="";
        }
        intent.putExtra("place",place);
        intent.putExtra("mark",mEvenType);
        intent.putExtra("aud",mAudPath);
        intent.putExtra("aud_t",mAudTime);
        intent.putExtra("img",mImgPath);
        intent.putExtra("txt",mTxt);
        startService(intent);


        ShareParams p = new ShareParams();
        p.text = "#听车发路况# 我在" + address[2] + "用听车说：“" + mTxtSay.getText() + "”";
        if (isShareSina) {
            mSWeibo.setWeiboActionListener(weiboListener);
            mSWeibo.share(p);
        }
        if (isShareQQ) {
            mQWeibo.setWeiboActionListener(weiboListener);
            mQWeibo.share(p);
        }

    }

    private String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor
                .getString(actual_image_column_index);
        File file = new File(img_path);
        return img_path;
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getWindowPix() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        // int heightPixels = dm.heightPixels;
        // float density = dm.density;
        return widthPixels;
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
                        address = OtherUtil.getDesc(ReleaseOkActivity.this,
                                lat, lng);
                        handler.sendEmptyMessage(0);
                        super.run();
                    }
                }.start();

            }

        }

    }
}
