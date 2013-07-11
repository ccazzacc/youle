package com.youle.managerUi;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.sharesdk.framework.AbstractWeibo;
import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.service.UpdataService;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

import java.io.File;
import java.io.IOException;

public class ReleaseOkActivity extends StatActivity {
    OnClickListener click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_re_ok_send:
                    upload();
                    break;
                case R.id.button_play:
                    if(mPlayer.isPlaying()){
                        mPlayer.pause();
                        v.setBackgroundResource(R.drawable.bofang);
                    }else{
                        mPlayer.start();
                        v.setBackgroundResource(R.drawable.bofang_bfz);
                    }
                    break;
                case R.id.btn_re_ok_canel:
                    startActivity(new Intent(ReleaseOkActivity.this, SlidActivity.class));
                    ReleaseOkActivity.this.overridePendingTransition(
                            R.anim.push_right_in, R.anim.push_right_out);
                    finish();
                    break;
            }
        }
    };
    private TextView mTxtLoc, mTxtSay;
    private ImageView mIvRoad;
    private Button/* mShareSina, mShareQQ,*/ mBtnUpload, mBtnPlay, mBtnUp, mBtnCanel;
    private boolean mIsUnRegister, isShareSina, isShareQQ;
    private String[] address;
    private String mAudPath, mImgPath, mTxt;
    private int mType, mEvenType, mAudTime;
    private AbstractWeibo mSWeibo, mQWeibo;
    private ProgressBar pb;
    private MediaPlayer mPlayer;
    private double lat = 0, lng = 0;
    private float spd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_ok_activity);
        MyApplication.getInstance().addActivity(this);
        AbstractWeibo.initSDK(this);

        getData();
        initView();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    private void getData() {
        mTxtSay = (TextView) findViewById(R.id.text_say);
        mIvRoad = (ImageView) findViewById(R.id.iv_road);
        mBtnPlay = (Button) findViewById(R.id.button_play);
        ImageView imgEven = (ImageView) findViewById(R.id.reok_img_even);
        TextView txtEven = (TextView) findViewById(R.id.reok_txt_even);
        mEvenType = getIntent().getIntExtra("even", 0);
        mType = getIntent().getIntExtra("type", 0);
        String txtSay = getIntent().getStringExtra("txt");
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);
        address = getIntent().getStringArrayExtra("address");
        int[] img = {R.drawable.crowd_normal, R.drawable.accident_normal, R.drawable.danger_normal, R.drawable.road_normal, R.drawable.often_normal, R.drawable.webcam_normal};
        String[] txt = {getString(R.string.traffic), getString(R.string.accident), getString(R.string.danger), getString(R.string.roads), getString(R.string.police), getString(R.string.monitor)};
        imgEven.setBackgroundResource(img[mEvenType]);
        txtEven.setText(txt[mEvenType]);

        switch (mType) {
            case 0:
                mAudPath = txtSay;
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(txtSay);
                    mPlayer.prepare();
                    mAudTime = mPlayer.getDuration() / 1000;
                    Log.i("1234", mAudTime + "");
                } catch (IOException e) {
                    Log.e("1234", "prepare() failed");
                }

                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPlay.setOnClickListener(click);
                break;
            case 1:
                mImgPath = OtherUtil.saveBitmap(txtSay);
                Bitmap bmp = BitmapFactory.decodeFile(mImgPath);
                Bitmap bmpp = OtherUtil.toRoundCorner(bmp, 20);
                mIvRoad.setImageBitmap(bmpp);
//                bmp.recycle();
                break;
            case 2:
                Uri uri = Uri.parse(txtSay);
                Log.i("1234", "uri " + getPath(uri));
                mIvRoad.setVisibility(View.VISIBLE);
                mImgPath = OtherUtil.saveBitmap(getPath(uri));
                Bitmap bmp1 = BitmapFactory.decodeFile(mImgPath);
                Bitmap bmpb = OtherUtil.toRoundCorner(bmp1, 20);
                mIvRoad.setImageBitmap(bmpb);
//                bmp.recycle();
                break;
            case 3:
                mTxtSay.setVisibility(View.VISIBLE);
                mTxt = txtSay;
                mTxtSay.setText(txtSay);
                break;
            default:
                break;
        }
        if (GlobalData.isIn_Vehicle) {
            upload();
            finish();
        }

    }

    private void initView() {
        mTxtLoc = (TextView) findViewById(R.id.text_me_loc);
        mTxtLoc.setText(" " + /*address[0] + address[1] +*/ address[2]);
        mBtnUpload = (Button) findViewById(R.id.btn_re_ok_send);
        mBtnUpload.setOnClickListener(click);
        mBtnCanel = (Button) findViewById(R.id.btn_re_ok_canel);
        mBtnCanel.setOnClickListener(click);
    }

    private void upload() {

        String place;
        Intent intent = new Intent(ReleaseOkActivity.this, UpdataService.class);
        intent.putExtra("radio_id", new SharedPref(this).getRadioId());
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("spd", spd);
        if (!OtherUtil.isNullOrEmpty(address[0])) {
            place = address[0] + address[1] + address[2];
        } else {
            place = "";
        }
        intent.putExtra("place", place);
        intent.putExtra("mark", mEvenType + 1);
        intent.putExtra("aud", mAudPath);
        intent.putExtra("aud_t", mAudTime);
        intent.putExtra("img", mImgPath);
        intent.putExtra("txt", mTxt);
        startService(intent);
        startActivity(new Intent(ReleaseOkActivity.this,SlidActivity.class));
        finish();

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

}
