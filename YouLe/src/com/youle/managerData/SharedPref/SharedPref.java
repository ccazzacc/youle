package com.youle.managerData.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {
	private SharedPreferences sharedPref;
	private Editor editor;
	private Context context;
	private static final String SHARED = "Zfc_Preferences";

	public SharedPref(Context context) {
		this.context = context;
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	public void saveCity(String city) {
		editor.putString("location_city", city);
		editor.commit();
	}

	public String getCity() {
		return sharedPref.getString("location_city", "s");
	}

}
