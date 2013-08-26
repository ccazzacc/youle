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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.AsyncLoader;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.info.CarReplyInfo;
import com.youle.managerData.info.CarTopicInfo;
import com.youle.util.GlobalData;
import com.youle.util.MD5;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PullUpListView;
import com.youle.view.PullUpListView.PullListViewListener;

public class CarReplyActivity extends StatActivity implements
		PullListViewListener {
	private PullUpListView lvCar;
	private List<CarReplyInfo> listCar = new ArrayList<CarReplyInfo>();
	private int mPage = 1, loadM = 0;
	private String postId, forumId;
	private boolean isFirst = true;
	private CarInfoAdapter adapter;
	private Button btnReply;
	private MediaPlayer mediaPlay;
	private FinalBitmap fb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carmain_activity);
		initView();
		MyApplication.getInstance().addActivity(this);
	}

	private void initView() {
		postId = getIntent().getStringExtra(GlobalData.POST_ID);
		forumId = getIntent().getStringExtra(GlobalData.FORUM_ID);
		fb = FinalBitmap.create(this);
		((LinearLayout) findViewById(R.id.carmain_header))
				.setVisibility(View.VISIBLE);
		lvCar = (PullUpListView) findViewById(R.id.car_mainLv);
		lvCar.setPullLoadEnable(true);
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CarReplyActivity.this,
						CarListActivity.class);
				it.putExtra(GlobalData.FORUM_ID, forumId);
				startActivity(it);
				CarReplyActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		btnReply = (Button) findViewById(R.id.twobtn_header_right);
		btnReply.setBackgroundResource(R.drawable.bar_button_reply_normal);
		btnReply.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.total);
		geneItems(mPage);
		// listCar.add(new
		// CarReplyInfo("100","我梵蒂冈豆腐干分发电公司打工是的发生的发生的发生大幅的few热污染的短发散发",
		// "http://api.pathtrip.com/avatars/128/1000101.png",
		// "",
		// "10077","test", "http://api.pathtrip.com/avatars/128/1000101.png",
		// "0", "3天前"));
		// listCar.add(new
		// CarReplyInfo("100","^我额的是啊$士大夫敢死队风格热天we古典风格的说法的发生的发生的的贵妇狗发生大",
		// "http://api.pathtrip.com/avatars/128/1000069.png",
		// "",
		// "10077","一帆风顺",
		// "http://api.pathtrip.com/covers/10003421360983697.jpg",
		// "0", "4天前"));
		// listCar.add(new
		// CarReplyInfo("100","短发散发地方都散发地方gas地方短发都是违法地方虽然法尔范大夫撒旦法的氛围",
		// "http://api.pathtrip.com/avatars/128/1000069.png",
		// "",
		// "10077","短发", "http://api.pathtrip.com/covers/10003421360983697.jpg",
		// "0", "6天前"));
		// btnReply.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent it = new Intent(CarReplyActivity.this,
		// AddPostActivity.class);
		// it.putExtra("reply",listCar.get(0).getUserName());
		// it.putExtra(GlobalData.POST_ID, listCar.get(0).getPostId());
		// startActivity(it);
		// }
		// });
		// CarInfoAdapter adapter = new CarInfoAdapter(this);
		// lvCar.setAdapter(adapter);
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
	private class CarInfoAdapter extends BaseAdapter {
		private Context context;
		private AnimationDrawable animationDrawable;
		private List<Integer> numList = new ArrayList<Integer>();
		private AsyncLoader asyncLoader;
		public CarInfoAdapter(Context context) {
			this.context = context;
			fb.onResume();
			asyncLoader = new AsyncLoader();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listCar.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listCar.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewInit init;
			if (convertView == null) {
				convertView = (RelativeLayout) View.inflate(context,
						R.layout.carreply_list_item, null);
				init = new ViewInit();
				init.ivAva = (ImageView) convertView
						.findViewById(R.id.cartotal_ivAva);
				init.tvContent = (TextView) convertView
						.findViewById(R.id.cartotal_tvContent);
				init.tvName = (TextView) convertView
						.findViewById(R.id.cartotal_tvName);
				init.tvTime = (TextView) convertView
						.findViewById(R.id.cartotal_tvTime);
				init.fPho = (FrameLayout) convertView
						.findViewById(R.id.cartotal_ly_chat);
				init.ivPhoto = (ImageView) convertView
						.findViewById(R.id.cartotal_iv_photo);
				init.ivPhotoTop = (ImageView) convertView
						.findViewById(R.id.cartotal_iv_photoTop);
				init.sAnim = (ImageView) convertView
						.findViewById(R.id.cartotal_iv_sAnim);
				init.sTime = (TextView) convertView
						.findViewById(R.id.cartotal_tv_sTime);
				init.lSound = (LinearLayout) convertView
						.findViewById(R.id.cartotal_lout_sound);
				init.tvNreply = (TextView) convertView
						.findViewById(R.id.cartotal_tvNreply);
				init.tvRname = (TextView) convertView
						.findViewById(R.id.cartotal_tvRName);
				init.ivReply = (ImageView) convertView
						.findViewById(R.id.cartotal_ivReply);
				init.tvReply = (TextView) convertView
						.findViewById(R.id.cartotal_tvReply);
				convertView.setTag(init);
			} else
				init = (ViewInit) convertView.getTag();
			final CarReplyInfo info = listCar.get(position);
			if (!TextUtils.isEmpty(info.getAudUrl())) {
				init.lSound.setVisibility(View.VISIBLE);
				init.sAnim.setBackgroundResource(R.anim.sound_anim);
				init.sTime.setText(info.getAudTime() + "s");
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
					animationDrawable = (AnimationDrawable) init.sAnim
							.getBackground();
					animationDrawable.start();
				}
				init.lSound.setOnClickListener(new OnClickListener() {

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
							init.sAnim.setBackgroundResource(R.anim.sound_anim);
							animationDrawable = (AnimationDrawable) init.sAnim
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
									msg.obj = init.sAnim;
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
				init.lSound.setVisibility(View.GONE);
			}
			
			fb.display(init.ivAva, info.getAvaUrl());
			String content = info.getContent();
			init.tvName.setText(info.getUserName());
			if (!OtherUtil.isNullOrEmpty(content) && content.startsWith("^")
					&& content.contains("$")) {
				init.tvNreply.setVisibility(View.VISIBLE);
				init.tvRname.setVisibility(View.VISIBLE);
				init.tvContent.setVisibility(View.VISIBLE);
				init.tvRname.setText(content.substring(
						content.indexOf("^") + 1, content.indexOf("$")));
				init.tvContent
						.setText(content.substring(content.indexOf("$") + 1));
			} else if (OtherUtil.isNullOrEmpty(content) || "null".equals(info.getContent())){
				init.tvNreply.setVisibility(View.GONE);
				init.tvRname.setVisibility(View.GONE);
				init.tvContent.setVisibility(View.GONE);
			}else
			{
				init.tvNreply.setVisibility(View.GONE);
				init.tvRname.setVisibility(View.GONE);
				init.tvContent.setVisibility(View.VISIBLE);
				init.tvContent.setText(content);
			}
			init.tvTime.setText(info.getCreateTime());
//			if (OtherUtil.isNullOrEmpty(info.getImgUrl()))
//				init.ivPho.setVisibility(View.GONE);
//			else {
//				init.ivPho.setVisibility(View.VISIBLE);
//				fb.display(init.ivPho, info.getImgUrl());
//			}
			if (!TextUtils.isEmpty(info.getImgUrl())) {
				init.fPho.setVisibility(View.VISIBLE);
				LayoutParams para;
				para = (LayoutParams) init.ivPhotoTop.getLayoutParams();
				if (info.getWidth() > info.getHeight()) {
					para.height = 160;
					para.width = 220;
				} else {
					para.height = 220;
					para.width = 160;
				}
				init.ivPhoto.setLayoutParams(para);
				fb.display(init.ivPhoto, info.getImgUrl());
				init.ivPhoto.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent it = new Intent(context,
								ShowLargeActivity.class);
						it.putExtra(GlobalData.IMAGE_URI, info.getImgUrl());
						context.startActivity(it);
						CarReplyActivity.this.overridePendingTransition(
								R.anim.push_left_in, R.anim.push_left_out);
					}
				});
			} else {
				init.fPho.setVisibility(View.GONE);
			}
			init.ivReply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent(CarReplyActivity.this,
							AddPostActivity.class);
					it.putExtra("post", postId);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					if(position == 0)
						it.putExtra(GlobalData.POST_ID, listCar.get(0)
								.getPostId());
					else
						it.putExtra(GlobalData.POST_ID, info.getParentId());
					it.putExtra("reply", info.getUserName());
					startActivity(it);
					CarReplyActivity.this.finish();
				}
			});
			init.tvReply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent(CarReplyActivity.this,
							AddPostActivity.class);
					it.putExtra("post", postId);
					it.putExtra(GlobalData.FORUM_ID, forumId);
					if(position == 0)
						it.putExtra(GlobalData.POST_ID, listCar.get(0)
								.getPostId());
					else
						it.putExtra(GlobalData.POST_ID, info.getParentId());
					it.putExtra("reply", info.getUserName());
					startActivity(it);
					CarReplyActivity.this.finish();
				}
			});
			return convertView;
		}

		private class ViewInit {
			ImageView ivAva;
			TextView tvContent;
			TextView tvName;
			TextView tvTime;
			FrameLayout fPho;
			ImageView ivPhoto;
			ImageView ivPhotoTop;
			ImageView sAnim;
			TextView sTime;
			LinearLayout lSound;
			TextView tvNreply;
			TextView tvRname;
			ImageView ivReply;
			TextView tvReply;
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

	private class GetReTask extends AsyncTask<Integer, Void, String> {
		List<CarReplyInfo> tList = new ArrayList<CarReplyInfo>();

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (isFirst)
				CustomProgressDialog.stopProgressDialog(CarReplyActivity.this);
			isFirst = false;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (isFirst) {
				CustomProgressDialog.stopProgressDialog(CarReplyActivity.this);
				adapter = new CarInfoAdapter(CarReplyActivity.this);
				btnReply.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent it = new Intent(CarReplyActivity.this,
								AddPostActivity.class);
						if (null != tList && tList.size() > 0) {
							it.putExtra("post", postId);
							it.putExtra(GlobalData.FORUM_ID, forumId);
							it.putExtra(GlobalData.POST_ID, postId);
							startActivity(it);
							CarReplyActivity.this.finish();
						}

					}
				});
			}
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				if (tList != null) {
					if (null == listCar || listCar.size() == 0)
						listCar = tList;
					else
						listCar.addAll(tList);
				}
				if (mPage > 1 && listCar != null)
					adapter.notifyDataSetChanged();
				else if (null != listCar) {
					lvCar.setAdapter(adapter);
					lvCar.setXListViewListener(CarReplyActivity.this);
				}
			} else
				ToastUtil.showToast(CarReplyActivity.this, result);
			isFirst = false;
			onLoad();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (isFirst)
				CustomProgressDialog.showMsg(CarReplyActivity.this,getString(R.string.please_wait));
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getReplyList(CarReplyActivity.this, postId,
					params[0]);
			if (!OtherUtil.isNullOrEmpty(res)
					&& res.startsWith(GlobalData.RESULT_OK)) {
				tList = YouLe.jsonCarReList(res.substring(3));
				return GlobalData.RESULT_OK;
			}
			return res;
		}

	}

	private void geneItems(int page) {
		if (OtherUtil.is3gWifi(CarReplyActivity.this)) {
			new GetReTask().execute(page);
		} else {
			onLoad();
			ToastUtil.show(CarReplyActivity.this, R.string.net_no);
		}
	}

	private void onLoad() {
		lvCar.stopRefresh();
		lvCar.stopLoadMore();
		loadM = 0;
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadM++;
		if (loadM == 1) {
			if (listCar != null && listCar.size() > 0) {
				mPage++;
				geneItems(mPage);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent it = new Intent(CarReplyActivity.this,
					CarListActivity.class);
			it.putExtra(GlobalData.FORUM_ID, forumId);
			startActivity(it);
			CarReplyActivity.this.finish();
		}
		return true;
	}
	
}
