package com.youle.http_helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.youle.managerData.SharedPref.YLSession;
import com.youle.managerData.info.Token;
import com.youle.util.GlobalData;
import com.youle.util.OtherUtil;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Utility class for http request. 封装网络连接的最主要接口
 * 
 */

public class Utility {

	private static RequestParameters mRequestHeader = new RequestParameters();

	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String MP_BOUNDARY = "--" + BOUNDARY;
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";

	public static final String HTTPMETHOD_POST = "POST";
	public static final String HTTPMETHOD_GET = "GET";
	public static final String HTTPMETHOD_DELETE = "DELETE";
	public static final String HTTPMETHOD_PUT = "PUT";

	private static final int SET_CONNECTION_TIMEOUT = 50000;
	private static final int SET_SOCKET_TIMEOUT = 200000;
	public static Token mToken;
	public static YLSession mSession;
	public static String mTripId;
	public static String LANGUAGE = "zh";
	public static boolean hasToken() {
		return (mToken == null) ? false : true;
	}

	public static void resetToken() {
		if (mToken != null) {
			mSession.resetToken();
			mToken = null;
		}
	}

	public static boolean isSessionValid() {
		// Log.i("test", "mToken.getExpires_in():" + mToken.getExpires_in());
		return (hasToken())&&(mToken.getExpires_in() == 0
				|| ((mToken.getExpires_in() != 0) && (((int) Math.floor(System
						.currentTimeMillis() / 1000) - mToken.getCurrent_time()) < mToken
						.getExpires_in())));
	}

	
	// 设置http头,如果authParam不为空，则表示当前有token认证信息需要加入到头中
	public static void setHeader(String httpMethod, HttpUriRequest request,
			RequestParameters authParam, String url) throws RequestException {
		if (!isBundleEmpty(mRequestHeader)) {
			Log.i("test", "!isBundleEmpty(mRequestHeader");
			for (int loc = 0; loc < mRequestHeader.size(); loc++) {
				String key = mRequestHeader.getKey(loc);
				request.setHeader(key, mRequestHeader.getValue(key).toString());
			}
		}
		if (!isBundleEmpty(authParam)) {
			Log.i("test", "!isBundleEmpty(authParam)");
				String authHeader = "Basic "
						+ new String(SecBase64.encode((authParam.getValue(
								"client_id").toString()
								+ ":" + authParam.getValue("client_secret")
								.toString()).getBytes()));
				if (authHeader != null) {
					Log.i("test", "authHeader:" + authHeader);
					request.setHeader("Authorization", authHeader);

				}
		}else
		{
			Log.i("test", "----------else setHeader-----------");
			if(mToken != null && !OtherUtil.isNullOrEmpty(mToken.getAccess_token()))
			{
				Log.i("test", "Authorization Token token="+mToken.getAccess_token());
				request.setHeader("Authorization","Token token="+mToken.getAccess_token());
			}
		}
		if (LANGUAGE.equals("zh")) {
			request.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		} else {
			request.setHeader("Accept-Language", "en-US,en;q=0.8");
		}
	}

	public static boolean isBundleEmpty(RequestParameters bundle) {
		if (bundle == null || bundle.size() == 0) {
			return true;
		}
		return false;
	}

	// 填充request bundle
	public static void setRequestHeader(String key, String value) {
		// mRequestHeader.clear();
		mRequestHeader.add(key, value);
	}

	public static void setRequestHeader(RequestParameters params) {
		mRequestHeader.addAll(params);
	}

	public static void clearRequestHeader() {
		mRequestHeader.clear();

	}

	public static String encodeUrl(RequestParameters parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(parameters.getKey(loc)) + "="
					+ URLEncoder.encode(parameters.getValue(loc)));
		}
		return sb.toString();
	}

	/**
	 * Construct a url encoded entity by parameters .
	 * 
	 * @param bundle
	 *            :parameters key pairs
	 * @return UrlEncodedFormEntity: encoed entity
	 */
	public static UrlEncodedFormEntity getPostParamters(Bundle bundle)
			throws RequestException {
		if (bundle == null || bundle.isEmpty()) {
			return null;
		}
		try {
			List<NameValuePair> form = new ArrayList<NameValuePair>();
			for (String key : bundle.keySet()) {
				Log.i("test", "key:" + key);
				form.add(new BasicNameValuePair(key, bundle.getString(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form,
					"UTF-8");
			return entity;
		} catch (UnsupportedEncodingException e) {
			throw new RequestException(e);
		}
	}

	/**
	 * 对象转数组
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	/**
	 * Implement a http request and return results .
	 * 
	 * @param context
	 *            : context of activity
	 * @param url
	 *            : request url of open api
	 * @param method
	 *            : HTTP METHOD.GET, POST, DELETE
	 * @param params
	 *            : Http params , query or postparameters
	 * @return UrlEncodedFormEntity: encoed entity
	 */

	public static String openUrl(Context context, String url, String method,
			RequestParameters params) throws RequestException {
		String rlt = "";
		String file = null;
		if (params != null) {
			for (int loc = 0; loc < params.size(); loc++) {
				String key = params.getKey(loc);
				if (key.equals("img")) {
					file = params.getValue(key);
					params.remove(key);
				}
			}
		}
		if (file == null) {
			rlt = openUrl(context, url, method, params, null, null, null, null);
		} else {
			rlt = openUrl(context, url, method, params, file, "img", null,
					null);
		}
		return rlt;
	}

	public static synchronized String openTrackUrl(Context context, String url,
			String method, RequestParameters params) throws RequestException {
		String rlt = "";
		String img = null;
		String aud = null;
		if (params != null) {
			for (int loc = 0; loc < params.size(); loc++) {
				String key = params.getKey(loc);
				if (key.equals("img")) {
					img = params.getValue(key);
					if (!OtherUtil.isNullOrEmpty(img)) {
						params.remove(key);
					}
				}
				if (key.equals("aud")) {
					aud = params.getValue(key);
					if (!OtherUtil.isNullOrEmpty(aud)) {
						params.remove(key);
					}
				}
			}
		}
		rlt = openUrl(context, url, method, params, img, "img", aud, "aud");
		return rlt;
	}

	public static String openUrl(Context context, String url, String method,
			RequestParameters params, String filePic, String fileKeyPic,
			String fileAud, String fileKeyAud)
			throws RequestException {
		String result = "";
		try {
			HttpClient client = getNewHttpClient(context);
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;
			if (method.equals("GET")) {
				if (params != null)
					url = url + "?" + encodeUrl(params);
				HttpGet get = new HttpGet(url);
				request = get;
			} else if (method.equals("POST")) {
				HttpPost post = new HttpPost(url);
				byte[] data = null;
				if(OtherUtil.isNullOrEmpty(filePic) && OtherUtil.isNullOrEmpty(fileAud))
				{
					bos = new ByteArrayOutputStream(1024 * 512);
				}else
					bos = new ByteArrayOutputStream(1024 * 1024 * 2);
				Log.i("test", "--"+filePic+"--");
				if (!OtherUtil.isNullOrEmpty(filePic)&& !filePic.equals("null")
						&& OtherUtil.isNullOrEmpty(fileAud)
						) {
					Utility.paramToUpload(bos, params);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA
							+ "; boundary=" + BOUNDARY);
					Log.i("test", "filePic:" + filePic + " fileKeyPic:"
							+ fileKeyPic);
					Utility.imageContentToUpload(bos, filePic, fileKeyPic);
					bos.write(("\r\n" + END_MP_BOUNDARY).getBytes());
					Log.i("Other", "aud cos null");
				} else if (OtherUtil.isNullOrEmpty(filePic)
						&& !OtherUtil.isNullOrEmpty(fileAud)&&!fileAud.equals("null")
						) {
					Utility.paramToUpload(bos, params);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA
							+ "; boundary=" + BOUNDARY);
					Utility.imageContentToUpload(bos, fileAud, fileKeyAud);
					bos.write(("\r\n" + END_MP_BOUNDARY).getBytes());
					Log.i("Other", "pic cos null");
				}else {
					post.setHeader("Content-Type",
							"application/x-www-form-urlencoded");
					String postParam = encodeParameters(params);
					data = postParam.getBytes();
					bos.write(data);
					Log.i("Other", "else null");
				}
				try {
					data = bos.toByteArray();
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					Log.e("Utility", "Utility e:"+e.toString());
				}
				bos.close();
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
				// 绑定到请求 Entry
				request = post;
			} else if (method.equals("DELETE")) {
				url = url + "?" + encodeUrl(params);
				request = new HttpDelete(url);
			} else if (method.equals("PUT")) {
				HttpPut put = new HttpPut(url);
				byte[] data = null;
				bos = new ByteArrayOutputStream(1024 * 10);
				put.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				String postParam = encodeParameters(params);
				data = postParam.getBytes("UTF-8");
				bos.write(data);
				data = bos.toByteArray();
				bos.close();
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				put.setEntity(formEntity);
				// 绑定到请求 Entry
				request = put;
			}
			if (params != null && params.getLocation("client_id") != -1) {
				setHeader(method, request, params, url);
			} else {
				setHeader(method, request, null, url);
			}
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			Log.i("test", "----statusCode:" + statusCode);
			if (method.equals("GET") && statusCode == 400) {
				return statusCode + "";
			}
			if(statusCode == 200)
			{
				result = read(response);
				// Log.i("test", "200 right:" + result);
//				if (TextUtils.isEmpty(result)) {
//					result = statusCode + "";
//				}
				return GlobalData.RESULT_OK+result;
			}
			if (statusCode == 400) {
				result = read(response);
				Log.i("test", "400--error:" + result);
				return regErrorJson(result);
			} 
			else if(statusCode == 401)
			{
				result = read(response);
				Log.i("test", statusCode+"--error:" + result);
				return statusCode + "";
			}else if (statusCode == 204) {
				Log.i("test", "204:"+response.toString());
				return statusCode + "";
			}else {
				result = read(response);//502
				Log.i("test", "other--error statusCode:" +statusCode);
				return statusCode + "";
			}
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	private static String regErrorJson(String strResult) {
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject js = (JSONObject) jsonParser.nextValue();
			String error_des = js
					.getString(GlobalData.ERROR_DESCRIPTION);
//			System.out.println("error_description：" + js
//					.getString(GlobalData.ERROR_DESCRIPTION));
			return error_des;
		} catch (JSONException ex) {
			// 异常处理代码
			return null;
		}
	}

	public static HttpClient getNewHttpClient(Context context) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			// Set the default socket timeout (SO_TIMEOUT) // in
			// milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setConnectionTimeout(params,
					Utility.SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params,
					Utility.SET_SOCKET_TIMEOUT);
			HttpClient client = new DefaultHttpClient(ccm, params);
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i("test", "bbbbbbbbbbbbbbbbbbbbb");
				// 获取当前正在使用的APN接入点
				Uri uri = Uri.parse("content://telephony/carriers/preferapn");
				Cursor mCursor = context.getContentResolver().query(uri, null,
						null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					// 游标移至第一条记录，当然也只有一条
					String proxyStr = mCursor.getString(mCursor
							.getColumnIndex("proxy"));
					if (proxyStr != null && proxyStr.trim().length() > 0) {
						HttpHost proxy = new HttpHost(proxyStr, 80);
						client.getParams().setParameter(
								ConnRouteParams.DEFAULT_PROXY, proxy);
					}
					mCursor.close();
				}
			}
			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	/**
	 * Upload image into output stream .
	 * 
	 * @param out
	 *            : output stream for uploading
	 * @param imgpath
	 *            : bitmap for uploading
	 * @return void
	 */
	private static void imageContentToUpload(OutputStream out, String path,
			String fileKey) throws RequestException {
		File ff = new File(path);
		StringBuilder temp = new StringBuilder();
		Log.i("Other", "utility path:" + path + " ff.getName():" + ff.getName()
				+ " fileKey:" + fileKey);
		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append(
				"Content-Disposition: form-data; name=\"" + fileKey
						+ "\"; filename=\"").append(ff.getName())
				.append("\"\r\n");
		String filetype = "application/octet-stream";
		temp.append("Content-Type: ").append(filetype)
				.append("; charset=UTF-8").append("\r\n\r\n");
		BufferedInputStream bis = null;
		
			byte[] res = temp.toString().getBytes();
		try {
			out.write(res);
			if (ff != null) {
				InputStream is = new FileInputStream(ff);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					out.write(bytes, 0, len);
				}
				is.close();

			}
			out.write("\r\n".getBytes("UTF-8"));
			// out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			Log.i("Other", "utility imageContentToUpload error");
			throw new RequestException(e);
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new RequestException(e);
				}
			}
		}
	}

	/**
	 * Upload contents into output stream .
	 * 
	 * @param baos
	 *            : output stream for uploading
	 * @param params
	 *            : post parameters for uploading
	 * @return void
	 */
	private static void paramToUpload(OutputStream baos,
			RequestParameters params) throws RequestException {
		String key = "";
		for (int loc = 0; loc < params.size(); loc++) {
			// if(params.getKey(""))
			key = params.getKey(loc);
			 Log.i("test", "key:" + key);
			StringBuilder temp = new StringBuilder(10);
			temp.setLength(0);
			temp.append(MP_BOUNDARY).append("\r\n");
			temp.append("content-disposition: form-data; name=\"").append(key)
					.append("\"\r\n\r\n");
//			Log.i("test", "params.getValue(key):"+params.getValue(key).toString());
			if(OtherUtil.isNullOrEmpty(params.getValue(key)))
			{
				temp.append("").append("\r\n");
			}else
				temp.append(params.getValue(key).toString()).append("\r\n");
			try {
				byte[] res = temp.toString().getBytes("UTF-8");
				try {
					baos.write(res);
				} catch (IOException e) {
					throw new RequestException(e);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read http requests result from response .
	 * 
	 * @param response
	 *            : http response by executing httpclient
	 * 
	 * @return String : http response content
	 */
	private static String read(HttpResponse response) throws RequestException {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null
					&& header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	/**
	 * Read http requests result from inputstream .
	 * 
	 * @param inputstream
	 *            : http inputstream from HttpConnection
	 * 
	 * @return String : http response content
	 */
	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	/**
	 * Clear current context cookies .
	 * 
	 * @param context
	 *            : current activity context.
	 * 
	 * @return void
	 */
	public static void clearCookies(Context context) {
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager
				.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	public static String encodeParameters(RequestParameters httpParams) {
		if (null == httpParams || Utility.isBundleEmpty(httpParams)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++) {
			String key = httpParams.getKey(loc);
			if (j != 0) {
				buf.append("&");
			}
			try {
				String content = httpParams.getValue(key);
				if (!OtherUtil.isNullOrEmpty(content))
					buf.append(URLEncoder.encode(key, "UTF-8"))
							.append("=")
							.append(URLEncoder.encode(content.toString(),
									"UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		
		for (int i = 0,len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount()+2));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
}
