package com.youle.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.search.core.AMapException;
import com.amap.api.search.geocoder.Geocoder;
import com.youle.managerData.MyApplication;
import com.youle.managerUi.CouponDetailActivity;
import com.youle.managerUi.SlidActivity;

public class OtherUtil {
	public static AMapLocation amlocation = null;

	/**
	 * 验证是否是邮箱格式
	 * 
	 * @param email
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
                bundle.putFloat("spd",location.getSpeed());
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
        Log.i("1234","w: "+width+"  h: "+height);
		Matrix matrix = new Matrix();
        float scaleWidht;
        if(width>height){
		    scaleWidht = ((float) w / width);
        }else{
            scaleWidht = ((float) w / height);
        }
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
            String [] add=new String[4];
			add[0]=addres.getLocality();
			add[1]=addres.getSubLocality();
			add[2]=addres.getThoroughfare();
            add[3]=addres.getCountryName();
			String addressName = addres.getLocality()
					+ addres.getSubLocality() + addres.getThoroughfare();
			Log.i("1234", "" + addressName);
			return add;
		}
		return null;
	}

    public static String getSDcard(){
        // 获取SdCard状态
        String state = android.os.Environment.getExternalStorageState();
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
            if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
                return android.os.Environment.getExternalStorageDirectory().getPath();
                }
            }
        return null;
    }
//    /**
//	 * 判断SD卡是否存在
//	 * 
//	 * @return
//	 */
//	public static boolean SDCardExist() {
//		try {
//			if (Environment.getExternalStorageState().equals(
//					Environment.MEDIA_MOUNTED)) {
//				return true;
//			} else
//				return false;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//	}
    /**
	 * 文件的名字（拍照录音）
	 * 
	 * @return
	 */
	public static String getFileName() {
		return "pt"
				+ new DateFormat().format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA));
	}
	public static File fileCreate(String fileName,Boolean isCache) {
		String sdCard = getSDcard();
		if (isNullOrEmpty(sdCard)) {
			return null;
		}
		File path;
		if (isCache)
			path = new File(sdCard+GlobalData.CACHE);
		else
			path = new File(sdCard+GlobalData.YOULE);
		if (!path.exists()) {
			path.mkdir();
		}
		File file = new File(path, fileName);
		return file;
	}
    
//	public static void initImageLoader(Context context) {
//		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
//
//		MemoryCacheAware<String, Bitmap> memoryCache;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//			memoryCache = new LruMemoryCache(memoryCacheSize);
//		} else {
//			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
//		}
//
//		// This configuration tuning is custom. You can tune every option, you
//		// may tune some of them,
//		// or you can create default configuration by
//		// ImageLoaderConfiguration.createDefault(this);
//		// method.
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//				context).threadPriority(Thread.NORM_PRIORITY - 2)
//				.memoryCache(memoryCache).denyCacheImageMultipleSizesInMemory()
//				.discCacheFileNameGenerator(new Md5FileNameGenerator())
//				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() // Not
//																				// necessary
//																				// in
//																				// common
//				.build();
//		// Initialize ImageLoader with configuration.
//		ImageLoader.getInstance().init(config);
//	}
	/**
	 * 保存图片到SD卡上
	 * 
	 * @param bm
	 * @param file
	 * 
	 */
	public static void saveFile(Bitmap bm, File file) {
		try {
			if (!isNullOrEmpty(getSDcard())) {
				// 检查图片是否存在
				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (bm != null && !bm.isRecycled()) {
			bm = null;
		}
		System.gc();
	}
    /**
     * Store image to SD card.
     */

    public static String saveBitmap(String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(fileName, options);

        File f = new File(getSDcard() + "/.cache/upload.jpg");
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap=zoomBitmap(bitmap,1024);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);// 把Bitmap对象解析成流
        try {
            fOut.flush();
            fOut.close();
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("1234",""+f.getAbsolutePath());
        return f.getAbsolutePath();
    }
    /**
     * 图片倒圆角
     *
     * @param bitmap
     * @param pixels
     *
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 字符串转换成日期
     * @param str
     * @return date
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void gotoAim(Context context ,int type, int re_id) {
        Intent intent = null;
        switch (type) {
            case 1:
                intent = new Intent(context, CouponDetailActivity.class);
                intent.putExtra("uc_id", re_id + "");
                break;
            case 2:
                intent = new Intent(context, SlidActivity.class);
                intent.putExtra("flag", 1);
                break;
            case 4:
                intent = new Intent(context, SlidActivity.class);
                intent.putExtra("flag", 7);
                break;
            case 5:
                intent = new Intent(context, SlidActivity.class);
                intent.putExtra("flag", 1);
                break;
        }
        context.startActivity(intent);
    }
    public static void showCloseDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("我们当前只为中国地区用户服务。其他地区用户暂时不能使用");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MyApplication.getInstance().exit();
            }
        });
        builder.create().show();
    }
}
