package com.youle.http_helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.youle.R;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.managerData.info.CarMainInfo;
import com.youle.managerData.info.CouponListInfo;
import com.youle.managerData.info.MainInfo;
import com.youle.managerData.info.MeInfo;
import com.youle.managerData.info.Token;
import com.youle.managerUi.CouponActivity;
import com.youle.managerUi.SplashActivity;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;
import com.youle.util.ToastUtil;

import android.content.Context;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

public class YouLe {
	public static String client_id = "469192608";
	public static String client_secret = "9bc4ce21147e3bc9";
	public static final String BASE_URL = "http://119.15.136.126:8000/";//218.6.224.55:8000  119.15.136.126:8000
	private static final String REGISTER_URL = "account/signup";// 注册URL
	private static final String UP_AVATAR = "account/avatar";// 上传头像URL
	private static final String UP_COVER = "account/cover";// 上传封面
	private static final String OAUTH = "oauth2/access_token";// oauth认证
	private static final String INFO = "tracks";// 获取信息列表
	private static final String U_INFO = "member/";
	private static final String SHOP = "merchants";
	private static final String COUPONS = "coupons";
	private static final String SCAN_COUPON = "user_coupons";//商家扫描优惠券
	private static final String FIX_PW = "member";
	private static final String FORUMS = "forums";//获取论坛列表
	private static final String FORUM_TOPIC = "topics/";//获取指定论坛主题列表
	private static final String TAXI = "taxis";
	private static final String POSTS = "posts/";
	private static final String CATEGORY = "categories/all";
	
	/**
	 * 登录
	 * @param context
	 * @param email
	 * @param pw
	 * @return
	 */
	public static String loginApp(Context context, String email, String pw) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.GRANT_TYPE, "password");
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.EMAIL, email);
		parameters.add(GlobalData.PASSWORD, pw);
		parameters.add(GlobalData.DEVICE_TOKEN, Secure.getString(
				context.getContentResolver(), Secure.ANDROID_ID));
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(OAUTH)
							.toString(), Utility.HTTPMETHOD_POST, parameters);
			if (!TextUtils.isEmpty(result)) {
				if (result.length() >= 3) {
					if (result.startsWith(GlobalData.RESULT_OK)) {
						if (null == Utility.mSession)
							Utility.mSession = new YLSession(context);
						return regRightJson(result.substring(3));
					} else {
						return result;
					}
				}
			}
			return GlobalData.RESULT_NULL_CODE;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Log.e("YouLe", "YouLe loginApp:" + e.toString());
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	/**
	 * 刷新token
	 * @param context
	 * @param refresh_token
	 * @return
	 */
	public synchronized static String refreshToken(Context context,
			String refresh_token) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.REFRESH_TOKEN, refresh_token);
		parameters.add(GlobalData.GRANT_TYPE, GlobalData.REFRESH_TOKEN);
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(OAUTH)
							.toString(), Utility.HTTPMETHOD_POST, parameters);
			if (!TextUtils.isEmpty(result)) {
				if (result.length() >= 3) {
					if (result.startsWith(GlobalData.RESULT_OK)) {
						if (null == Utility.mSession)
							Utility.mSession = new YLSession(context);
						return regRightJson(result.substring(3));
					} else {
						return result;
					}
				}
			}
			return GlobalData.RESULT_NULL_CODE;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			Log.e("YouLe", "YouLe refreshToken:" + e.toString());
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	/**
	 * 注册
	 * @param context
	 * @param email
	 * @param username
	 * @param password
	 * @return
	 */
	public static String register(Context context, String email,
			String username, String password) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.USER_NAME, username);
		parameters.add(GlobalData.EMAIL, email);
		parameters.add(GlobalData.PASSWORD, password);
		parameters.add(GlobalData.DEVICE_TOKEN, Secure.getString(
				context.getContentResolver(), Secure.ANDROID_ID));
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(REGISTER_URL)
							.toString(), Utility.HTTPMETHOD_POST, parameters);
			if (!TextUtils.isEmpty(result)) {
				if (result.length() >= 3) {
					if (result.startsWith(GlobalData.RESULT_OK)) {
						if (null == Utility.mSession)
							Utility.mSession = new YLSession(context);
						return regRightJson(result.substring(3));
					} else {
						return result;
					}
				}
			}
			return GlobalData.RESULT_NULL_CODE;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Log.e("YouLe", "YouLe register:" + e.toString());
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	private static String regRightJson(String strResult) {
		try {
			JSONObject js = new JSONObject(strResult);
			JSONObject js2 = js.optJSONObject(GlobalData.MEMBER_INFO);
			Log.i("test", "save avaUrl:" + js2.getString(GlobalData.AVATAR_URL));
			Utility.mSession.storeToken(new Token(js
					.isNull(GlobalData.ACCESS_TOKEN) ? "" : js
					.getString(GlobalData.ACCESS_TOKEN), js
					.isNull(GlobalData.EXPIRES_IN) ? 0 : js
					.getLong(GlobalData.EXPIRES_IN), js
					.isNull(GlobalData.REFRESH_TOKEN) ? "" : js
					.getString(GlobalData.REFRESH_TOKEN)));
			Utility.mToken = Utility.mSession.getToken();
			Utility.mSession.storeMe(js2
					.isNull(GlobalData.USER_ID) ? "" : js2
					.getString(GlobalData.USER_ID), js2
					.isNull(GlobalData.TYPE) ? 0 : js2
					.getInt(GlobalData.TYPE), js2
					.isNull(GlobalData.AVATAR_URL) ? "" : js2
					.getString(GlobalData.AVATAR_URL));
			return GlobalData.RESULT_OK;
		} catch (JSONException ex) {
			Utility.resetToken();
			return GlobalData.JSON_ERROR_CODE;
		}
	}
	/**
	 * 第三方注册/登录
	 * @param context
	 * @param pForm
	 * @param uId
	 * @param uName
	 * @return
	 */
	public static String ThirdPlate(Context context, String pForm, String uId,
			String uName) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.P_FORM, pForm);
		parameters.add(GlobalData.U_ID, uId);
		parameters.add(GlobalData.USER_NAME, uName);
		parameters.add(GlobalData.GRANT_TYPE, "platform");
		parameters.add(GlobalData.DEVICE_TOKEN, Secure.getString(
				context.getContentResolver(), Secure.ANDROID_ID));
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(OAUTH)
							.toString(), Utility.HTTPMETHOD_POST, parameters);
//			Log.i("test", "result:"+result);
			if (!TextUtils.isEmpty(result)) {
				if (result.length() >= 3) {
					if (result.startsWith(GlobalData.RESULT_OK)) {
						Utility.mSession = new YLSession(context);
						return regRightJson(result.substring(3));
					} else {
						return result;
					}
				}
			}
			return GlobalData.RESULT_NULL_CODE;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Log.e("YouLe", "YouLe loginApp:" + e.toString());
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	/**上传封面
	 * @param context
	 * @param img
	 */
	public static String upCover(Context context, String img) {
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.IMG, img);
		try {
			String res = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(UP_COVER)
							.toString(), Utility.HTTPMETHOD_POST, params);
//			Log.i("YouLe", "cover res：" + res);
			if (!TextUtils.isEmpty(res)) {
				if (res.length() >= 3) {
					if (res.startsWith(GlobalData.RESULT_OK)) {
						return getCoverJson(res.substring(3));
					}
				}
			}
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(OtherUtil.is3gWifi(context))
			return GlobalData.REQUEST_FAIL;
        else
        	return context.getString(R.string.net_no);
	}

	private static String getCoverJson(String strResult) {
		String c_url = null;
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject js = (JSONObject) jsonParser.nextValue();
			c_url = js.getString(GlobalData.COVER_URL);
//			Log.i("YouLe", "c_url：" + c_url);
		} catch (JSONException ex) {
			c_url = null;
		}
		return c_url;
	}

	/**上传头像
	 * @param context
	 * @param picPath
	 * @return
	 */
	public static String upAvatar(Context context, String picPath) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.IMG, picPath);
		try {
			String str = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(UP_AVATAR)
							.toString(), Utility.HTTPMETHOD_POST, parameters);
			if (!TextUtils.isEmpty(str)) {
				if (str.length() >= 3) {
					if (str.startsWith(GlobalData.RESULT_OK)) {
						return getPicJson(str.substring(3));
					} else {
						return str;
					}
				}
			}
			return GlobalData.RESULT_NULL_CODE;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			Log.e("YouLe", "YouLe upAvatar:" + e.toString());
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	// http://218.6.224.55:8000/avatars/256/100003.jpg
	private static String getPicJson(String strResult) {
		String avatar_url = null;
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject js = (JSONObject) jsonParser.nextValue();
			StringBuffer sb = new StringBuffer();
			sb.append(js.getString(GlobalData.URI_PREFIX))
					.append(js.getString(GlobalData.SIZES).substring(1, 4))
					.append("/").append(js.getString(GlobalData.AVATAR));
			avatar_url = sb.toString();
			Log.i("YouLe", "avatar_url：" + avatar_url);
		} catch (JSONException ex) {
			avatar_url = null;
		}
		return avatar_url;
	}

	/**
	 * @param context
	 * @param distance
	 * @param lng
	 * @param lat
	 * @param size
	 * http://218.6.224.55:8000/tracks?access_token=6f56b0cfac27f0e3&radio_id=1&distance=0&size=30
	 */
	public static String getInfoList(Context context, int distance, double lng,
			double lat, int page,int size) {
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.RADIO_ID,
				Integer.toString(new SharedPref(context).getRadioId()));
		params.add(GlobalData.DISTANCE, Integer.toString(distance));
		if (distance > 0) {
			params.add(GlobalData.LNG, Double.toString(lng));
			params.add(GlobalData.LAT, Double.toString(lat));
		}
		params.add(GlobalData.PAGE, Integer.toString(page));
		params.add(GlobalData.SIZE, Integer.toString(size));
		try {
			String result = Utility
					.openUrl(context, new StringBuffer().append(BASE_URL)
							.append(INFO).toString(), Utility.HTTPMETHOD_GET,
							params);
//			Log.i("YouLe", "getInfoList:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	/**获取发布的路况
	 * @param context
	 * @param uid
	 * @param page
	 * @param size
	 * @return
	 */
	public static String getUserTracks(Context context, String uid, int page) {
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.U_ID, uid);
		params.add(GlobalData.PAGE, Integer.toString(page));
		params.add(GlobalData.SIZE, "20");
		try {
			String result = Utility
					.openUrl(context, new StringBuffer().append(BASE_URL)
							.append(INFO).toString(), Utility.HTTPMETHOD_GET,
							params);
//			Log.i("YouLe", "getUserList:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	public static List<MainInfo> jsonInfo(String res) {
		String tStr = res.substring(3);
		try {
			List<MainInfo> list = new ArrayList<MainInfo>();
			JSONObject js = new JSONObject(tStr);
			if (js.getInt(GlobalData.TOTAL) > 0) {
				JSONArray array = js.getJSONArray(GlobalData.TRACKS);
				JSONObject obj;
				for (int i = 0, j = array.length(); i < j; i++) {
					obj = array.optJSONObject(i);
					String latLng = obj.getString(GlobalData.LOCATION);
					list.add(new MainInfo(
							obj.getString(GlobalData.TRACK_ID),
							obj.getString(GlobalData.USER_ID),
							obj.getString(GlobalData.USER_NAME),
							obj.getString(GlobalData.AVATAR_URL),
							formatDate(intToLong(obj.getInt(GlobalData.CREATED))),
							obj.getString(GlobalData.TEXT), obj
									.getInt(GlobalData.MARK), obj
									.getString(GlobalData.AUDIO_URL), obj
									.getInt(GlobalData.AUDIO_TIME), obj
									.getString(GlobalData.IMAGE_URL), obj
									.getString(GlobalData.ORIGIN_IMAGE_URL),
							new LatLng(Double.parseDouble(latLng.substring(
									latLng.indexOf(",") + 1,
									latLng.lastIndexOf("]"))), Double
									.parseDouble(latLng.substring(1,
											latLng.indexOf(",")))), obj
									.getString(GlobalData.PLACE), obj
									.getInt(GlobalData.FLAGS), obj
									.getInt(GlobalData.WIDTH), obj
									.getInt(GlobalData.HEIGHT), obj
									.getInt(GlobalData.USER_TYPE)));
				}
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 发布信息
	 * 
	 * @param context
	 * @param lng
	 * @param lat
	 * @param place
	 * @param spd
	 * @param mark
	 * @param aud
	 * @param aud_t
	 * @param img
	 * @param text
	 */
	public static void upTrack(Context context, int radio_id, double lng,
			double lat, String place, double spd, int mark, String aud,
			int aud_t, String img, String text) {
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.RADIO_ID, Integer.toString(radio_id));
		params.add(GlobalData.LNG, Double.toString(lng));
		params.add(GlobalData.LAT, Double.toString(lat));
		params.add(GlobalData.PLACE, place);
		params.add(GlobalData.SPD, Double.toString(spd));
		params.add(GlobalData.MARK, Integer.toString(mark));
		params.add(GlobalData.AUD, aud);
		params.add(GlobalData.AUD_T, Integer.toString(aud_t));
		params.add(GlobalData.IMG, img);
		params.add(GlobalData.TXT, text);
		try {
			String result = Utility.openTrackUrl(context, new StringBuffer()
					.append(BASE_URL).append(INFO).toString(),
					Utility.HTTPMETHOD_POST, params);
			Log.i("YouLe", "upTrack:" + result);
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**获取用户信息
	 * @param context
	 * @return
	 */
	public static String getMeInfo(Context context) {
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(FIX_PW)
							.toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("YouLe", "getMeInfo result:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	/**获取指定用户信息
	 * @param context
	 * @param uId
	 */
	public static String getUserInfo(Context context, String uId) {
		Log.i("YouLe", "uId:" + uId);
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.USER_ID, uId);
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(U_INFO).append(uId)
							.toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("YouLe", "getUserInfo result:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	public static MeInfo jsonUserInfo(String res,boolean isMe) {
		try {
			JSONObject js = new JSONObject(res);
			MeInfo info = new MeInfo(js.getString(GlobalData.USER_NAME), js.getString(GlobalData.USER_ID), 
					js.getInt(GlobalData.TYPE), 
					js.getInt(GlobalData.GENDER), 
					js.getString(GlobalData.AVATAR_URL), 
					js.getString(GlobalData.LEVEL), 
					js.getString(GlobalData.POINTS));
			if(isMe)
			{
				info.setAge(js.getString(GlobalData.AGE));
				info.setName(js.getString(GlobalData.NAME));
				Utility.mSession.storeMe(info);
			}
			return info;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**获取优惠券列表（列表模式：0，地图模式：1）
	 * @param context
	 * @param isList
	 * @param radioId
	 * @param distance
	 * @param lng
	 * @param lat
	 * http://218.6.224.55:8000/coupons?map=1&radio_id=1&distance=5000&lng=104.089328&lat=30.667327
	 */
	public static String getCoupon(Context context,String isList,String radioId,int distance,double lng,double lat)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.MAP, isList);
		params.add(GlobalData.RADIO_ID, radioId);
		params.add(GlobalData.DISTANCE, Integer.toString(distance));
		params.add(GlobalData.LNG, Double.toString(lng));
		params.add(GlobalData.LAT, Double.toString(lat));
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(COUPONS)
							.toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("test", "coupons:"+result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	public static List<CouponListInfo> jsonCouponList(String res)
	{
		List<CouponListInfo> list = new ArrayList<CouponListInfo>();
		try {
			JSONObject js = new JSONObject(res);
			if (js.getInt(GlobalData.TOTAL) > 0) {
				JSONArray array = js.getJSONArray(GlobalData.COUPONS);
				JSONObject obj;
				for (int i = 0, j = array.length(); i < j; i++) {
					obj = array.optJSONObject(i);
					JSONObject obj2 = obj.optJSONObject(GlobalData.MERCHANT);
					JSONObject obj3 = obj.optJSONObject(GlobalData.COUPON);
					list.add(new CouponListInfo(
							obj2.getInt(GlobalData.MERCHANT_ID), 
							obj2.getString(GlobalData.NAME), 
							obj2.getString(GlobalData.ADDRESS), 
							obj.getString(GlobalData.DISTANCE).substring(0, obj.getString(GlobalData.DISTANCE).lastIndexOf(".")), 
							obj3.getString(GlobalData.IMAGE_URL), 
							obj3.getString(GlobalData.NAME), 
							obj3.getString(GlobalData.EXPIRE_AT)));
				}
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	/**获取我的优惠券
	 * @param context
	 * @param page
	 * @param size
	 */
	public static String getMeCoupon(Context context,int page,int size)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.EXPIRED, "0");
		params.add(GlobalData.PAGE, Integer.toString(page));
		params.add(GlobalData.SIZE, Integer.toString(size));
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(SCAN_COUPON)
							.toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("test", "me_coupons:"+result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	public static List<CouponListInfo> jsonMeCoupon(Context context,String res)
	{
		LatLng lg = new SharedPref(context).getLatLng();
		List<CouponListInfo> list = new ArrayList<CouponListInfo>();
		try {
			JSONObject js = new JSONObject(res);
			if (js.getInt(GlobalData.TOTAL) > 0) {
				JSONArray array = js.getJSONArray(GlobalData.COUPONS);
				JSONObject obj;
				for (int i = 0, j = array.length(); i < j; i++) {
					obj = array.optJSONObject(i);
					JSONObject obj3 = obj.optJSONObject(GlobalData.COUPON);
					String latLng = obj3.getString(GlobalData.LOCATION);
					LatLng lg2 = new LatLng(Double.parseDouble(latLng.substring(
							latLng.indexOf(",") + 1,
							latLng.lastIndexOf("]"))), Double
							.parseDouble(latLng.substring(1,
									latLng.indexOf(","))));
					float distance = AMapUtils.calculateLineDistance(lg, lg2);
					CouponListInfo info = new CouponListInfo(
							obj3.getInt(GlobalData.MERCHANT_ID), 
							obj3.getString(GlobalData.MERCHANT_NAME), 
							obj3.getString(GlobalData.ADDRESS), 
							Float.toString(distance).substring(0, Float.toString(distance).lastIndexOf(".")), 
							obj3.getString(GlobalData.IMAGE_URL), 
							obj3.getString(GlobalData.NAME), 
							obj3.getString(GlobalData.TO_DATE));
					info.setQrCode(obj.getString(GlobalData.QR_CODE));
					list.add(info);
				}
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**修改密码
	 * @param context
	 * @param oldPw
	 * @param newPw
	 * @return
	 */
	public static String fixPassword(Context context,String oldPw,String newPw)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.PASSWORD, oldPw);
		params.add(GlobalData.NEW_PASSWORD, newPw);
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(FIX_PW)
							.toString(), Utility.HTTPMETHOD_PUT, params);
			Log.i("YouLe", "fixPw result:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
		
	}
	/**
	 * @param context
	 * @param name
	 * @param gender
	 * @param age
	 * @return
	 */
	public static String setMe(Context context,String name,String gender,String age)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.NAME, name);
		params.add(GlobalData.GENDER, gender);
		if(!OtherUtil.isNullOrEmpty(age))
			params.add(GlobalData.AGE, age);
		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(FIX_PW)
							.toString(), Utility.HTTPMETHOD_POST, params);
			Log.i("YouLe", "fixPw result:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	/**
	 * @param context
	 * @param forumId
	 * @param txt
	 * @param img
	 * @param aud
	 * @param aud_t
	 * @return
	 */
	public static String pubTopic(Context context,String forumId,String txt,String img,String aud,int aud_t)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.TXT, txt);
		params.add(GlobalData.IMG, img);
		params.add(GlobalData.AUD, aud);
		params.add(GlobalData.AUD_T, Integer.toString(aud_t));
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(FORUM_TOPIC).append(forumId).toString(), Utility.HTTPMETHOD_POST, params);
			Log.i("YouLe", "pubTopic result:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	/**
	 * @param context
	 * @param postId
	 * @param txt
	 * @param img
	 * @param aud
	 * @param aud_t
	 * @return
	 */
	public static String replyTopic(Context context,String postId,String txt,String img,String aud,int aud_t)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.TXT, txt);
		params.add(GlobalData.IMG, img);
		params.add(GlobalData.AUD, aud);
		params.add(GlobalData.AUD_T, Integer.toString(aud_t));
		try {
			String res = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(POSTS).append(postId).toString(), Utility.HTTPMETHOD_POST, params);
			Log.i("YouLe", "replyTopic result:" + res);
			return res;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	/**
	 * @param context
	 * @param radioId
	 * @param page
	 * @param size
	 * @return
	 */
	public static String getForums(Context context,int radioId,int page,int size)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.RADIO_ID, Integer.toString(radioId));
		params.add(GlobalData.PAGE, Integer.toString(page));
		params.add(GlobalData.SIZE, Integer.toString(size));
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(FORUMS).toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("test", "getForums:"+result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	public static List<CarMainInfo> jsonForums(String res)
	{
		List<CarMainInfo> list = new ArrayList<CarMainInfo>();
		try {
			JSONObject js = new JSONObject(res);
			if (js.getInt(GlobalData.TOTAL) > 0) {
				JSONArray array = js.getJSONArray(GlobalData.FORUMS);
				JSONObject obj;
				for(int i=0,j=array.length();i<j;i++)
				{
					obj = array.optJSONObject(i);
					list.add(new CarMainInfo(obj.getString(GlobalData.LOGO_URL), 
							obj.getString(GlobalData.NAME), obj.getString(GlobalData.TOTAL_POSTS), obj.getString(GlobalData.FORUM_ID), obj.getString(GlobalData.RADIO_ID)));
				}
				return list;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @param context
	 * @param forumId
	 * @return
	 */
	public static String getTheForums(Context context,String forumId)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.FORUM_ID, forumId);
		try {
			String res = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(FORUMS).append("/").append(forumId).toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("test", "getTheForums:"+res);
			return res;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	/**
	 * @param context
	 * @param forumId
	 * @param page
	 * @param size
	 * @return
	 */
	public static String getForumsTopic(Context context,String forumId,int page,int size)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.FORUM_ID, forumId);
		params.add(GlobalData.PAGE, Integer.toString(page));
		params.add(GlobalData.SIZE, Integer.toString(size));
		try {
			String res = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(FORUM_TOPIC).append(forumId).toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("test", "getForumsTopic:"+res);
			return res;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	public static void jsonForumTopic(String res)
	{
		
	}
	public static String getReplyList(Context context,String postId,int page,int size)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.PAGE, Integer.toString(page));
		params.add(GlobalData.SIZE, Integer.toString(size));
		try {
			String res = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(POSTS).append(postId).toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("test", "getReplyList:"+res);
			return res;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	/**
	 * @param context
	 * @param radioId
	 * @param name
	 * @param phone
	 * @param license
	 * @param company
	 * @param no
	 * @return
	 */
	public static String applyTaxi(Context context,String radioId,String name,String phone,String license,String company,String no)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.RADIO_ID, radioId);
		params.add(GlobalData.NAME, name);
		params.add(GlobalData.PHONE, phone);
		params.add(GlobalData.LICENSE, license);
		params.add(GlobalData.COMPANY, company);
		params.add(GlobalData.NO, no);
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(TAXI).toString(), Utility.HTTPMETHOD_POST, params);
			Log.i("test", "appTaxi:"+result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	public static String getCategory(Context context)
	{
		RequestParameters params = new RequestParameters();
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(CATEGORY).toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("test", "getCategory:"+result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}
	/**
	 * @param context
	 * @param code
	 */
	public static String scanCoupon(Context context,String code,String confirm)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.CODE, code);
		params.add(GlobalData.CONFIRM, confirm);
		try {
			String result = Utility.openUrl(context,new StringBuffer().append(BASE_URL).append(SCAN_COUPON).toString(), Utility.HTTPMETHOD_POST, params);
			Log.i("test", "scan_coupon:"+result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
		
	}
	
	private static String getTime(long timestamp,boolean isDay) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = null;
		try {
			String str = sdf.format(new Timestamp(timestamp * 1000));
			if(isDay)
				time = str.substring(5, 10);
			else
				time = str.substring(11, 16);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}

	private static long intToLong(int i) {
		long result = (long) i;
		// result *= 1000;
		return result;
	}

	public static String formatDate(long timestamp) {
		long currentTime = System.currentTimeMillis() / 1000;
		long mss = currentTime - timestamp;
		long days = mss / (60 * 60 * 24);
		long hours = (mss % (60 * 60 * 24)) / (60 * 60);
		long minutes = (mss % (60 * 60)) / (60);
		long seconds = (mss % (60)) / 1000;
		if (days > 0) {
			return getTime(timestamp,true);
		} else if (hours > 0) {
			return getTime(timestamp,false);
		} else if (minutes > 0) {
			return minutes + "分钟前";
		} else {
			return seconds + "秒钟前";
		}
	}

	public static String applyShop(Context context, String radioId,
			String userId, String cityId, String categoryId, String name,
			String address, String phone, String contact, String license,
			String lng, String lat, String img) {
		RequestParameters params = new RequestParameters();
		if(null != Utility.mToken)
			params.add(GlobalData.ACCESS_TOKEN, Utility.mToken.getAccess_token());
		params.add(GlobalData.RADIO_ID, radioId);
		params.add(GlobalData.USER_ID, userId);
		params.add(GlobalData.CITY_ID, cityId);
		params.add(GlobalData.CATEGORY_ID, categoryId);
		params.add(GlobalData.NAME, name);
		params.add(GlobalData.ADDRESS, address);
		params.add(GlobalData.PHONE, phone);
		params.add(GlobalData.CONTACT, contact);
		params.add(GlobalData.LICENSE, license);
		params.add(GlobalData.LNG, lng);
		params.add(GlobalData.LAT, lat);
		params.add(GlobalData.IMG, img);
		try {
			String result = Utility
					.openUrl(context, new StringBuffer().append(BASE_URL)
							.append(SHOP).toString(), Utility.HTTPMETHOD_POST,
							params);
			Log.i("YouLe", "appShop result:" + result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(OtherUtil.is3gWifi(context))
				return GlobalData.REQUEST_FAIL;
	        else
	        	return context.getString(R.string.net_no);
		}
	}

	/**
	 * @param theString
	 * @return
	 */
	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
