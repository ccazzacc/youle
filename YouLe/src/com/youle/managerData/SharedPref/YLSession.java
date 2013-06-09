package com.youle.managerData.SharedPref;


import com.youle.managerData.info.Token;
import com.youle.util.OtherUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class YLSession {
	private static final String ACCESS_TOKEN = "Access_token";
	private static final String EXPIRES_IN = "Expires_in";
	private static final String REFRESH_TOKEN = "Refresh_token";
	private static final String USER_ID = "User_id";
	private static final String CUR_TIME = "Cur_time";
	private SharedPreferences sharedPref;
	private Editor editor;
	private static final String SHARED = "Travel_Preferences";
	private Context context;
	public YLSession(Context context) {
		this.context = context;
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	public void storeToken(Token token) {
		editor.putString(ACCESS_TOKEN, token.getAccess_token());
		editor.putLong(EXPIRES_IN, token.getExpires_in());
		editor.putString(REFRESH_TOKEN, token.getRefresh_token());
		editor.putString(USER_ID, token.getUser_id());
		editor.putInt(CUR_TIME, token.getCurrent_time());
//		new UserIdShared(context).isStore(token.getUser_id());
		editor.commit();
	}
	
	
	
	public void resetToken() {
		editor.putString(ACCESS_TOKEN, null);
		editor.putLong(EXPIRES_IN, 0);
		editor.putString(REFRESH_TOKEN, null);
		editor.putLong(USER_ID, 0);
		editor.putInt(CUR_TIME, 0);
		editor.commit();
	}

	public Token getToken() {
		String access_token = sharedPref.getString(ACCESS_TOKEN, null);
		long expires_in = sharedPref.getLong(EXPIRES_IN, 0);
		String refresh_token = sharedPref.getString(REFRESH_TOKEN, null);
		String user_id = sharedPref.getString(USER_ID, null);
		int cur_time = sharedPref.getInt(CUR_TIME, 0);
		if (!OtherUtil.isNullOrEmpty(access_token) && !OtherUtil.isNullOrEmpty(refresh_token)
				&& !OtherUtil.isNullOrEmpty(user_id) && cur_time != 0)
			return new Token(access_token, expires_in, refresh_token, user_id,cur_time);
		else
			return null;
	}
}
