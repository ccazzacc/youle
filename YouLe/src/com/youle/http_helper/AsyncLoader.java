package com.youle.http_helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.youle.util.MD5;
import com.youle.util.OtherUtil;

import android.os.Handler;

public class AsyncLoader {
	private ExecutorService executorService = Executors.newFixedThreadPool(3);// 线程池中线程数量
	private final Handler handler = new Handler();

	// 对外界开放的回调接口
	public interface ImageCallback {
		public String mediaLoaded(String mediaUrl);
	}

	/*
	 * 从网络上获取音频，如果音频在本地存在的话就直接拿，如果不存在再去服务器上下载 这里的path是音频的地址
	 */
	public String loadMedia(final String path, final ImageCallback callback) {
		String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
		final File file = OtherUtil.fileCreate(name, true);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (file != null && file.exists()) {
			return file.getAbsolutePath();// Uri.fromFile(path)这个方法能得到文件的URI
		}
		// 缓存中没有，则从网络上取出数据，并将取出的数据缓存到内存中
		executorService.submit(new Runnable() {
			public void run() {
				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					if (conn.getResponseCode() == 200) {

						InputStream is = conn.getInputStream();
						FileOutputStream fos = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
						is.close();
						fos.close();
						handler.post(new Runnable() {
							public void run() {
								callback.mediaLoaded(file.getAbsolutePath());
							}
						});
					}

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		return null;
	}

}
