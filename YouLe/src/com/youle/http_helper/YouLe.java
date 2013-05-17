package com.youle.http_helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.youle.managerData.SharedPref.YLSession;
import com.youle.managerData.info.Token;
import com.youle.util.GlobalData;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


public class YouLe {
	public static String client_id = "469192608";
	public static String client_secret = "kDEa4BarT0";
	public static final String BASE_URL = "http://api.pathtrip.com/";// http://218.6.224.38:5000/
																	// http://api.pathtrip.com/
	private static final String REGISTER_URL = "account/signup";// 注册URL
	private static final String UP_AVATAR = "account/avatar";// 上传头像URL
	private static final String OAUTH = "oauth2/access_token";// oauth认证
	/**
	 * 登录
	 * 
	 * @param context
	 * @param email
	 * @param pw
	 * @return
	 */
	public static String loginApp(Context context, String email, String pw) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.GRANT_TYPE, "password");
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);//
		parameters.add(GlobalData.USER_NAME, email);
		parameters.add(GlobalData.PASSWORD, pw);
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(OAUTH).toString(),
					Utility.HTTPMETHOD_POST, parameters);
			if (!TextUtils.isEmpty(result)) {
				if (result.length() >= 3) {
					if (result.startsWith(GlobalData.RESULT_OK)) {
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
			Log.e("PathTrip", "pathtrip loginApp:" + e.toString());
			return GlobalData.EXCEPTION_CODE;
		}
	}

	/**
	 * 刷新token
	 * 
	 * @param context
	 * @param grant_type
	 * @param refresh_token
	 * @return
	 */
	public synchronized static String refreshToken(Context context,
			String grant_type, String refresh_token) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.GRANT_TYPE, grant_type);
		parameters.add(GlobalData.REFRESH_TOKEN, refresh_token);
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(OAUTH).toString(),
					Utility.HTTPMETHOD_POST, parameters);
			if (!TextUtils.isEmpty(result)) {
				if (result.length() >= 3) {
					if (result.startsWith(GlobalData.RESULT_OK)) {
						return regRightJson(result.substring(3));
					} else {
						return result;
					}
				}
			}
			return GlobalData.RESULT_NULL_CODE;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			Log.e("PathTrip", "pathtrip refreshToken:" + e.toString());
			return GlobalData.EXCEPTION_CODE;
		}
	}

	/**
	 * 注册
	 * 
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
		parameters.add(GlobalData.EMAIL, email);
		parameters.add(GlobalData.USER_NAME, username);
		parameters.add(GlobalData.PASSWORD, password);

		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(REGISTER_URL).toString(), Utility.HTTPMETHOD_POST,
					parameters);
			 Log.i("PathTrip", "result:"+result);
			if (!TextUtils.isEmpty(result)) {
				if (result.length() >= 3) {
//					String tt = result.substring(0, 3);
//					Log.i("test", "register tt---" + tt);
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
			Log.e("PathTrip", "pathtrip register:" + e.toString());
			return GlobalData.EXCEPTION_CODE;
		}
	}

	private static String regRightJson(String strResult) {
		try {
			JSONObject js = new JSONObject(strResult);
			Utility.mSession.storeToken(new Token(js
					.isNull(GlobalData.ACCESS_TOKEN) ? "" : js
					.getString(GlobalData.ACCESS_TOKEN), js
					.isNull(GlobalData.EXPIRES_IN) ? 0 : js
					.getLong(GlobalData.EXPIRES_IN), js
					.isNull(GlobalData.REFRESH_TOKEN) ? "" : js
					.getString(GlobalData.REFRESH_TOKEN), js
					.isNull(GlobalData.USER_ID) ? "" : js
					.getString(GlobalData.USER_ID), (int) Math.floor(System
					.currentTimeMillis() / 1000)));
			Utility.mToken = Utility.mSession.getToken();
			return GlobalData.RESULT_OK;
		} catch (JSONException ex) {
			Utility.resetToken();
			return GlobalData.JSON_ERROR_CODE;
		}
	}

	/**
	 * 上传头像
	 * 
	 * @param context
	 * @param picPath
	 * @return
	 */
	public static String upAvatar(Context context, String picPath) {
		RequestParameters parameters = new RequestParameters();
		parameters.add("file", picPath);
		try {
			String str = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(UP_AVATAR).toString(),
					Utility.HTTPMETHOD_POST, parameters);
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
			Log.e("PathTrip", "pathtrip upAvatar:" + e.toString());
			return null;
		}
	}

	private static String getPicJson(String strResult) {
		String avatar_url = null;
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject js = (JSONObject) jsonParser.nextValue();
			avatar_url = js.getString(GlobalData.AVATAR_MEDIUM_URL);
//			System.out.println("avatar_small_url：" + avatar_url);
		} catch (JSONException ex) {
			avatar_url = null;
		}
		return avatar_url;
	}
}
