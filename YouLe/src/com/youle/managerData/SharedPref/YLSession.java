package com.youle.managerData.SharedPref;


import com.youle.managerData.info.MeInfo;
import com.youle.managerData.info.Token;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class YLSession {
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
		editor.putString(GlobalData.ACCESS_TOKEN, token.getAccess_token());
		editor.putLong(GlobalData.EXPIRES_IN, token.getExpires_in());
		editor.putString(GlobalData.REFRESH_TOKEN, token.getRefresh_token());
		editor.commit();
	}
	public void storeMe(MeInfo info)
	{
		editor.putString(GlobalData.USER_ID, info.getUserId());
		editor.putInt(GlobalData.TYPE, info.getType());
		editor.putString(GlobalData.AVATAR_URL, info.getAvaUrl());
		editor.commit();
	}
	public void storeMe(String uId,int type,String avaUrl)
	{
		editor.putString(GlobalData.USER_ID, uId);
		editor.putInt(GlobalData.TYPE, type);
		editor.putString(GlobalData.AVATAR_URL, avaUrl);
		editor.commit();
	}
	public MeInfo getMe()
	{
		return new MeInfo(sharedPref.getString(GlobalData.USER_NAME, null), 
				sharedPref.getString(GlobalData.USER_ID, null), 
				sharedPref.getInt(GlobalData.TYPE, 0), 
				sharedPref.getInt(GlobalData.GENDER, 0), 
				sharedPref.getString(GlobalData.AVATAR_URL, null), 
				"", "");
	}
	public String getUserId()
	{
		return sharedPref.getString(GlobalData.USER_ID, null);
	}
	public void resetToken() {
		editor.putString(GlobalData.ACCESS_TOKEN, null);
		editor.putLong(GlobalData.EXPIRES_IN, 0);
		editor.putString(GlobalData.REFRESH_TOKEN, null);
		editor.putString(GlobalData.USER_ID, null);
		editor.putInt(GlobalData.TYPE, 0);
		editor.putString(GlobalData.AVATAR_URL, null);
		editor.putBoolean("shop", false);
		editor.putBoolean("taxi", false);
		editor.commit();
	}
	public void storeShop()
	{
		editor.putBoolean("shop", true);
		editor.commit();
	}
	public void storeTaxi()
	{
		editor.putBoolean("taxi", true);
		editor.commit();
	}
	public boolean getShopTaxi()
	{
		return (sharedPref.getBoolean("shop", false)||sharedPref.getBoolean("taxi", false));
	}
	public Token getToken() {
		String access_token = sharedPref.getString(GlobalData.ACCESS_TOKEN, null);
		long expires_in = sharedPref.getLong(GlobalData.EXPIRES_IN, 0);
		String refresh_token = sharedPref.getString(GlobalData.REFRESH_TOKEN, null);
		if (!OtherUtil.isNullOrEmpty(access_token) && !OtherUtil.isNullOrEmpty(refresh_token))
			return new Token(access_token, expires_in, refresh_token);
		else
			return null;
	}
	
}
