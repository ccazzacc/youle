package com.youle.managerUi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.AsyncLoader;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.info.MainInfo;
import com.youle.util.GlobalData;
import com.youle.util.MD5;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.XListView;
import com.youle.view.XListView.IXListViewListener;

public class MainActivity extends StatActivity implements IXListViewListener,
OnClickListener{
	private XListView msgLv;
	private Context ct;
	private List<MainInfo> list;
	private FinalBitmap fb;
	// private ImageLoader imageLoader = ImageLoader.getInstance();
	// private View hView;
	private Button btn_quan, btn_record;
	// private ImageView ivMava, ivCover;
	private Handler mHandler;
	private MediaPlayer mediaPlay;
	private int page = 1, loadM = 0, refresh = 0;
	private boolean isFirst = true;
	private MsgAdapter adapter;
	private GestureDetector detector;
	@SuppressWarnings("deprecation")
	private void initView()
	{
		msgLv = (XListView) findViewById(R.id.me_center_listview);
		msgLv.setPullLoadEnable(true);
		btn_quan = (Button) findViewById(R.id.me_center_tab_quan);
		btn_record = (Button) findViewById(R.id.me_center_tab_record);
		list = new ArrayList<MainInfo>();
		msgLv.setXListViewListener(this);
		btn_quan.setOnClickListener(this);
		btn_record.setOnClickListener(this);
		geneItems(page, 0);
		mHandler = new Handler();
		detector = new GestureDetector(new OnGestureListener() {

			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				try {
					if (Math.abs(velocityX) > ViewConfiguration
							.get(MainActivity.this).getScaledMinimumFlingVelocity()) {
						if (e1.getX() - e2.getX() > 200 && Math.abs(velocityX) > 10) {
							SlidActivity.showMenu();
						} else if (e2.getX() - e1.getX() > 200
								&& Math.abs(velocityX) > 10) {
							SlidActivity.showMenu();
						}
					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}

			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		msgLv.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				detector.onTouchEvent(event);
				return false;
			}
		});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_activity);
		ct = this;
		fb = FinalBitmap.create(ct);
		initView();
		Log.i("test", "MainActivity onCreate");
	}

	// url:http://218.6.224.55:8000/tracks?radio_id=1&distance=3000&lng=104.022&lat=30.6889&size=30

		private class GetInfoTask extends AsyncTask<Integer, Void, String> {
			private List<MainInfo> mList;

			@Override
			protected String doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				Log.i("test", "page:" + params[0] + " type:" + params[1]);
				String res = YouLe.getInfoList(ct, 0, 0, 0, params[0], 30);
				if (res.startsWith(GlobalData.RESULT_OK)) {
					mList = YouLe.jsonInfo(res);
					return GlobalData.RESULT_OK + params[1];
				} else
					return res;
			}

			// http://218.6.224.55:8000/tracks?radio_id=1&distance=3000&lng=104.022&lat=30.6889&page=2&size=30
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (isFirst)
					CustomProgressDialog.stopProgressDialog(ct);
				isFirst = false;
				if (!OtherUtil.isNullOrEmpty(result)
						&& result.startsWith(GlobalData.RESULT_OK)) {
					// list = mList;
					// msgLv.setAdapter(adapter);
					if (result.substring(3).equals("2") && list != null
							&& list.size() > 0) {
						list.clear();
						list.addAll(mList);
					} else {
						if (mList != null && mList.size() != 0) {
							list.addAll(mList);
						}
					}
					if (result.substring(3).equals("0")) {// 第一次加载
						adapter = new MsgAdapter(ct);
						msgLv.setAdapter(adapter);
					} else if (result.substring(3).equals("1")
							|| result.substring(3).equals("2")) {
						try {
							msgLv.requestLayout();
							adapter.notifyDataSetChanged();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(null == mList || mList.size() == 0)
							ToastUtil.show(ct, R.string.please_rest);
						msgLv.stopLoadMore();
					}
				} else {
					ToastUtil.showToast(ct, result);
				}
				onLoad();
				loadM = 0;
				refresh = 0;
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if (isFirst)
					CustomProgressDialog.showMsg(ct,
							getString(R.string.please_wait));
					
			}

		}
//07-08 13:41:01.218: I/test(8799): url:http://119.15.136.126:8000/coupons?map=0&radio_id=1&distance=8000&lng=104.022&lat=30.6889

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
					convertView = (RelativeLayout) View.inflate(context,
							R.layout.msg_item_listview, null);
					vc = new ViewCache();
					vc.ivAva = (ImageView) convertView
							.findViewById(R.id.msg_iv_ava);
					vc.tvChat = (TextView) convertView
							.findViewById(R.id.msg_tv_chat);
					vc.tvName = (TextView) convertView
							.findViewById(R.id.msg_tvName);
					vc.ivPhoto = (ImageView) convertView
							.findViewById(R.id.msg_iv_photo);
					vc.ivPhotoTop = (ImageView) convertView
							.findViewById(R.id.msg_iv_photoTop);
					vc.tvTime = (TextView) convertView
							.findViewById(R.id.msg_tv_time);
					vc.tvAdd = (TextView) convertView
							.findViewById(R.id.msg_tv_address);
					vc.ivStatus = (ImageView) convertView
							.findViewById(R.id.msg_iv_status);
					vc.sAnim = (ImageView) convertView
							.findViewById(R.id.msg_iv_sAnim);
					vc.sTime = (TextView) convertView
							.findViewById(R.id.msg_tv_sTime);
					vc.lSound = (LinearLayout) convertView
							.findViewById(R.id.msg_lout_sound);
					vc.fPho = (FrameLayout) convertView
							.findViewById(R.id.msg_ly_chat);
					vc.ivClass = (ImageView) convertView
							.findViewById(R.id.msg_iv_class);
					vc.tvClass = (TextView) convertView
							.findViewById(R.id.msg_tv_class);
					convertView.setTag(vc);
				} else {
					vc = (ViewCache) convertView.getTag();
				}
				final MainInfo info = list.get(position);
				fb.display(vc.ivAva, info.getAvaUrl());
				vc.ivAva.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!OtherUtil.isNullOrEmpty(info.getUserId())
								&& info.getUserId().equals(Utility.mSession.getUserId()))
						{
							SlipMainCenter.slidIntent(2);
						}else
						{
							Intent it = new Intent(ct, MeActivity.class);
							it.putExtra(GlobalData.U_ID, info.getUserId());
							startActivity(it);
							MainActivity.this
									.overridePendingTransition(R.anim.push_left_in,
											R.anim.push_left_out);
						}
					}
				});
				vc.tvName.setText(info.getUserName());
				switch (info.getUtype()) {
				case 2:
					vc.ivClass.setVisibility(View.VISIBLE);
					vc.tvClass.setVisibility(View.VISIBLE);
					vc.ivClass.setBackgroundResource(R.drawable.taxi_s);
					vc.tvClass.setText(R.string.taxi);
					break;
				case 3:
					vc.ivClass.setVisibility(View.VISIBLE);
					vc.tvClass.setVisibility(View.VISIBLE);
					vc.ivClass.setBackgroundResource(R.drawable.radio_s);
					vc.tvClass.setText(R.string.radio);
					break;
				default:
					vc.ivClass.setVisibility(View.GONE);
					vc.tvClass.setVisibility(View.GONE);
					break;
				}
				// imageLoader.displayImage(info.getAvaUrl(), vc.ivAva);
				if (TextUtils.isEmpty(info.getAudUrl())
						&& TextUtils.isEmpty(info.getImgUrl())
						&& TextUtils.isEmpty(info.getText())) {
					vc.tvChat.setVisibility(View.VISIBLE);
					vc.tvChat.setText(getString(R.string.noThing));
					vc.fPho.setVisibility(View.GONE);
					vc.lSound.setVisibility(View.GONE);
				} else {
					if (!TextUtils.isEmpty(info.getAudUrl())) {
						vc.lSound.setVisibility(View.VISIBLE);
						vc.sAnim.setBackgroundResource(R.anim.sound_anim);
						vc.sTime.setText(info.getAudTime() + "s");
						final String path = asyncLoader.loadMedia(info.getAudUrl(),
								new AsyncLoader.ImageCallback() {
									@Override
									public String mediaLoaded(String mediaUrl) {
										// TODO Auto-generated method stub
										return mediaUrl;
									}
								});
						if (mediaPlay != null && mediaPlay.isPlaying()
								&& !animationDrawable.isRunning()) {
							animationDrawable = (AnimationDrawable) vc.sAnim
									.getBackground();
							animationDrawable.start();
						}
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
											if ((mediaPlay != null && mediaPlay
													.isPlaying()) == true)
												x++;
										}
									}
								}
								if (x % 2 == 0) {
									if ((mediaPlay != null && mediaPlay.isPlaying()) == true) {
										mediaPlay.stop();
									}
									mediaPlay = null;
									if (animationDrawable.isRunning())
										animationDrawable.stop();
									return;
								}
								if ((mediaPlay != null && mediaPlay.isPlaying()) == true) {
									mediaPlay.stop();
									mediaPlay = null;
									if (animationDrawable.isRunning())
										animationDrawable.stop();
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
									if (!animationDrawable.isRunning())
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
						vc.tvChat.setVisibility(View.VISIBLE);
						vc.tvChat.setText(info.getText());
						// vc.tvChat.setBackgroundResource(R.drawable.event_bg);
						// vc.ivPhoto.setVisibility(View.GONE);
						// vc.ivPhotoTop.setVisibility(View.GONE);
					} else {
						vc.tvChat.setVisibility(View.GONE);
					}
					if (!TextUtils.isEmpty(info.getImgUrl())) {
						vc.fPho.setVisibility(View.VISIBLE);
						LayoutParams para;
						para = (LayoutParams) vc.ivPhotoTop.getLayoutParams();
						if (info.getWidth() > info.getHeight()) {
							para.height = 200;
							para.width = 270;
						} else {
							para.height = 270;
							para.width = 200;
						}
						vc.ivPhoto.setLayoutParams(para);
						fb.display(vc.ivPhoto, info.getImgUrl());
						// imageLoader.displayImage(info.getImgUrl(), vc.ivPhoto);
						// vc.ivPhotoTop.setVisibility(View.VISIBLE);
						vc.tvChat.setVisibility(View.GONE);
						vc.ivPhoto.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent it = new Intent(context,
										ShowLargeActivity.class);
								it.putExtra(GlobalData.IMAGE_URI, info.getImgUrl());
								context.startActivity(it);
								MainActivity.this.overridePendingTransition(
										R.anim.push_left_in, R.anim.push_left_out);
							}
						});
					} else {
						vc.fPho.setVisibility(View.GONE);
					}
					// LayoutParams para;
					// para = (LayoutParams) vc.ivPhotoTop.getLayoutParams();
					//
					// para.height = 200;
					// para.width = 270;
					// vc.ivPhotoTop.setLayoutParams(para);
				}

				// long currentTime=System.currentTimeMillis()/1000;
				// String s=formatDuring(currentTime-info.getCreated());
				// Log.i("1234",currentTime+" "+info.getCreated()+" "+s);

				vc.tvTime.setText(info.getCreated());
				switch (info.getMark()) {
				case 1:
					vc.ivStatus.setBackgroundResource(R.drawable.crowd_normal_ns);
					break;
				case 2:
					vc.ivStatus
							.setBackgroundResource(R.drawable.accident_normal_ns);
					break;
				case 3:
					vc.ivStatus.setBackgroundResource(R.drawable.danger_normal_ns);
					break;
				case 4:
					vc.ivStatus.setBackgroundResource(R.drawable.road_normal_ns);
					break;
				case 5:
					vc.ivStatus.setBackgroundResource(R.drawable.often_normal_ns);
					break;
				case 6:
					vc.ivStatus.setBackgroundResource(R.drawable.webcam_normal_ns);
					break;
				}
				vc.tvAdd.setText(info.getPlace());

				return convertView;
			}

			private class ViewCache {
				ImageView ivAva;
				TextView tvChat;
				TextView tvName;
				ImageView ivPhoto;
				ImageView ivPhotoTop;
				FrameLayout fPho;
				TextView tvTime;
				ImageView ivStatus;
				TextView tvAdd;
				ImageView sAnim;
				TextView sTime;
				LinearLayout lSound;
				ImageView ivClass;
				TextView tvClass;
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
						((ImageView) msg.obj)
								.setBackgroundResource(R.drawable.sound3);
					}
				}

			};
		}

		private void geneItems(int page, int type) {
			if (OtherUtil.is3gWifi(ct)) {
				new GetInfoTask().execute(page, type);
			} else {
				onLoad();
				ToastUtil.show(ct, R.string.please_check_net);
			}

		}

		private void onLoad() {
			msgLv.stopRefresh();
			msgLv.stopLoadMore();
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!OtherUtil.is3gWifi(ct))
				ToastUtil.show(ct, R.string.net_no);
			else
				switch (v.getId()) {
				case R.id.me_center_tab_quan:
					startActivity(new Intent(ct,
							CouponMapActivity.class));
					MainActivity.this.overridePendingTransition(
							R.anim.push_left_in, R.anim.push_left_out);
					break;
				case R.id.me_center_tab_record:
					startActivity(new Intent(ct,
							ReleaseActivity.class));
					break;
				case R.id.header_right:
					SlipMainCenter.slidIntent(0);
//					MainActivity.this.finish();
					break;
				case R.id.header_ly_bottom:
					startActivity(new Intent(ct, ChooseCity.class));
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
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			detector.onTouchEvent(event);
			return true;
		}
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			fb.onResume();
			Log.i("test", "MainActivity onResume");
			SharedPref sharedPref = new SharedPref(ct);
			SlipMainCenter.lyButtom.setVisibility(View.VISIBLE);
			if (!OtherUtil.isNullOrEmpty(sharedPref.getCity()))
				SlipMainCenter.tvSubName.setText(sharedPref.getCity());
			if(!OtherUtil.isNullOrEmpty(sharedPref.getRadioName()))
				SlipMainCenter.tvName.setText(sharedPref.getRadioName());
			SlipMainCenter.lyButtom.setOnClickListener(this);
			SlipMainCenter.btnRight.setBackgroundResource(R.drawable.bar_button_map_normal);
			SlipMainCenter.btnRight.setOnClickListener(this);
			SlipMainCenter.btnMsgtop.setVisibility(View.GONE);
		}

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			if (refresh == 1 || loadM == 1) {
				return;
			}
			refresh = 1;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (list != null && list.size() > 0)
						geneItems(1, 2);
					else {
						geneItems(1, 1);
					}
				}
			}, 2000);
		}

		@Override
		public void onLoadMore() {
			// TODO Auto-generated method stub
			if (loadM == 1 || refresh == 1) {
				return;
			}
			loadM = 1;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (list != null && list.size() > 0) {
						geneItems(++page, 1);
					} else {
						geneItems(1, 1);
					}

				}
			}, 2000);
		}

	}
