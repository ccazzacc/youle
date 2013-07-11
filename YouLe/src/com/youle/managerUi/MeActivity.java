package com.youle.managerUi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.AsyncLoader;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.info.MainInfo;
import com.youle.managerData.info.MeInfo;
import com.youle.util.GlobalData;
import com.youle.util.ImageUtil;
import com.youle.util.MD5;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PicPopu;
import com.youle.view.XListView;
import com.youle.view.XListView.IXListViewListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeActivity extends StatActivity implements IXListViewListener,
		OnClickListener {
	private XListView msgLv;
	private Context ct;
	private List<MainInfo> list;
	private FinalBitmap fb;
	private View hView;
	//
	private Button btn_left,btn_quan, btn_record;
	private TextView tv_uName,tvHname, tvLevel, tvPoint, tvClass, tvNoInfo;
	private ImageView ivMava, ivClass;
	private LinearLayout lyMe;
	// private int progress = 0;
	private Handler mHandler;
	private MediaPlayer mediaPlay;
	private PicPopu pop;
	private File tempFile = OtherUtil.fileCreate(OtherUtil.getFileName()
			+ ".jpg", false);
	private ImageUtil imageUtil;
	private Bitmap photo;
	private String uId;
	private int page = 1, loadM = 0, refresh = 0;
	private boolean isFirst = true;
	private MsgAdapter adapter;
	private GestureDetector detector;

	@SuppressWarnings("deprecation")
	private void initView() {
		lyMe = (LinearLayout)findViewById(R.id.me_actLay);
		msgLv = (XListView) findViewById(R.id.me_center_listview);
		hView = LayoutInflater.from(ct).inflate(
				R.layout.msg_item_header_listview, null);
		ivMava = (ImageView) hView.findViewById(R.id.msg_iv_Head_ava);
		tvHname = (TextView) hView.findViewById(R.id.msg_tv_hUname);
		tvLevel = (TextView) hView.findViewById(R.id.msg_tv_hLevel);
		tvPoint = (TextView) hView.findViewById(R.id.msg_tv_hJinyan);
		tvClass = (TextView) hView.findViewById(R.id.msg_tv_hClass);
		ivClass = (ImageView) hView.findViewById(R.id.msg_iv_hClass);
		btn_quan = (Button) findViewById(R.id.me_center_tab_quan);
		btn_record = (Button) findViewById(R.id.me_center_tab_record);
		tvNoInfo = (TextView) findViewById(R.id.me_center_tv);
		uId = this.getIntent().getStringExtra(GlobalData.U_ID);
		btn_left = (Button) findViewById(R.id.twobtn_header_left);
		tv_uName = (TextView)findViewById(R.id.twobtn_header_tv);
		btn_left.setBackgroundResource(R.drawable.bar_button_navigation_normal);
		btn_left.setVisibility(View.VISIBLE);
		btn_left.setOnClickListener(this);
		imageUtil = new ImageUtil(ct);
		mHandler = new Handler();
		msgLv.setPullLoadEnable(true);
		btn_quan.setOnClickListener(this);
		btn_record.setOnClickListener(this);
		list = new ArrayList<MainInfo>();
		msgLv.setXListViewListener(this);
		fb.onResume();
		if (!OtherUtil.isNullOrEmpty(uId)
				&& uId.equals(Utility.mSession.getUserId())) {
			SlipMainCenter.lyButtom.setVisibility(View.GONE);
//			ivMava.setOnClickListener(this);
			// ivCover.setOnClickListener(this);
			SlipMainCenter.btnRight
					.setBackgroundResource(R.drawable.bar_button_setup_normal);
			SlipMainCenter.btnRight.setVisibility(View.VISIBLE);
			SlipMainCenter.btnRight.setOnClickListener(this);
			SlipMainCenter.btnMsgtop.setVisibility(View.GONE);
		} else {
			SlipMainCenter.btnRight.setVisibility(View.INVISIBLE);
			lyMe.setVisibility(View.VISIBLE);
		}
		if (!OtherUtil.isNullOrEmpty(uId)) {
			new GetUserTask().execute();
			geneItems(page, 0);
		}
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
							.get(MeActivity.this).getScaledMinimumFlingVelocity()) {
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
	}

	private class GetUserTask extends AsyncTask<Void, Void, String> {
		private MeInfo info;

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getUserInfo(ct, uId);
			if (res.startsWith(GlobalData.RESULT_OK)) {
				// if(uId.equals(Utility.mToken.getUser_id()))
				// {
				// info = YouLe.jsonUserInfo(res.substring(3),true);
				// }else
				info = YouLe.jsonUserInfo(res.substring(3), false);
				return GlobalData.RESULT_OK;
			} else
				return res;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog.stopProgressDialog(ct);
			Log.i("test", "info==null?" + (info == null));
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK) && null != info) {
				if (!OtherUtil.isNullOrEmpty(uId)
						&& uId.equals(Utility.mSession.getUserId())) 
					SlipMainCenter.tvName.setText(info.getUserName());
				else
					tv_uName.setText(info.getUserName());
				tvHname.setText(info.getUserName());
				fb.display(ivMava, info.getAvaUrl());
				String point = getResources().getString(R.string.points);
				tvPoint.setText(String.format(point, info.getPoints()));
				String level = getResources().getString(R.string.level);
				tvLevel.setText(String.format(level, info.getLevel()));
				switch (info.getType()) {
				case 2:
					ivClass.setVisibility(View.VISIBLE);
					tvClass.setVisibility(View.VISIBLE);
					ivClass.setBackgroundResource(R.drawable.taxi_s);
					tvClass.setText(R.string.taxi);
					break;
				case 3:
					ivClass.setVisibility(View.VISIBLE);
					tvClass.setVisibility(View.VISIBLE);
					ivClass.setBackgroundResource(R.drawable.radio_s);
					tvClass.setText(R.string.radio);
					break;
				default:
					ivClass.setVisibility(View.GONE);
					tvClass.setVisibility(View.GONE);
					break;
				}
			} else {
				ToastUtil.showToast(ct, result);
			}

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog.stopProgressDialog(ct);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(ct, getString(R.string.please_wait));
		}

	}

	private class GetTracksTask extends AsyncTask<Integer, Void, String> {
		private List<MainInfo> mList;

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getUserTracks(ct, uId, params[0]);
			if (res.startsWith(GlobalData.RESULT_OK)) {
				mList = YouLe.jsonInfo(res);
				return GlobalData.RESULT_OK + params[1];
			} else
				return res;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (isFirst) {
				CustomProgressDialog.stopProgressDialog(ct);
				adapter = new MsgAdapter(ct);
				msgLv.addHeaderView(hView);
			}
			isFirst = false;
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				// list = mList;
				// if (null == list || list.size() == 0)
				// tvNoInfo.setVisibility(View.VISIBLE);
				// else
				// tvNoInfo.setVisibility(View.GONE);
				// MsgAdapter adapter = new MsgAdapter(ct);
				// msgLv.setAdapter(adapter);
				if (result.substring(3).equals("2") && list != null
						&& list.size() > 0) {
				} else {
					if (mList != null && mList.size() != 0) {
						list.addAll(mList);
					}
				}
				if (null == list || list.size() == 0)
					tvNoInfo.setVisibility(View.VISIBLE);
				else
					tvNoInfo.setVisibility(View.GONE);
				if (result.substring(3).equals("0")) {// 第一次加载

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
					if (null == mList || mList.size() == 0)
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
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (isFirst)
				CustomProgressDialog.stopProgressDialog(ct);
			isFirst = false;
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		detector.onTouchEvent(event);
		return true;
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
				convertView.setTag(vc);
			} else {
				vc = (ViewCache) convertView.getTag();
			}
			final MainInfo info = list.get(position);
			if (null != info) {
				fb.display(vc.ivAva, info.getAvaUrl());
				vc.tvName.setText(info.getUserName());
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
						final String path = asyncLoader.loadMedia(
								info.getAudUrl(),
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
										if (numList.get(i) == numList
												.get(i - 1)) {
											if ((mediaPlay != null && mediaPlay
													.isPlaying()) == true)
												x++;
										}
									}
								}
								if (x % 2 == 0) {
									if ((mediaPlay != null && mediaPlay
											.isPlaying()) == true) {
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
											+ uri.substring(uri
													.lastIndexOf("."));
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
						// imageLoader.displayImage(info.getImgUrl(),
						// vc.ivPhoto);
						// vc.ivPhotoTop.setVisibility(View.VISIBLE);
						vc.tvChat.setVisibility(View.GONE);
						vc.ivPhoto.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent it = new Intent(context,
										ShowLargeActivity.class);
								it.putExtra(GlobalData.IMAGE_URI,
										info.getImgUrl());
								context.startActivity(it);
								MeActivity.this.overridePendingTransition(
										R.anim.push_left_in,
										R.anim.push_left_out);
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
					vc.ivStatus
							.setBackgroundResource(R.drawable.crowd_normal_ns);
					break;
				case 2:
					vc.ivStatus
							.setBackgroundResource(R.drawable.accident_normal_ns);
					break;
				case 3:
					vc.ivStatus
							.setBackgroundResource(R.drawable.danger_normal_ns);
					break;
				case 4:
					vc.ivStatus
							.setBackgroundResource(R.drawable.road_normal_ns);
					break;
				case 5:
					vc.ivStatus
							.setBackgroundResource(R.drawable.often_normal_ns);
					break;
				case 6:
					vc.ivStatus
							.setBackgroundResource(R.drawable.webcam_normal_ns);
					break;
				}
				vc.tvAdd.setText(info.getPlace());
			}

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
			new GetTracksTask().execute(page, type);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!OtherUtil.is3gWifi(ct))
			ToastUtil.show(ct, R.string.net_no);
		else
			switch (v.getId()) {
			case R.id.me_center_tab_quan:
				startActivity(new Intent(ct, CouponActivity.class));
				MeActivity.this.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				break;
			case R.id.me_center_tab_record:
				startActivity(new Intent(ct, ReleaseActivity.class));
				break;
			case R.id.msg_iv_Head_ava:
				if (null == photo) {
					pop = new PicPopu(ct, avaOnClick);
					pop.showAtLocation(
							MeActivity.this.findViewById(R.id.me_center_layout),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				}
				break;
			// case R.id.msg_header_ivPhoto:
			// if (null == photo) {
			// pop = new PicPopu(ct, coverOnClick);
			// pop.showAtLocation(((SlidingMeActivity) ct)
			// .findViewById(R.id.me_center_layout),
			// Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			// }
			// break;
			case R.id.header_right:
				startActivity(new Intent(ct, MeSetActivity.class));
				MeActivity.this.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				break;
			case R.id.twobtn_header_left:
				MeActivity.this.finish();
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fb.onResume();
	}

	// private OnClickListener coverOnClick = new OnClickListener() {
	//
	// public void onClick(View v) {
	// pop.dismiss();
	// switch (v.getId()) {
	// case R.id.btn_first:
	// if (photo != null) {
	// photo = null;
	// }
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
	// startActivityForResult(intent,
	// GlobalData.PHOTO_REQUEST_TAKEPHOTO);
	// break;
	// case R.id.btn_second:
	// if (photo != null) {
	// photo = null;
	// }
	// Intent intents = new Intent(Intent.ACTION_PICK, null);
	// intents.setDataAndType(
	// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
	// startActivityForResult(intents,
	// GlobalData.PHOTO_REQUEST_GALLERY);
	// break;
	// }
	// }
	// };
	private OnClickListener avaOnClick = new OnClickListener() {

		public void onClick(View v) {
			pop.dismiss();
			switch (v.getId()) {
			case R.id.btn_first:
				try {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 指定调用相机拍照后照片的储存路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(tempFile));
					startActivityForResult(intent,
							GlobalData.AVA_REQUEST_TAKEPHOTO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("test", "camera e:" + e.toString());
				}
				break;
			case R.id.btn_second:
				getLocalImage(GlobalData.AVA_REQUEST_GALLERY);
				break;
			}
		}
	};

	// private String myCoverPath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// isActResult = true;
		switch (requestCode) {
		// case GlobalData.PHOTO_REQUEST_TAKEPHOTO:
		// if (tempFile != null && tempFile.exists()) {
		// myCoverPath = tempFile.getAbsolutePath();
		// try {
		// ExifInterface exif = new ExifInterface(myCoverPath);
		// if ((Integer.valueOf(exif
		// .getAttribute(ExifInterface.TAG_IMAGE_WIDTH))) < 320) {
		// ToastUtil.showToast(ct, "图片小");
		// myCoverPath = null;
		// } else {
		// if (OtherUtil.is3gWifi(ct)) {
		// new UpCoverTask().execute(tempFile
		// .getAbsolutePath());
		// } else {
		// ToastUtil.showToast(ct,
		// getString(R.string.please_check_net));
		// }
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// break;
		//
		// case GlobalData.PHOTO_REQUEST_GALLERY:
		// if (data != null) {
		// Uri uri = data.getData();
		// if (uri != null) {
		// try {
		// ContentResolver resolver = ct.getContentResolver();
		// Cursor cursor = resolver.query(uri, null, null, null,
		// null);
		// if (cursor != null) {
		// int colunm_index = cursor
		// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// cursor.moveToFirst();
		// String path = cursor.getString(colunm_index);
		// cursor.close();
		// // Log.i("test", "GALLERY path:" + path);
		// if (!OtherUtil.isNullOrEmpty(path)
		// && (path.endsWith("jpg") || path
		// .endsWith("png"))) {
		// myCoverPath = path;
		// // ExifInterface exif = new ExifInterface(path);
		// // if
		// //
		// ((Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)))
		// // < 640
		// // &&
		// //
		// (Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)))
		// // < 640) {
		// // ToastUtil.showToast(
		// // ct,
		// // "图片小");
		// // myCoverPath = null;
		// // } else {
		// if (OtherUtil.is3gWifi(ct)) {
		// new UpCoverTask().execute(path);
		// } else {
		// ToastUtil
		// .showToast(
		// ct,
		// getString(R.string.please_check_net));
		// }
		// // }
		//
		// }
		// }
		//
		// } catch (Exception e) {
		//
		// }
		// }
		// }
		// break;
		case GlobalData.AVA_REQUEST_GALLERY:
			if (data != null) {
				readLocalImage(data.getData(), true);
			}
			break;
		case GlobalData.AVA_REQUEST_TAKEPHOTO:
			if (tempFile.exists()) {
				readLocalImage(Uri.fromFile(tempFile), false);
			}
			break;
		case GlobalData.REQ_CODE_PHOTO_CROP:
			if (data != null) {
				readCropImage(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void getLocalImage(int reqCode) {
		// 抓下异常是防止有的机器不支持ACTION_PICK或ACTION_GET_CONTENT的动作
		try {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, reqCode);
		} catch (Exception e1) {
			try {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, reqCode);
			} catch (Exception e2) {
				// Log.i("test", "机器不支持ACTION_GET_CONTENT " + e2.toString());
			}
		}
	}

	/**
	 * 此处写方法描述
	 * 
	 * @Title: readLocalImage
	 * @param data
	 * @return void
	 */
	private void readLocalImage(Uri uri, boolean isGallay) {
		if (uri == null) {
			return;
		}
		if (uri != null) {
			if (isGallay)
				startPhotoCrop(uri, null, GlobalData.REQ_CODE_PHOTO_CROP); // 图片裁剪
			else
				startPhotoCrop(uri, tempFile.getAbsolutePath(),
						GlobalData.REQ_CODE_PHOTO_CROP); // 照照片裁剪
		}
	}

	/**
	 * 开始裁剪
	 * 
	 * @Title: startPhotoCrop
	 * @param uri
	 * @param duplicatePath
	 * @param reqCode
	 * @return void
	 */
	private void startPhotoCrop(Uri uri, String duplicatePath, int reqCode) {
		if (null != uri && !OtherUtil.isNullOrEmpty(uri.toString())) {
			if (imageUtil == null)
				imageUtil = new ImageUtil(ct);
			Uri duplicateUri = imageUtil.preCrop(uri, duplicatePath);
			Intent intent = new Intent(GlobalData.ACTION_CROP_IMAGE);
			intent.putExtra(GlobalData.IMAGE_URI, uri);
			startActivityForResult(intent, reqCode);
		}
	}

	/**
	 * @Title: readCropImage
	 * @param data
	 * @return void
	 */
	@SuppressWarnings("deprecation")
	private void readCropImage(Intent data) {
		if (data == null) {
			return;
		}
		Uri uri = data.getParcelableExtra(GlobalData.CROP_IMAGE_URI);
		// Log.i("test", "uri=========================>" + uri.toString());
		if (OtherUtil.is3gWifi(ct)) {
			photo = imageUtil.getBitmap(uri);
			// ivMava.setImageBitmap(photo);
			new uploadTask().execute();
		} else {
			ToastUtil.show(ct, getString(R.string.please_check_net));
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (photo != null && !photo.isRecycled()) {
			photo.recycle();
			photo = null;
			System.gc();
		}
	}

	// private class UpCoverTask extends AsyncTask<String, Void, String> {
	// @Override
	// protected void onPostExecute(String result) {
	// // TODO Auto-generated method stub
	// super.onPostExecute(result);
	// if (!TextUtils.isEmpty(result) && result.startsWith("http")) {
	// // result = result.substring(0, result.lastIndexOf("?"));
	// fb.display(ivCover, result);
	// }
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// // TODO Auto-generated method stub
	// super.onPreExecute();
	// }
	//
	// @Override
	// protected String doInBackground(String... params) {
	// // TODO Auto-generated method stub
	// String url = "";
	// if (!TextUtils.isEmpty(params[0])) {
	// if (Utility.isSessionValid()) {
	// url = YouLe.upCover(ct, params[0]);
	// } else {
	// if (Utility.hasToken()) {
	// String result = YouLe.refreshToken(ct,
	// Utility.mToken.getRefresh_token());
	// if (GlobalData.RESULT_OK.equals(result)) {
	// url = YouLe.upCover(ct, params[0]);
	// }
	// }
	// }
	// }
	// if (!OtherUtil.isNullOrEmpty(url) && url.startsWith("http")) {
	// int i = url.lastIndexOf("?");
	// if (i > 0 && i < url.length()) {
	// url = url.substring(0, i);
	// }
	// fb.clearCache(url);
	// File f = new File(BitmapCommonUtils.getDiskCacheDir(ct,
	// "afinalCache"), FileNameGenerator.generator(url) + ".0");
	// if (f.exists())
	// f.delete();
	// File ff = new File(BitmapCommonUtils.getDiskCacheDir(ct,
	// "afinalCache/original"),
	// FileNameGenerator.generator(url) + ".0");
	// if (ff.exists())
	// ff.delete();
	// fb.flushCache();
	// }
	// return url;
	// }
	//
	// }

	private class uploadTask extends AsyncTask<Void, Void, String> {
		private Drawable drawable;

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!OtherUtil.isNullOrEmpty(result) && drawable != null)
				ivMava.setImageDrawable(drawable);
			else {
				ToastUtil.showToast(ct, result);
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String url = null;
			File file = OtherUtil.fileCreate("ava.jpg", false);
			String filePath = file.getAbsolutePath();
			OtherUtil.saveFile(photo, file);
			if (!TextUtils.isEmpty(filePath)) {
				if (Utility.isSessionValid()) {
					url = YouLe.upAvatar(ct, filePath);
				} else {
					if (Utility.hasToken()) {
						String result = YouLe.refreshToken(ct,
								Utility.mToken.getRefresh_token());
						if (GlobalData.RESULT_OK.equals(result)) {
							url = YouLe.upAvatar(ct, filePath);
						}
					}
				}
				if (photo != null && !photo.isRecycled()) {
					photo.recycle();
					photo = null;
					System.gc();
				}
				// 下载头像
				if (!TextUtils.isEmpty(url)) {
					if (url.startsWith("http") || url.startsWith(" http")) {
						try {
							drawable = Drawable.createFromStream(
									new URL(url).openStream(), "iamgeSync");
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
			return url;
		}

	}

}
