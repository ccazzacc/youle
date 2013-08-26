package com.youle.util;

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
		if(OtherUtil.isNullOrEmpty(result))
			return;
		if (result.equals("404")
				|| result.equals("401")
				|| result.equals("403")
				|| result.equals("502")
				|| result.equals("204")) {
			show(context, "http:" + result);
			return;
		} else
			show(context, result);
	}
}
