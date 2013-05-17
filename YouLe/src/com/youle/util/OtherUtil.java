package com.youle.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.search.core.AMapException;
import com.amap.api.search.geocoder.Geocoder;
import com.youle.managerUi.ChooseCity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

public class OtherUtil {
	public static AMapLocation amlocation = null;

	/**
	 * 验证是否是邮箱格式
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static <T> boolean isNullOrEmpty(Collection<T> c) {
		return (c == null) || (c.size() == 0);
	}

	/**
	 * String是否null
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNullOrEmpty(String s) {
		return (TextUtils.isEmpty(s) || (s == null) || (s.length() == 0));
	}

	/**
	 * 判断是否有3G或wifi
	 * 
	 * @param inContext
	 * @return
	 */
	public static boolean is3gWifi(Context inContext) {
		try {
			Context context = inContext.getApplicationContext();
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isAvailable() && info.isConnected())
					return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 缩放图片
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static void getLocation(final Context context) {
		final LocationManagerProxy mAMapLocManager;
		mAMapLocManager = LocationManagerProxy.getInstance(context);

		AMapLocationListener listener = new AMapLocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(AMapLocation location) {
				amlocation = location;
				Intent intent = new Intent(GlobalData.BROADCAST_COUNTER_ACTION);
				Bundle bundle = new Bundle();
				bundle.putDouble("lat", location.getLatitude());
				bundle.putDouble("lng", location.getLongitude());
				intent.putExtras(bundle);
				context.sendBroadcast(intent);
				mAMapLocManager.removeUpdates(this);
			}
		};
		mAMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 30000, 50, listener);

	}
	/**
	 * 裁剪图片
	 * 
	 * @param bitmap
	 * @param w
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		matrix.postScale(scaleWidht, scaleWidht);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}
	
	public static String[] getDesc(Context context,double lat, double lng) {
		Geocoder coder = new Geocoder(context);
		List<Address> address = null;
		
		try {
			address = coder.getFromLocation(lat, lng, 3);
		} catch (AMapException e) {
			Log.i("1234", "没取到~" + e.getMessage());
			getLocation(context);
			return null;
		}
		if (address != null && address.size() > 0) {
			Address addres = address.get(0);
			String [] add=new String[3];
			add[0]=addres.getLocality();
			add[1]=addres.getSubLocality();
			add[2]=addres.getThoroughfare();
			String addressName = addres.getLocality()
					+ addres.getSubLocality() + addres.getThoroughfare();
			Log.i("1234", "" + addressName);
			return add;
		}
		return null;
	}
}
