package com.youle.managerUi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.koushikdutta.async.http.*;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.AsyncLoader;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.service.SystemMsgService;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CircleProgress;
import com.youle.view.XListView;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zhaofuchao on 13-7-17.
 */
public class SysPrivateMsgActivity extends Activity {
    private List<PrivateMsgInfo> mPrivateList = new ArrayList<PrivateMsgInfo>();
    private FinalBitmap fb;
    private MyAdapter myAdapter = new MyAdapter();
    private int mMyUserId;
    private String mSendImg = "", mSendaud = "", mSendTxt = "", mSendId, mFromUName, user_id;
    private File mImgFile, mAudFile;
    private XListView xListView;
    private int mSendAudT, audioVolume;
    private MediaRecorder mr;
    private long mRecordTimeEnd, mRecordTime;
    private CircleProgress mCircleProgressBar1;
    private AudioManager audioService;
    private Button mButtonXX;
    private EditText mEditInput;
    private TextView mTextView, mTxtSender;
    private TableLayout tl;
    private boolean isTxt;
    private RelativeLayout rl, rlRecord, rlInput;
    private int mPage = 1;
    private boolean mIsRefresh;
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sys_private_btn_back:
                    finish();
                    break;
                case R.id.sysmsg_btnAdd:
                    tl.setVisibility(View.VISIBLE);
                    mButtonXX.setVisibility(View.VISIBLE);
                    break;
                case R.id.sysmsg_btnQuit:
                    tl.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                    break;
                case R.id.sysmsg_send:
                    mSendTxt = mEditInput.getText().toString();
                    sendMsg();
                    mEditInput.setText("");
                    break;
                case R.id.button_camera:
                    Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImgFile));
                    startActivityForResult(i, Activity.DEFAULT_KEYS_DIALER);
                    break;
                case R.id.button_photos:
                    Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
                    innerIntent.setType("image/*"); // 查看类型
                    Intent wrapperIntent = Intent.createChooser(innerIntent, null);
                    startActivityForResult(wrapperIntent, 2);
                    break;
                case R.id.button_txt:
                    if (!isTxt) {
                        view.setBackgroundResource(R.drawable.button_recording_d);
                        rlRecord.setVisibility(View.GONE);
                        rlInput.setVisibility(View.VISIBLE);
                        mEditInput.setFocusable(true);
                        mEditInput.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        isTxt = true;
                    } else {
                        view.setBackgroundResource(R.drawable.button_text_a);
                        rlRecord.setVisibility(View.VISIBLE);
                        rlInput.setVisibility(View.GONE);
                        isTxt = false;
                    }
                    break;
            }
        }
    };
    private View.OnLongClickListener longClick = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            record();
            return false;
        }

    };
    private View.OnTouchListener touch = new View.OnTouchListener() {

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
    XListView.IXListViewListener xListViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            xListView.stopLoadMore();
            mIsRefresh = false;
            mPage++;
            getPrivateMsg(user_id);
            xListView.setSelection(0);
        }

        @Override
        public void onLoadMore() {
            xListView.stopRefresh();
            mPage = 1;
            mIsRefresh = true;
            getPrivateMsg(user_id);
            xListView.setSelection(mPrivateList.size() - 1);
        }
    };
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_private_msg_activity);
        fb = FinalBitmap.create(SysPrivateMsgActivity.this);
        fb.onResume();
        mMyUserId = Integer.parseInt(Utility.mSession.getUserId());
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1) {
                    mSendImg = OtherUtil.saveBitmap(mImgFile.getAbsolutePath());
                    sendMsg();
                } else if (requestCode == 2) {
                    mSendImg = data.getData() + "";
                    Uri uri = Uri.parse(data.getData() + "");
                    mSendImg = OtherUtil.saveBitmap(getPath(uri));
                    sendMsg();
                }

                break;
        }
    }

    private void init() {
        File path = new File(OtherUtil.getSDcard()
                + "/.cache/");
        if (!path.exists()) {
            path.mkdir();
        }
        mImgFile = new File(path, "upload.jpg");

        user_id = getIntent().getStringExtra("user_id");
        Button button_back = (Button) findViewById(R.id.sys_private_btn_back);
        button_back.setOnClickListener(click);
        Button mBtnRecord = (Button) findViewById(R.id.sysmsg_btnRecord);

        mBtnRecord.setOnTouchListener(touch);
        mBtnRecord.setOnLongClickListener(longClick);

        Button btnadd = (Button) findViewById(R.id.sysmsg_btnAdd);
        Button btnTxt = (Button) findViewById(R.id.button_txt);
        Button btnCam = (Button) findViewById(R.id.button_camera);
        Button btnPho = (Button) findViewById(R.id.button_photos);
        Button btnSend = (Button) findViewById(R.id.sysmsg_send);

        rl = (RelativeLayout) findViewById(R.id.sys_msg_rl);
        rlRecord = (RelativeLayout) findViewById(R.id.release_layout_record);
        rlInput = (RelativeLayout) findViewById(R.id.release_input);

        tl = (TableLayout) findViewById(R.id.sys_msg_tab);
        mTextView = (TextView) findViewById(R.id.sysmsg_size);
        mEditInput = (EditText) findViewById(R.id.sysmsg_edit_input);
        mEditInput.addTextChangedListener(mTextWatcher);
        mEditInput.setSelection(mEditInput.length()); // 将光标移动最后一个字符后面
        mEditInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(SysPrivateMsgActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mButtonXX = (Button) findViewById(R.id.sysmsg_btnQuit);
        btnadd.setOnClickListener(click);
        mButtonXX.setOnClickListener(click);
        btnTxt.setOnClickListener(click);
        btnCam.setOnClickListener(click);
        btnPho.setOnClickListener(click);
        btnSend.setOnClickListener(click);


        xListView = (XListView) findViewById(R.id.sys_private_listview);
        xListView.setPullLoadEnable(true);
        xListView.setAdapter(myAdapter);
        xListView.setXListViewListener(xListViewListener);
        getPrivateMsg(user_id);
        xListView.setSelection(mPrivateList.size()-1);

    }

    private void sendMsg() {
        String url = new StringBuffer().append(YouLe.BASE_URL).append("private_messages").toString();
        AsyncHttpPost post = new AsyncHttpPost(url);
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addStringPart("access_token", Utility.mToken.getAccess_token());
        body.addStringPart("uid", mSendId);
        if (!OtherUtil.isNullOrEmpty(mSendImg)) {
            body.addFilePart("img", mImgFile);
        } else {
            body.addStringPart("img", mSendImg);
        }
        if (!OtherUtil.isNullOrEmpty(mSendaud)) {
            body.addFilePart("aud", mAudFile);

        } else {
            body.addStringPart("aud", mSendaud);
        }
        body.addStringPart("txt", mSendTxt);
        body.addStringPart("aud_t", mSendAudT + "");
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().execute(post, new HttpConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, AsyncHttpResponse asyncHttpResponse) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                if (asyncHttpResponse.getHeaders().getHeaders().getResponseCode() != 200) {
                    ToastUtil.show(SysPrivateMsgActivity.this, "服务器正忙，请稍后重试");
                    return;
                }
//                getPrivateMsg(mSendId);
                int id = Integer.parseInt(mSendId);
//                Log.i("1234", asyncHttpResponse.toString() + " mSendaud: " + mAudFile.getAbsoluteFile() + " time:" + mSendAudT);
                PrivateMsgInfo msgInfo = new PrivateMsgInfo("", "", id, true, mSendaud, 0, mSendImg, mSendTxt,
                        Calendar.getInstance().getTimeInMillis() / 1000+"", mMyUserId, "", "");
                mPrivateList.add(0, msgInfo);
                myAdapter.notifyDataSetChanged();
                xListView.setSelection(mPrivateList.size());
            }
        });
    }

    private void getPrivateMsg(String id) {
        mSendId = id;
        String url = new StringBuffer().append(YouLe.BASE_URL).append("private_messages/")
                .append(id).append("?access_token=")
                .append(Utility.mToken.getAccess_token())
                .append("&page=").append(mPage)
                .append("&size=10").toString();
        Log.i("1234", url);
        AsyncHttpClient.getDefaultInstance().get(url, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, JSONObject jsonObject) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                if (mIsRefresh) {
                    mPrivateList = new ArrayList<PrivateMsgInfo>();
                }
                try {
                    String sender = "";
                    JSONArray jsonArray = jsonObject.getJSONArray("messages");
                    SystemMsgService.MSGbadge = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj1 = jsonArray.getJSONObject(i);

                        JSONObject obj2 = obj1.getJSONObject("to_user");
                        String username = obj2.getString("username");
                        String avatar_url = obj2.getString("avatar_url");
                        int user_id = obj2.getInt("user_id");

                        JSONObject obj3 = obj1.getJSONObject("from_user");
                        int suser_id = obj3.getInt("user_id");
                        String susername = obj3.getString("username");
                        String savatar_url = obj3.getString("avatar_url");

                        if (Utility.mSession.getUserId().equals(suser_id + "")) {
                            sender = username;
                        } else {
                            sender = susername;
                        }
                        boolean isRead = obj1.getBoolean("is_read");
                        String audUrl = obj1.getString("audio_url");
                        int aud_time = obj1.getInt("audio_time");
                        String imgUrl = obj1.getString("image_url");
                        String content = obj1.getString("content");
                        int msgId = obj1.getInt("message_id");
                        String date = obj1.getString("created_date");

                        PrivateMsgInfo msgInfo = new PrivateMsgInfo(username, avatar_url, user_id, isRead, audUrl, aud_time, imgUrl, content,
                                date, suser_id, susername, savatar_url);
                        mPrivateList.add(msgInfo);
                    }
                    if (mPrivateList.size() > 0 && mPrivateList != null) {
                        TextView textView = (TextView) findViewById(R.id.sys_p_text);
                        textView.setText(sender);
                        myAdapter.notifyDataSetChanged();
                        xListView.stopLoadMore();
                        xListView.stopRefresh();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
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

    private void record() {
        File path = new File(OtherUtil.getSDcard()
                + "/.cache/");
        if (!path.exists()) {
            path.mkdir();
        }
        mAudFile = new File(path, "private_msg.amr");


        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {10, 100}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);

        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mr.setOutputFile(mAudFile.getAbsolutePath());
        try {
            // 创建文件
            mAudFile.createNewFile();
            mr.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 暂停音乐
        pauseMusic();
        mr.start();
        mRecordTime = System.currentTimeMillis();
        mCircleProgressBar1 = (CircleProgress) findViewById(R.id.roundBar1);
        mCircleProgressBar1.setMainProgress(0);
        mCircleProgressBar1.startCartoom(60);
    }

    private void stopRecord() {
        mRecordTimeEnd = System.currentTimeMillis();
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
                mSendAudT = (int) ((mRecordTimeEnd - mRecordTime) / 1000);
                mSendaud = mAudFile.getAbsolutePath();
                sendMsg();
            } else {
                ToastUtil.show(SysPrivateMsgActivity.this, R.string.recorde_short);
            }
        }
    }

    private void pauseMusic() {
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

    public class MyAdapter extends BaseAdapter {
        private AnimationDrawable animationDrawable;

        @Override
        public int getCount() {
            return mPrivateList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = null;
            final ViewHolder holder;
            if (convertView == null || view == null) {
                view = getLayoutInflater().inflate(R.layout.sys_msg_item2,
                        viewGroup, false);
                holder = new ViewHolder();
                holder.left = (RelativeLayout) view.findViewById(R.id.sysmsg_item_accept);
                holder.right = (RelativeLayout) view.findViewById(R.id.sysmsg_item_send);
                holder.accTxt = (TextView) view.findViewById(R.id.sysmsg_item2_acctxt);
                holder.sendTxt = (TextView) view.findViewById(R.id.sysmsg_item2_sendtxt);
                holder.sendAud = (LinearLayout) view.findViewById(R.id.sysmsg_item2_sendaud);
                holder.accImg = (ImageView) view.findViewById(R.id.sysmsg_item2_accimg);
                holder.sendImg = (ImageView) view.findViewById(R.id.sysmsg_item2_sendimg);
                holder.accAv = (ImageView) view.findViewById(R.id.sysmsg_item2_accav);
                holder.sAnim = (ImageView) view.findViewById(R.id.msg_iv_sAnim);
                holder.audTime = (TextView) view.findViewById(R.id.sysmsg_item2_aud_time);
                holder.caeateDate = (TextView) view.findViewById(R.id.sysmsg_item2_accDate);
                holder.caeatesDate = (TextView) view.findViewById(R.id.sysmsg_item2_sendDate);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (mPrivateList != null && mPrivateList.size() > 0) {
                int ii = mPrivateList.size() - i - 1;
                final String aud_url, img_url, contents;
                aud_url = mPrivateList.get(ii).audUrl;
                img_url = mPrivateList.get(ii).imgUrl;
                contents = mPrivateList.get(ii).ftxt;
                if (mMyUserId != mPrivateList.get(ii).sender_id) {
                    //对方
                    holder.right.setVisibility(View.GONE);
                    fb.display(holder.accAv, mPrivateList.get(ii).sender_av);

                    holder.caeateDate.setText(YouLe.formatDate(Long.parseLong(mPrivateList.get(ii).date)));
                    if (!OtherUtil.isNullOrEmpty(aud_url)) {
                        holder.accTxt.setVisibility(View.GONE);
                        holder.accImg.setVisibility(View.GONE);
                        return view;
                    } else if (!OtherUtil.isNullOrEmpty(img_url)) {
                        holder.accTxt.setVisibility(View.GONE);
                        holder.accImg.setVisibility(View.VISIBLE);
                        fb.display(holder.accImg, img_url);
                        return view;
                    } else if (!OtherUtil.isNullOrEmpty(contents)) {
                        holder.accTxt.setVisibility(View.VISIBLE);
                        holder.accImg.setVisibility(View.GONE);
                        holder.accTxt.setText(contents);
                        return view;
                    }

                } else {
                    holder.left.setVisibility(View.GONE);

                    holder.caeatesDate.setText(YouLe.formatDate(Long.parseLong(mPrivateList.get(ii).date)));
                    if (!OtherUtil.isNullOrEmpty(aud_url)) {
                        holder.sendTxt.setVisibility(View.GONE);
                        holder.sendImg.setVisibility(View.GONE);
                        holder.sendAud.setVisibility(View.VISIBLE);
                        holder.audTime.setText(mPrivateList.get(ii).audTime + "s");
                        holder.sAnim.setBackgroundResource(R.anim.sound_anim);
                        holder.sendAud.setOnClickListener(new View.OnClickListener() {
                            boolean isplayed;
                            MediaPlayer mediaPlayer = new MediaPlayer();

                            @Override
                            public void onClick(View view) {
                                AsyncLoader asyncLoader = new AsyncLoader();
                                String path = asyncLoader.loadMedia(aud_url, new AsyncLoader.ImageCallback() {
                                    @Override
                                    public String mediaLoaded(String mediaUrl) {
                                        return mediaUrl;
                                    }
                                });
                                if (!OtherUtil.isNullOrEmpty(path)) {
                                    animationDrawable = (AnimationDrawable) holder.sAnim.getBackground();

                                    if (mediaPlayer.isPlaying()) {
                                        mediaPlayer.pause();
                                        animationDrawable.stop();
                                    } else {
                                        if (!isplayed) {
                                            try {
                                                mediaPlayer.setDataSource(path);
                                                mediaPlayer.prepare();
                                            } catch (IOException e) {
                                                Log.e("1234", e.getMessage());
                                            }
                                        }
                                        isplayed = true;
                                        mediaPlayer.start();
                                        animationDrawable.start();
                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                animationDrawable.stop();
//                                        iv.setBackgroundResource(R.drawable.sound0);
                                            }
                                        });
                                    }
                                }
                            }
                        });


                        return view;
                    } else if (!OtherUtil.isNullOrEmpty(img_url)) {
                        holder.sendTxt.setVisibility(View.GONE);
                        holder.sendImg.setVisibility(View.VISIBLE);
                        holder.sendAud.setVisibility(View.GONE);
                        fb.display(holder.sendImg, img_url);
                        return view;
                    } else if (!OtherUtil.isNullOrEmpty(contents)) {
                        holder.sendTxt.setVisibility(View.VISIBLE);
                        holder.sendImg.setVisibility(View.GONE);
                        holder.sendAud.setVisibility(View.GONE);
                        holder.sendTxt.setText(contents);
                        return view;
                    }
                }
            }
            http:
//119.15.136.126:8000/coupons?access_token=91052eaa1c3e5baf&map=1&radio_id=1&distance=6000&lng=104.022&lat=30.6889
            return view;
        }

        private class ViewHolder {
            public RelativeLayout left;
            public RelativeLayout right;
            public TextView accTxt;
            public TextView sendTxt;
            public LinearLayout sendAud;
            public ImageView accImg;
            public ImageView sendImg;
            public ImageView accAv;
            public ImageView sAnim;
            public TextView audTime;
            public TextView caeateDate;
            public TextView caeatesDate;

        }
    }

    private class PrivateMsgInfo {
        public String username, avUrl, audUrl, imgUrl, ftxt, date, sender_name, sender_av;
        public boolean isRead;
        public int uId, audTime, imgW, imgH, sender_id;

        public PrivateMsgInfo(String username, String avUrl, int uId, boolean isRead,
                              String audUrl, int audTime, String imgUrl, String ftxt, String date,
                              int sender_id, String sender_name, String sender_av) {
            this.username = username;
            this.avUrl = avUrl;
            this.audUrl = audUrl;
            this.imgUrl = imgUrl;
            this.ftxt = ftxt;
            this.date = date;
            this.isRead = isRead;
            this.uId = uId;
            this.audTime = audTime;
            this.sender_av = sender_av;
            this.sender_id = sender_id;
            this.sender_name = sender_name;
        }
    }
}