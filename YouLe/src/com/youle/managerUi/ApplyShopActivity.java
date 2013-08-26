package com.youle.managerUi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.search.core.AMapException;
import com.amap.api.search.geocoder.Geocoder;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.youle.R;
import com.youle.fragment.SlipMainCenter;
import com.youle.http_helper.Utility;
import com.youle.http_helper.YouLe;
import com.youle.managerData.MyApplication;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;
import com.youle.view.CustomProgressDialog;
import com.youle.view.PicPopu;

public class ApplyShopActivity extends Activity implements OnClickListener {
	// private String mApplyUrl = new
	// StringBuffer().append(YouLe.BASE_URL).append("merchants").toString();
	private EditText merchants_name, merchants_link, merchants_phone,
			merchants_address, shopLicense;
	private TextView textCity;
	private Geocoder coder;
	private String url = new StringBuffer().append(YouLe.BASE_URL)
			.append("categories/all").toString();
	private List<Categories> mList1 = new ArrayList<Categories>();
	private List<Categories> mList2 = new ArrayList<Categories>();
	private String[] mFst;
	private Button choose;
	private String mFstCategories, mSedCategories;
	private ImageView ivPic;
	private PicPopu pop;
	private Bitmap photo;
	private String latLng;
	private String categoryId;
	private String addressName;
	private File tempFile = OtherUtil.fileCreate("comment.jpg", false);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applyshop_activity);
		coder = new Geocoder(this);
		initView();
		MyApplication.getInstance().addActivity(this);
	}

	private void initView() {
		SharedPref sharedPref = new SharedPref(ApplyShopActivity.this);
		merchants_name = (EditText) findViewById(R.id.applyshop_etShop);
		merchants_link = (EditText) findViewById(R.id.applyshop_etLinkman);
		merchants_phone = (EditText) findViewById(R.id.applyshop_etPhone);
		merchants_address = (EditText) findViewById(R.id.applyshop_etAdress);
		getAddress(sharedPref.getLatLng().latitude,
				sharedPref.getLatLng().longitude);
		shopLicense = (EditText) findViewById(R.id.appshop_etLicense);
		ivPic = (ImageView) findViewById(R.id.appshop_ivPhoto);
		ivPic.setOnClickListener(this);
		textCity = (TextView) findViewById(R.id.applyshop_txtCity);
		// if (OtherUtil.isNullOrEmpty(sharedPref.getLocCity())) {
		// textCity.setText(sharedPref.getCity());
		// } else {
		textCity.setText(sharedPref.getLocCity());
		// }
		Button btnBack = (Button) findViewById(R.id.twobtn_header_left);
		btnBack.setBackgroundResource(R.drawable.bar_button_back_normal);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ApplyShopActivity.this.finish();
			}
		});
		btnBack.setVisibility(View.VISIBLE);
		TextView tvTitle = (TextView) findViewById(R.id.twobtn_header_tv);
		tvTitle.setText(R.string.apply_shop);
		choose = (Button) findViewById(R.id.choose_categories);
		Button btnCommit = (Button) findViewById(R.id.applyshop_btnCommit);
		btnCommit.setOnClickListener(this);
		choose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showCategoriesDialog();
			}
		});
		CustomProgressDialog.showMsg(ApplyShopActivity.this,
				getString(R.string.please_wait));
		getData();
	}
	
	private void getData() {
		AsyncHttpClient.getDefaultInstance().get(url,
				new AsyncHttpClient.JSONObjectCallback() {
					// Callback is invoked with any exceptions/errors, and the
					// result, if available.
					@Override
					public void onCompleted(Exception e,
							AsyncHttpResponse response, JSONObject result) {
						CustomProgressDialog
						.stopProgressDialog(ApplyShopActivity.this);
						if (e != null) {
							e.printStackTrace();
							return;
						}
						try {
							JSONArray array = result.getJSONArray("categories");
							for (int i = 0; i < array.length(); i++) {
								JSONObject obj2 = array.getJSONObject(i);
								Categories c = new Categories(obj2
										.getInt("category_id"), 0, obj2
										.getString("name"));
								mList1.add(c);
								// Log.i("1234",obj2.getString("name")+"");
								JSONArray array2 = obj2
										.getJSONArray("sub_categories");
								for (int ii = 0; ii < array2.length(); ii++) {
									JSONObject obj3 = array2.getJSONObject(ii);
									// Log.i("1234",obj2.getInt("category_id")+" "+obj3.getInt("category_id")+" "+obj3.getString("name"));
									Categories categories = new Categories(obj2
											.getInt("category_id"), obj3
											.getInt("category_id"), obj3
											.getString("name"));
									mList2.add(categories);

								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				});
	}
	private class AppShopTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CustomProgressDialog
			.stopProgressDialog(ApplyShopActivity.this);
			if (!OtherUtil.isNullOrEmpty(result)
					&& result.startsWith(GlobalData.RESULT_OK)) {
				Utility.mSession.storeShop();
				ToastUtil.show(ApplyShopActivity.this, R.string.apply_success);
				Intent it = new Intent(ApplyShopActivity.this,SlidActivity.class);
				it.putExtra("flag", 6);
				startActivity(it);
				ApplyShopActivity.this.finish();
			} else if(result.equals("merchant already applied"))
			{
				ToastUtil.show(ApplyShopActivity.this, R.string.applying);
				ApplyShopActivity.this.finish();
			}else{
				ToastUtil.showToast(ApplyShopActivity.this, result);
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String img = "";
			if (null != tempFile && tempFile.exists())
				img = tempPath;
			String res = YouLe.applyShop(ApplyShopActivity.this,
					new SharedPref(ApplyShopActivity.this).getRadioId() + "",
					Utility.mSession.getUserId(), new SharedPref(
							ApplyShopActivity.this).getCityId() + "",
					categoryId, merchants_name.getText().toString(), textCity
							.getText().toString()
							+ merchants_address.getText().toString(),
					merchants_phone.getText().toString(), merchants_link
							.getText().toString(), shopLicense.getText()
							.toString(),
					latLng.substring(latLng.indexOf(",") + 1), latLng
							.substring(0, latLng.indexOf(",")), img);
			if (res.startsWith(GlobalData.RESULT_OK))
				return GlobalData.RESULT_OK;
			else
				return res;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			CustomProgressDialog
			.stopProgressDialog(ApplyShopActivity.this);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CustomProgressDialog.showMsg(ApplyShopActivity.this,
					getString(R.string.please_wait));
		}
		
	}

	// 地理编码
	public void getLatlon(final String name) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					try {
						List<Address> address = coder.getFromLocationName(name, 3);
						if (address != null && address.size() > 0) {
							Address addres = address.get(0);
							latLng = addres.getLatitude() + ","
									+ addres.getLongitude();
							Log.i("1234", latLng);
							handler.sendEmptyMessage(2);
						}
					} catch (AMapException e) {

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		t.start();
	}
	// 逆地理编码
	public void getAddress(final double mlat, final double mLon) {
		Thread t = new Thread(new Runnable() {
			public void run() {

				try {
					try {
						List<List<Address>> lists = coder.getFromLocation(mlat,
								mLon, 3, 3, 3, 500);
						List<Address> address = lists.get(0);
						if (address != null && address.size() > 0) {
							Address addres = address.get(0);
							addressName = addres.getSubLocality()
									+ addres.getFeatureName();
							handler.sendEmptyMessage(1);
						}
					} catch (AMapException e) {
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		t.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				merchants_address.setText(addressName);
			}else if(msg.what == 2)
			{
				if(!OtherUtil.is3gWifi(ApplyShopActivity.this))
		        	ToastUtil.show(ApplyShopActivity.this, R.string.net_no);
		        else
		        	new AppShopTask().execute();
			}
		}
	};

	private void showCategoriesDialog() {
		String[] frequency = new String[mList1.size()];
		for (int i = 0; i < mList1.size(); i++) {
			frequency[i] = mList1.get(i).cname;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ApplyShopActivity.this);
		builder.setTitle("").setIcon(android.R.drawable.ic_dialog_info)
				.setItems(frequency, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setNegativeButton(getString(R.string.cancel), null)
				.setInverseBackgroundForced(true);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// Toast.makeText(ApplyShopActivity.this, "" +
				// mList1.get(which).fstId,
				// Toast.LENGTH_SHORT).show();
				mFstCategories = mList1.get(which).cname;
				showCategoriesDialog2(mList1.get(which).fstId);
				dialog.dismiss();
			}
		};

		builder.setAdapter(null, listener);
		builder.show();

	}

	private void showCategoriesDialog2(int which) {
		final List<Categories> list = new ArrayList<Categories>();

		for (int i = 0; i < mList2.size(); i++) {
			if (mList2.get(i).fstId == which) {
				list.add(new Categories(which, mList2.get(i).sedId, mList2
						.get(i).cname));
			}
		}
		final String[] frequency = new String[list.size()];
		for (int ii = 0; ii < list.size(); ii++) {
			frequency[ii] = list.get(ii).cname;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ApplyShopActivity.this);
		builder.setTitle("").setIcon(android.R.drawable.ic_dialog_info)
				.setItems(frequency, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(getString(R.string.cancel), null);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				Toast.makeText(ApplyShopActivity.this,
//						"" + list.get(which).sedId, Toast.LENGTH_SHORT).show();
				choose.setText(mFstCategories + " - " + list.get(which).cname);
				categoryId = list.get(which).sedId + "";
				dialog.dismiss();
			}
		};

		builder.setAdapter(null, listener);
		builder.show();
	}

	private class Categories {
		public int fstId;
		public int sedId;
		public String cname;

		public Categories(int f, int s, String c) {
			fstId = f;
			sedId = s;
			cname = c;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.appshop_ivPhoto:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
			.hideSoftInputFromWindow(ApplyShopActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			pop = new PicPopu(ApplyShopActivity.this, itemOnClick);
			pop.showAtLocation(
					ApplyShopActivity.this.findViewById(R.id.appshop_ly),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.applyshop_btnCommit:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
			.hideSoftInputFromWindow(ApplyShopActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			if (OtherUtil
					.isNullOrEmpty(merchants_name.getText().toString())
					|| OtherUtil.isNullOrEmpty(merchants_link.getText()
							.toString())
					|| OtherUtil.isNullOrEmpty(merchants_phone.getText()
							.toString())
					|| OtherUtil.isNullOrEmpty(merchants_address.getText()
							.toString())
					|| OtherUtil.isNullOrEmpty(categoryId)) {
				ToastUtil.show(ApplyShopActivity.this, R.string.please_com);
				return;
			} else {
				getLatlon(textCity.getText().toString()
						+ merchants_address.getText().toString());
			}
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != photo)
			photo = null;
	}

	private OnClickListener itemOnClick = new OnClickListener() {

		public void onClick(View v) {
			pop.dismiss();
			switch (v.getId()) {
			case R.id.btn_first:
				if (photo != null) {
					photo = null;
				}
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
				startActivityForResult(intent,
						GlobalData.PHOTO_REQUEST_TAKEPHOTO);
				break;
			case R.id.btn_second:
				if (photo != null) {
					photo = null;
				}
				Intent intents = new Intent(Intent.ACTION_PICK, null);
				intents.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intents,
						GlobalData.PHOTO_REQUEST_GALLERY);
				break;
			}
		}
	};
	private String tempPath;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case GlobalData.PHOTO_REQUEST_TAKEPHOTO:
			if (tempFile != null && tempFile.exists()) {
				managePic(tempFile.getAbsolutePath());
			}
			break;

		case GlobalData.PHOTO_REQUEST_GALLERY:
			backPhotoGallery(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void backPhotoGallery(Intent data) {
		if (data != null) {

			Uri uri = data.getData();
			if (uri != null) {
				try {
					ContentResolver resolver = getContentResolver();
					Cursor cursor = resolver.query(uri, null, null, null, null);
					if (cursor != null) {
						int colunm_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(colunm_index);
						cursor.close();
						if (path.endsWith("jpg") || path.endsWith("png")) {
							managePic(path);
						} else {
							// Log.i("test", "pic lost");
						}
					}

				} catch (Exception e) {
					Log.i("test", e.toString());
				}
			}

		}
	}

	private void managePic(String path) {
		try {
			try {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, opts);
				opts.inSampleSize = OtherUtil.computeSampleSize(opts, -1,
						1024 * 720);// 1920*1080
				opts.inJustDecodeBounds = false;
				FileInputStream fis = new FileInputStream(path);
				photo = ThumbnailUtils.extractThumbnail(
						BitmapFactory.decodeStream(fis, null, opts), 96, 96,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				if (!OtherUtil.isNullOrEmpty(path)) {
					// ExifInterface exif = new ExifInterface(path);
					// int picWidth = Integer.valueOf(exif
					// .getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
					// int picHeight = Integer.valueOf(exif
					// .getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
					// if (picWidth < 400 && picHeight < 400) {
					// ToastUtil.showToast(ApplyShopActivity.this, R.string.s);
					// photo = null;
					// } else {
					tempPath = OtherUtil.saveBitmap(path);
					ivPic.setImageBitmap(photo);
					// }
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			Log.i("test", e.toString());
		}

	}
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			SlipMainCenter.slidIntent(6);
//			ApplyShopActivity.this.finish();
//		}
//		return true;
//	}
}
