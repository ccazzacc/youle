package com.youle.util;


import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ToastUtil {
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	
}
