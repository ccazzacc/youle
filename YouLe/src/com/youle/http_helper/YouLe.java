package com.youle.http_helper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.amap.api.maps.model.LatLng;
import com.youle.managerData.SharedPref.YLSession;
import com.youle.managerData.info.MainInfo;
import com.youle.managerData.info.Token;
import com.youle.util.GlobalData;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


public class YouLe {
	public static String client_id = "469192608";
	public static String client_secret = "9bc4ce21147e3bc9";
	public static final String BASE_URL = "http://218.6.224.55:8000/";
	private static final String REGISTER_URL = "account/signup";// 注册URL
	private static final String UP_AVATAR = "account/avatar";// 上传头像URL
	private static final String UP_COVER = "account/cover";//上传封面
	private static final String OAUTH = "oauth2/access_token";// oauth认证
	private static final String INFO = "tracks";//获取信息列表
	private static final String U_INFO = "member";
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
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.EMAIL, email);
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
			Log.e("YouLe", "YouLe loginApp:" + e.toString());
			return GlobalData.REQUEST_FAIL;
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
			String refresh_token) {
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.REFRESH_TOKEN, refresh_token);
		parameters.add(GlobalData.GRANT_TYPE, GlobalData.REFRESH_TOKEN);
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
			Log.e("YouLe", "YouLe refreshToken:" + e.toString());
			return GlobalData.REQUEST_FAIL;
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
		parameters.add(GlobalData.USER_NAME, username);
		parameters.add(GlobalData.EMAIL, email);
		parameters.add(GlobalData.PASSWORD, password);

		try {
			String result = Utility.openUrl(context,
					new StringBuffer().append(BASE_URL).append(REGISTER_URL).toString(), Utility.HTTPMETHOD_POST,
					parameters);
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
			Log.e("YouLe", "YouLe register:" + e.toString());
			return GlobalData.REQUEST_FAIL;
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
	 * 第三方注册/登录
	 * 
	 * @param context
	 * @param pForm
	 * @param uId
	 * @param uName
	 * @return
	 */
	public static String ThirdPlate(Context context, String pForm,String uId, String uName)
	{
		RequestParameters parameters = new RequestParameters();
		parameters.add(GlobalData.CLIENT_ID, client_id);
		parameters.add(GlobalData.CLIENT_SECRET, client_secret);
		parameters.add(GlobalData.P_FORM, pForm);
		parameters.add(GlobalData.U_ID, uId);
		parameters.add(GlobalData.USER_NAME, uName);
		parameters.add(GlobalData.GRANT_TYPE, "platform");
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
			Log.e("YouLe", "YouLe loginApp:" + e.toString());
			return GlobalData.REQUEST_FAIL;
		}
	}
	/**上传封面
	 * @param context
	 * @param img
	 */
	public static String upCover(Context context,String img)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.IMG, img);
		try {
			String res = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(UP_COVER).toString(),
					Utility.HTTPMETHOD_POST, params);
			Log.i("YouLe", "cover res：" + res);
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
		return GlobalData.REQUEST_FAIL;
	}
	private static String getCoverJson(String strResult) {
		String c_url = null;
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject js = (JSONObject) jsonParser.nextValue();
			c_url = js.getString(GlobalData.COVER_URL);
			Log.i("YouLe", "c_url：" + c_url);
		} catch (JSONException ex) {
			c_url = null;
		}
		return c_url;
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
		parameters.add(GlobalData.IMG, picPath);
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
			Log.e("YouLe", "YouLe upAvatar:" + e.toString());
			return GlobalData.REQUEST_FAIL;
		}
	}
	//http://218.6.224.55:8000/avatars/256/100003.jpg
	private static String getPicJson(String strResult) {
		String avatar_url = null;
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject js = (JSONObject) jsonParser.nextValue();
			StringBuffer sb = new StringBuffer();
			sb.append(js.getString(GlobalData.URI_PREFIX)).append(js.getString(GlobalData.SIZES).substring(1, 4)).append("/").append(js.getString(GlobalData.AVATAR));
			avatar_url = sb.toString();
			Log.i("YouLe", "avatar_small_url：" + avatar_url);
		} catch (JSONException ex) {
			avatar_url = null;
		}
		return avatar_url;
	}
	/**获取信息列表
	 * @param context
	 * @param distance
	 * @param lng
	 * @param lat
	 * @param size
	 */
	public static String getInfoList(Context context,int distance,double lng,double lat,int size)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.DISTANCE, Integer.toString(distance));
		if(distance > 0)
		{
			params.add(GlobalData.LNG, Double.toString(lng));
			params.add(GlobalData.LAT, Double.toString(lat));
		}
//		params.add(GlobalData.PAGE, Integer.toString(page));
		params.add(GlobalData.SIZE, Integer.toString(size));
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(INFO).toString(),
					Utility.HTTPMETHOD_GET, params);
			Log.i("YouLe", "getInfoList:"+result);
			return result;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return GlobalData.REQUEST_FAIL;
		}
	}
	public static List<MainInfo> jsonInfo(String res)
	{
		String tStr = res.substring(3);
		try {
			List<MainInfo> list = new ArrayList<MainInfo>();
			JSONObject js = new JSONObject(tStr);
			if(js.getInt(GlobalData.TOTAL) > 0)
			{
				JSONArray array = js.getJSONArray(GlobalData.TRACKS);
				JSONObject obj;
				for(int i = 0,j = array.length();i<j;i++)
				{
					obj = array.optJSONObject(i);
					String latLng = obj.getString(GlobalData.LOCATION);
					list.add(new MainInfo(
							obj.getString(GlobalData.TRACK_ID), 
							obj.getString(GlobalData.USER_ID), 
							obj.getString(GlobalData.AVATAR_URL),
                            obj.getInt(GlobalData.CREATED),
							obj.getString(GlobalData.TEXT),
                            obj.getInt(GlobalData.MARK),
							obj.getString(GlobalData.AUDIO_URL), 
							obj.getInt(GlobalData.AUDIO_TIME), 
							obj.getString(GlobalData.IMAGE_URL), 
							obj.getString(GlobalData.ORIGIN_IMAGE_URL), 
							new LatLng(Double.parseDouble(latLng.substring(latLng.indexOf(",")+1,latLng.lastIndexOf("]"))),Double.parseDouble(latLng.substring(1, latLng.indexOf(",")))),
							obj.getString(GlobalData.PLACE),
							obj.getInt(GlobalData.FLAGS),
							obj.getInt(GlobalData.WIDTH),
							obj.getInt(GlobalData.HEIGHT)));
				}
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**发布信息
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
	public static void upTrack(Context context,double lng,double lat,String place,double spd,int mark,String aud,int aud_t,String img,String text)
	{
		RequestParameters params = new RequestParameters();
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
			String result = Utility.openTrackUrl(context, new StringBuffer().append(BASE_URL).append(INFO).toString(),
					Utility.HTTPMETHOD_POST, params);
			Log.i("YouLe", "upTrack:"+result);
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**获取指定用户信息
	 * @param context
	 * @param uId
	 */
	public static void getUserInfo(Context context,String uId)
	{
		RequestParameters params = new RequestParameters();
		params.add(GlobalData.USER_ID, uId);
		try {
			String result = Utility.openUrl(context, new StringBuffer().append(BASE_URL).append(U_INFO).toString(), Utility.HTTPMETHOD_GET, params);
			Log.i("YouLe", "getUserInfo result:"+result);
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void jsonUserInfo()
	{
		
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
