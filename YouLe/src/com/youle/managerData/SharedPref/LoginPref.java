package com.youle.managerData.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LoginPref {
	private static final String FIRST_LOGIN = "first_login";
	private SharedPreferences sharedPref;
	private Editor editor;
	private static final String SHARED = "first_Pref";
	private Context context;

	public LoginPref(Context context) {
		this.context = context;
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	public void storeLogin(Boolean first) {
		editor.putBoolean(FIRST_LOGIN, first);
		editor.commit();
	}

	public void resetLogin() {
		editor.putBoolean(FIRST_LOGIN, true);
		editor.commit();
	}

	public Boolean getLogin() {
		return sharedPref.getBoolean(FIRST_LOGIN, true);
	}
}
