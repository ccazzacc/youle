package com.youle.util;


import com.koushikdutta.async.http.AsyncHttpClientMiddleware.GetSocketData;
import com.youle.R;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static void show(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void show(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	public static void showToast(Context context, String result) {
		if(result.equals(GlobalData.ERROR_204) || result.equals(GlobalData.ERROR_404) || result.equals(GlobalData.ERROR_502))
		{
			show(context, "http:"+result);
			return;
		}
		else
		{
			show(context, result);
			return;
		}
	}
}
