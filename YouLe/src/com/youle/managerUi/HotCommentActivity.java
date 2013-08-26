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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.baidu.mobstat.StatActivity;
import com.youle.R;
import com.youle.http_helper.AsyncLoader;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.info.HotComInfo;
import com.youle.util.GlobalData;
import com.youle.util.MD5;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PullUpListView;
import com.youle.view.PullUpListView.PullListViewListener;

public class HotCommentActivity extends StatActivity implements
		PullListViewListener {
	private PullUpListView lvComment;
	private List<HotComInfo> listComment = new ArrayList<HotComInfo>();
	private int mPage = 1, loadM = 0;
	private String topicId;
	private boolean isFirst = true;
	private CommentAdapter adapter;
	private MediaPlayer mediaPlay;
	private FinalBitmap fb;
	private LinearLayout lyComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_comment_activity);
		initView();
		MyApplication.getInstance().addActivity(this);
	}
	
	private void initView() {
		topicId = getIntent().getStringExtra(GlobalData.TOPIC_ID);
		fb = FinalBitmap.create(this);
		lvComment = (PullUpListView) findViewById(R.id.hot_com_listLv);
		lvComment.setPullLoadEnable(true);
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HotCommentActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		lyComment = (LinearLayout) findViewById(R.id.hot_com_ly);
		lyComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(HotCommentActivity.this,
						HotReplyActivity.class);
				it.putExtra(GlobalData.TOPIC_ID, topicId);
				startActivity(it);
				HotCommentActivity.this.finish();

			}
		});
		((Button) findViewById(R.id.twobtn_header_right))
				.setVisibility(View.INVISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.comment);
		geneItems(mPage);
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

	private class CommentAdapter extends BaseAdapter {
		private Context context;
		private AnimationDrawable animationDrawable;
		private List<Integer> numList = new ArrayList<Integer>();
		private AsyncLoader asyncLoader;

		public CommentAdapter(Context context) {
			this.context = context;
			fb.onResume();
			asyncLoader = new AsyncLoader();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listComment.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listComment.get(position);
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
			final ViewInit init;
			if (convertView == null) {
				convertView = (RelativeLayout) View.inflate(context,
						R.layout.hot_comment_item, null);
				init = new ViewInit();
				init.ivAva = (ImageView) convertView
						.findViewById(R.id.comment_ivAva);
				init.tvContent = (TextView) convertView
						.findViewById(R.id.comment_tvContent);
				init.tvName = (TextView) convertView
						.findViewById(R.id.comment_tvName);
				init.tvTime = (TextView) convertView
						.findViewById(R.id.comment_tvTime);
				init.fPho = (FrameLayout) convertView
						.findViewById(R.id.comment_ly_chat);
				init.ivPhoto = (ImageView) convertView
						.findViewById(R.id.comment_iv_photo);
				init.ivPhotoTop = (ImageView) convertView
						.findViewById(R.id.comment_iv_photoTop);
				init.sAnim = (ImageView) convertView
						.findViewById(R.id.comment_iv_sAnim);
				init.sTime = (TextView) convertView
						.findViewById(R.id.comment_tv_sTime);
				init.lSound = (LinearLayout) convertView
						.findViewById(R.id.comment_lout_sound);
				init.tvNreply = (TextView) convertView
						.findViewById(R.id.comment_tvNreply);
				init.tvRname = (TextView) convertView
						.findViewById(R.id.comment_tvRName);
				init.tvReply = (TextView) convertView
						.findViewById(R.id.comment_tvReply);
				convertView.setTag(init);
			} else
				init = (ViewInit) convertView.getTag();
			final HotComInfo info = listComment.get(position);
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
			} else if (OtherUtil.isNullOrEmpty(content)
					|| "null".equals(info.getContent())) {
				init.tvNreply.setVisibility(View.GONE);
				init.tvRname.setVisibility(View.GONE);
				init.tvContent.setVisibility(View.GONE);
			} else {
				init.tvNreply.setVisibility(View.GONE);
				init.tvRname.setVisibility(View.GONE);
				init.tvContent.setVisibility(View.VISIBLE);
				init.tvContent.setText(content);
			}
			init.tvTime.setText(info.getCreated());
			// if (OtherUtil.isNullOrEmpty(info.getImgUrl()))
			// init.ivPho.setVisibility(View.GONE);
			// else {
			// init.ivPho.setVisibility(View.VISIBLE);
			// fb.display(init.ivPho, info.getImgUrl());
			// }
			if (!TextUtils.isEmpty(info.getImgUrl())) {
				init.fPho.setVisibility(View.VISIBLE);
				LayoutParams para;
				para = (LayoutParams) init.ivPhotoTop.getLayoutParams();
				if (info.getImgWidth() > info.getImgHeight()) {
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
						Intent it = new Intent(context, ShowLargeActivity.class);
						it.putExtra(GlobalData.IMAGE_URI, info.getImgUrl());
						context.startActivity(it);
						HotCommentActivity.this.overridePendingTransition(
								R.anim.push_left_in, R.anim.push_left_out);
					}
				});
			} else {
				init.fPho.setVisibility(View.GONE);
			}
			init.tvReply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent(HotCommentActivity.this,
							HotReplyActivity.class);
					it.putExtra("replyContent", info.getUserName());
					// if(position == 0)
					// it.putExtra(GlobalData.POST_ID, listCar.get(0)
					// .getPostId());
					// else
					it.putExtra(GlobalData.TOPIC_ID, info.getTopicId());
					startActivity(it);
					HotCommentActivity.this.finish();
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
		List<HotComInfo> tList = new ArrayList<HotComInfo>();

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (isFirst)
				CustomProgressDialog
						.stopProgressDialog(HotCommentActivity.this);
			isFirst = false;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (isFirst) {
				CustomProgressDialog
						.stopProgressDialog(HotCommentActivity.this);
				adapter = new CommentAdapter(HotCommentActivity.this);
			}
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				if (tList != null) {
					if (null == listComment || listComment.size() == 0)
						listComment = tList;
					else
						listComment.addAll(tList);
				}
				if (mPage > 1 && listComment != null)
					adapter.notifyDataSetChanged();
				else if (null != listComment) {
					lvComment.setAdapter(adapter);
					lvComment.setXListViewListener(HotCommentActivity.this);
				}
			} else
				ToastUtil.showToast(HotCommentActivity.this, result);
			isFirst = false;
			onLoad();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (isFirst)
				CustomProgressDialog.showMsg(HotCommentActivity.this,
						getString(R.string.please_wait));
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String res = YouLe.getHotComment(HotCommentActivity.this, topicId,
					params[0]);
			if (!OtherUtil.isNullOrEmpty(res)
					&& res.startsWith(GlobalData.RESULT_OK)) {
				tList = YouLe.jsonComment(res.substring(3));
				return GlobalData.RESULT_OK;
			}
			return res;
		}

	}

	private void geneItems(int page) {
		if (OtherUtil.is3gWifi(HotCommentActivity.this)) {
			new GetReTask().execute(page);
		} else {
			onLoad();
			ToastUtil.show(HotCommentActivity.this, R.string.net_no);
		}
	}

	private void onLoad() {
		lvComment.stopRefresh();
		lvComment.stopLoadMore();
		loadM = 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			HotCommentActivity.this.finish();
		}
		return true;
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadM++;
		if (loadM == 1) {
			if (listComment != null && listComment.size() > 0) {
				mPage++;
				geneItems(mPage);
			}
		}
	}

}
