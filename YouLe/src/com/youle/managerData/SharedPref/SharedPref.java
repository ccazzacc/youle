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
		return sharedPref.getString("location_city", "");
	}

    //保存sina和qq是否认证过及用户名
    public void saveSinaStatus(boolean b,String un){
        editor.putBoolean("sina_status",b);
        editor.putString("sina_nickname",un);
        editor.commit();
    }
    public boolean getSinaStatus(){
        return sharedPref.getBoolean("sina_status",false);
    }
    public String getSinaNickname(){
        return sharedPref.getString("sina_nickname",null);
    }
    public void saveQQStatus(boolean b,String un){
        editor.putBoolean("qq_status",b);
        editor.putString("qq_nickname",un);
        editor.commit();
    }
    public boolean getQQStatus(){
        return sharedPref.getBoolean("qq_status",false);
    }
    public String getQQnickname(){
        return sharedPref.getString("qq_nickname",null);
    }

}
