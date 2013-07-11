package com.youle.managerData.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.amap.api.maps.model.LatLng;

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

	public void saveCity(int id,String city) {
        editor.putInt("location_city_id",id);
		editor.putString("location_city", city);
		editor.commit();
	}
    public int getCityId(){
        return sharedPref.getInt("location_city_id",0);
    }

	public String getCity() {
		return sharedPref.getString("location_city", null);
	}
    public void saveRadio(int id,String name){
        editor.putInt("radio_id",id);
        editor.putString("radio_name", name);
        editor.commit();
    }
    public int getRadioId(){
        return sharedPref.getInt("radio_id",1);
    }
    public String getRadioName(){
        return sharedPref.getString("radio_name",null);
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
    public void resetSinaQQ()
    {
    	editor.putBoolean("sina_status",false);
        editor.putString("sina_nickname",null);
        editor.putBoolean("qq_status",false);
        editor.putString("qq_nickname",null);
        editor.commit();
    }
    public boolean getQQStatus(){
        return sharedPref.getBoolean("qq_status",false);
    }
    public String getQQnickname(){
        return sharedPref.getString("qq_nickname",null);
    }
    public void saveLatLngCity(double lat,double lng,String city){
        editor.putString("lat",lat+"");
        editor.putString("lng",lng+"");
        editor.putString("city",city);
        editor.commit();
    }
    public LatLng getLatLng(){
        Double lat=Double.parseDouble(sharedPref.getString("lat","0"));
        Double lng=Double.parseDouble(sharedPref.getString("lng","0"));
        return new LatLng(lat,lng);
    }
    public String getLocCity(){
        return sharedPref.getString("city","");
    }

}
