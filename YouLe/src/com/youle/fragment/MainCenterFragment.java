package com.youle.fragment;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerUi.*;
import com.youle.util.GlobalData;
import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.youle.http_helper.YouLe;
import com.youle.managerData.info.MainInfo;
import com.youle.managerUi.ChooseCity;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.ReleaseActivity;
import com.youle.managerUi.SlidingActivity;
import com.youle.util.MD5;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.XListView;
import com.youle.view.XListView.IXListViewListener;

public class MainCenterFragment extends Fragment implements IXListViewListener,
		OnClickListener{
	private XListView msgLv;
	private Context ct;
	private List<MainInfo> list;
	private FinalBitmap fb;
	// private ImageLoader imageLoader = ImageLoader.getInstance();
//	private View hView;

	private Button btn_left, btn_quan, btn_record, btn_map;
//	private ImageView ivMava, ivCover;
	private Handler mHandler;
	private TextView mCity;
	private MediaPlayer mediaPlay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// Log.i("test", "Center onCreateView");
		ct = (SlidingActivity) getActivity();
		fb = FinalBitmap.create(ct);
		fb.onResume();
		// OtherUtil.initImageLoader(ct);
		View v = inflater
				.inflate(R.layout.main_center_layout, container, false);
		btn_left = (Button) v.findViewById(R.id.center_tab_menu);
		((ImageView) v.findViewById(R.id.header_iv_bottom))
				.setVisibility(View.VISIBLE);
		msgLv = (XListView) v.findViewById(R.id.center_listview);

//		hView = LayoutInflater.from(ct).inflate(
//				R.layout.msg_item_header_listview, null);
//		ivMava = (ImageView) hView.findViewById(R.id.msg_iv_Head_ava);
//		ivCover = (ImageView) hView.findViewById(R.id.msg_header_ivPhoto);
		btn_quan = (Button) v.findViewById(R.id.center_tab_quan);
		btn_record = (Button) v.findViewById(R.id.center_tab_record);
		btn_map = (Button) v.findViewById(R.id.header_right);
		mCity = (TextView) v.findViewById(R.id.header_tv_bottom);
		SharedPref sharedPref = new SharedPref(ct);
		if (!OtherUtil.isNullOrEmpty(sharedPref.getCity())) {
			mCity.setText(sharedPref.getCity());
		}
		LinearLayout linearLayout = (LinearLayout) v
				.findViewById(R.id.header_all);
		linearLayout.setOnClickListener(this);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// Log.i("test", "Center onActivityCreated");
		mHandler = new Handler();
		msgLv.setPullLoadEnable(true);
		btn_left.setOnClickListener(this);
		btn_quan.setOnClickListener(this);
		btn_record.setOnClickListener(this);
		btn_map.setOnClickListener(this);
		list = new ArrayList<MainInfo>();
		new GetInfoTask().execute();
//		fb.display(ivMava, "http://api.pathtrip.com/avatars/128/1000108.png");
		// imageLoader.displayImage("http://api.pathtrip.com/avatars/128/1000108.png",
		// ivMava);
//		ivCover.setBackgroundResource(R.drawable.photo);
//		msgLv.addHeaderView(hView);
	}
	private class GetInfoTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getInfoList(ct, 0, 0, 0,  10);
			if(res.startsWith(GlobalData.RESULT_OK))
			{
				list = YouLe.jsonInfo(res);
				return GlobalData.RESULT_OK;
			}else
				return res;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!OtherUtil.isNullOrEmpty(result) && result.startsWith(GlobalData.RESULT_OK))
			{
				MsgAdapter adapter = new MsgAdapter(ct);
				msgLv.setAdapter(adapter);
			}else
			{
				ToastUtil.showToast(ct, result);
			}
				
		}
		
	}
	private class MsgAdapter extends BaseAdapter {
		private Context context;
		private AsyncLoader asyncLoader;
		private AnimationDrawable animationDrawable;
		private List<Integer> numList = new ArrayList<Integer>();

		public MsgAdapter(Context context) {
			this.context = context;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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
				vc.sAnim = (ImageView) convertView
						.findViewById(R.id.msg_iv_sAnim);
				vc.sTime = (TextView) convertView
						.findViewById(R.id.msg_tv_sTime);
				vc.lSound = (LinearLayout) convertView
						.findViewById(R.id.msg_lout_sound);
				vc.lyChat = (LinearLayout) convertView
						.findViewById(R.id.msg_ltv_chat);
				convertView.setTag(vc);
			} else {
				vc = (ViewCache) convertView.getTag();
			}
			vc.ivLine.setBackgroundResource(R.drawable.line);
			final MainInfo info = list.get(position);
			fb.display(vc.ivAva, info.getAvaUrl());
			// imageLoader.displayImage(info.getAvaUrl(), vc.ivAva);
			if(TextUtils.isEmpty(info.getAudUrl()) && TextUtils.isEmpty(info.getImgUrl()) && TextUtils.isEmpty(info.getText()))
			{
				vc.lyChat.setVisibility(View.VISIBLE);
				vc.tvChat.setVisibility(View.VISIBLE);
				vc.tvChat.setText(getString(R.string.noThing));
				vc.ivPhotoTop.setVisibility(View.GONE);
			}else
			{
				if (!TextUtils.isEmpty(info.getAudUrl())) {
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
					vc.lSound.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							// TODO Auto-generated method stub
							numList.add(position);
							int x = 1;
							if (null != numList && numList.size() >= 2) {
								for (int i = numList.size() - 1, j = 0; i >= j
										&& i > 2; i--) {
									if (numList.get(i) == numList.get(i - 1)) {
										x++;
									}
								}
							}
							if (x % 2 == 0) {
								if ((mediaPlay != null && mediaPlay.isPlaying()) == true) {
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
										+ uri.substring(uri.lastIndexOf("."));
								sound = OtherUtil.fileCreate(name, true);
							} else {
								sound = new File(path);
							}

							if (sound.exists()) {
								vc.sAnim.setBackgroundResource(R.anim.sound_anim);
								animationDrawable = (AnimationDrawable) vc.sAnim
										.getBackground();
								animationDrawable.start();
								try {
									if (mediaPlay == null)
										mediaPlay = new MediaPlayer();
									mediaPlay.reset();
									mediaPlay.setDataSource(sound.getAbsolutePath());
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
					// vc.tvChat.setBackgroundResource(R.drawable.event_bg);
					// vc.ivPhoto.setVisibility(View.GONE);
					// vc.ivPhotoTop.setVisibility(View.GONE);
				} else {
					vc.lyChat.setVisibility(View.GONE);
				}
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
					// imageLoader.displayImage(info.getImgUrl(), vc.ivPhoto);
					vc.ivPhotoTop.setVisibility(View.VISIBLE);
					vc.tvChat.setVisibility(View.GONE);
				} else {
					vc.ivPhoto.setVisibility(View.GONE);
					vc.ivPhotoTop.setVisibility(View.GONE);
				}
//				LayoutParams para;
//				para = (LayoutParams) vc.ivPhotoTop.getLayoutParams();
//
//				para.height = 200;
//				para.width = 270;
//				vc.ivPhotoTop.setLayoutParams(para);
			}

            long currentTime=System.currentTimeMillis()/1000;
            String s=formatDuring(currentTime-info.getCreated());
//            Log.i("1234",currentTime+" "+info.getCreated()+" "+s);

            vc.tvTime.setText(currentTime-info.getCreated()+"");
			switch (info.getMark()) {
			case 1:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_trafficjam);
				break;
			case 2:
				vc.ivStatus.setBackgroundResource(R.drawable.event_status_event);
				break;
			case 3:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_danger);
				break;
			case 4:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_road);
				break;
			case 5:
				vc.ivStatus
						.setBackgroundResource(R.drawable.event_status_unimpeded);
				break;
			case 6:
				vc.ivStatus
				.setBackgroundResource(R.drawable.event_status_unimpeded);
				break;
			}
			vc.tvAdd.setText(info.getPlace());

			return convertView;
		}
        /**
         *
         * @param 要转换的毫秒数
         * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
         * @author fy.zhang
         */
        public String formatDuring(long mss) {
            long days = mss / ( 60 * 60 * 24);
            long hours = (mss % ( 60 * 60 * 24)) / ( 60 * 60);
            long minutes = (mss % ( 60 * 60)) / ( 60);
            long seconds = (mss % ( 60)) / 1000;
            return days + " days " + hours + " hours " + minutes + " minutes "
                    + seconds + " seconds ";
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
					if (animationDrawable.isRunning())
						animationDrawable.stop();
					((ImageView) msg.obj).setImageResource(R.drawable.sound0);
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
		case R.id.center_tab_menu:
			((SlidingActivity) ct).showLeft();
			break;

		case R.id.center_tab_quan:
			startActivity(new Intent((SlidingActivity) ct, CouponActivity.class));
			((SlidingActivity) ct).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.center_tab_record:
			startActivity(new Intent((SlidingActivity) ct,
					ReleaseActivity.class));
			break;
		case R.id.header_right:
			startActivity(new Intent((SlidingActivity) ct, MapActivity.class));
			((SlidingActivity) ct).overridePendingTransition(
					R.anim.push_left_in, R.anim.push_left_out);
			((SlidingActivity) getActivity()).finish();
			break;
		case R.id.header_all:
			startActivity(new Intent((SlidingActivity) ct, ChooseCity.class));
			break;
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// fb.onPause();
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
		// fb.onResume();
	}

}
