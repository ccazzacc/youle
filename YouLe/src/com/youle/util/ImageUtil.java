package com.youle.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;

public class ImageUtil {
	private Context context;
	public ImageUtil(Context context)
	{
		this.context = context;
	}
	/**
	 * 此处写方法描述
	 * @Title: getBitmap
	 * @param intent
	 * @return void
	 */
	public Bitmap getBitmap(Uri uri) {
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = getInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}
		return bitmap;
	}

	/**
	 * 获取输入流
	 * 
	 * @Title: getInputStream
	 * @param mUri
	 * @return
	 * @return InputStream
	 */
	public InputStream getInputStream(Uri mUri) throws IOException {
		try {
			if (mUri.getScheme().equals("file")) {
				return new java.io.FileInputStream(mUri.getPath());
			} else {
				return context.getContentResolver().openInputStream(mUri);
			}
		} catch (FileNotFoundException ex) {
			return null;
		}
	}


	/**
	 * 剪裁之前的预处理
	 * 
	 * @Title: preCrop
	 * @param uri
	 * @param duplicatePath
	 * @return
	 * @return Uri
	 */
	public Uri preCrop(Uri uri, String duplicatePath) {
		Uri duplicateUri = null;
		if(null != uri && !OtherUtil.isNullOrEmpty(uri.toString()))
		{
			if (duplicatePath == null) {
				duplicateUri = getDuplicateUri(uri);
			} else {
				duplicateUri = getDuplicateUri(uri, duplicatePath);
			}

		}
		
		// rotateImage();
		return duplicateUri;
	}

	/**
	 * 设置获取裁剪后图像的uri
	 * 
	 * @Title: getDuplicateUri
	 * @param uri
	 * @return
	 * @return Uri
	 */
	public Uri getDuplicateUri(Uri uri) {
		Uri duplicateUri = null;

		String uriString = getUriString(uri);

		duplicateUri = getDuplicateUri(uri, uriString);

		return duplicateUri;
	}

	/**
	 * 如果是拍照的话就直接获取了
	 * 
	 * @Title: getDuplicateUri
	 * @param uri
	 * @param uriString
	 * @return
	 * @return Uri
	 */
	public Uri getDuplicateUri(Uri uri, String uriString) {

		Uri duplicateUri = null;
		String duplicatePath = null;
		duplicatePath = uriString.replace(".", "_duplicate.");

		// cropImagePath = uriString;
		// 判断原图是否旋转，旋转了进行修复
		rotateImage(uriString);

		duplicateUri = Uri.fromFile(new File(duplicatePath));

		return duplicateUri;
	}

	/**
	 * 旋转图象
	 * 
	 * @Title: rotateImage
	 * @return void
	 */
	public void rotateImage(String uriString) {

		try {
			ExifInterface exifInterface = new ExifInterface(uriString);

			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90
					|| orientation == ExifInterface.ORIENTATION_ROTATE_180
					|| orientation == ExifInterface.ORIENTATION_ROTATE_270) {

				String value = String.valueOf(orientation);
				exifInterface
						.setAttribute(ExifInterface.TAG_ORIENTATION, value);
				// exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
				// "no");
				exifInterface.saveAttributes();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据Uri获取文件的路径
	 * 
	 * @Title: getUriString
	 * @param uri
	 * @return
	 * @return String
	 */
	public String getUriString(Uri uri) {
		String imgPath = null;
		if (uri != null) {
			String uriString = uri.toString();
			// 小米手机的适配问题，小米手机的uri以file开头，其他的手机都以content开头
			// 以content开头的uri表明图片插入数据库中了，而以file开头表示没有插入数据库
			// 所以就不能通过query来查询，否则获取的cursor会为null。
			if (uriString.startsWith("file")) {
				// uri的格式为file:///mnt....,将前七个过滤掉获取路径
				imgPath = uriString.substring(7, uriString.length());
				return imgPath;
			}
			Cursor cursor = context.getContentResolver().query(uri, null, null, null,
					null);
			cursor.moveToFirst();
			imgPath = cursor.getString(1); // 图片文件路径
			cursor.close();
		}
		return imgPath;
	}
}
