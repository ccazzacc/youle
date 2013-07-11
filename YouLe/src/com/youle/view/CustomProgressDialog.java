
package com.youle.view;


import com.youle.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;

	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}
	public static void showMsg(Context context,String strMessage)
	{
		if (customProgressDialog == null)
			customProgressDialog = createDialog(context);
		customProgressDialog.setCancelable(false);
		customProgressDialog.setMessage(strMessage);
		customProgressDialog.show();
	}
	public static void showTitle(Context context,String strTitle)
	{
		customProgressDialog = createDialog(context);
		customProgressDialog.setCancelable(false);
		customProgressDialog.setMessage(strTitle);
		customProgressDialog.show();
	}
	public static void stopProgressDialog(Context context){
		if (customProgressDialog == null)
			customProgressDialog = createDialog(context);
		if (customProgressDialog != null){
			customProgressDialog.dismiss();
			customProgressDialog = null;
		}
	}
	public static CustomProgressDialog createDialog(Context context) {
		customProgressDialog = new CustomProgressDialog(context,
				R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null)
			return;
		// ImageView imageView = (ImageView)
		// customProgressDialog.findViewById(R.id.loadingImageView);
		// AnimationDrawable animationDrawable = (AnimationDrawable)
		// imageView.getBackground();
		// animationDrawable.start();
	}

	public CustomProgressDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	public CustomProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}
}
