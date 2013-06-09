package com.youle.util;

public class GlobalData {
	// 拍照
	public static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	public static final int PHOTO_REQUEST_GALLERY = 2;
	// 头像裁剪
	public static final int AVA_REQUEST_TAKEPHOTO = 3;
	public static final int AVA_REQUEST_GALLERY = 4;
	public static final int PHOTO_REQUEST_CUT = 5;
	public final static int REQ_CODE_PHOTO_CROP = 102;
	// 录音
	public static final int SOUND_REQUEST = 6;
	public static final int SOUND_RESULT = 100;
	public final static String ACTION_CROP_IMAGE = "android.intent.action.CROP";
	public final static String IMAGE_URI = "image_uri";
	public final static String CROP_IMAGE_URI = "crop_image_uri";
	// key
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	// DEVICE_TOKEN
	public static final String TOKEN = "token";
	public static final String CLIENT_TYPE = "client_type";
	public static final String SQL_Tab = "T";
	// 登录
	public static final String GRANT_TYPE = "grant_type";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String U_ID = "uid";
	// 注册
	public static final String EMAIL = "email";
	public static final String P_FORM = "platform";
	// 获取信息列表（参数）
	public static final String DISTANCE = "distance";
	public static final String LNG = "lng";
	public static final String LAT = "lat";
	public static final String PAGE = "page";
	public static final String SIZE = "size";
	//用户封面上传
	public static final String COVER_URL = "cover_url";
	//用户头像上传
	public static final String URI_PREFIX = "uri_prefix";
	public static final String AVATAR = "avatar";
	public static final String SIZES = "sizes";
	//发布信息
	public static final String SPD = "spd";
	public static final String AUD = "aud";
	public static final String AUD_T = "aud_t";
	public static final String IMG = "img";
	public static final String TXT = "txt";
	// json用
	public static final String ACCESS_TOKEN = "access_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String USER_ID = "user_id";
	public static final String ERROR_CODE = "error_code";
	public static final String ERROR_DESCRIPTION = "error_description";
	public static final String AVATAR_SMALL_URL = "avatar_small_url";
	// 获取信息列表
	public static final String TOTAL = "total";
	public static final String TRACKS = "tracks";
	public static final String TRACK_ID = "track_id";
	public static final String AVATAR_URL = "avatar_url";
	public static final String CREATED = "created";
	public static final String TEXT = "text";
	public static final String MARK = "mark";
	public static final String AUDIO_URL = "audio_url";
	public static final String AUDIO_TIME = "audio_time";
	public static final String IMAGE_URL = "image_url";
	public static final String ORIGIN_IMAGE_URL = "origin_image_url";
	public static final String LOCATION = "location";
	public static final String PLACE = "place";
	public static final String FLAGS = "flags";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	
	
	// 地址解析
	public static final String RESULTS = "results";
	public static final String ADDRESS_COMP = "address_components";
	public static final String LONG_NAME = "long_name";
	public static final String TYPES = "types";

	// 是否修改
	public static final String IS_UPDATE = "is_update";
	// 结果为null
	public static final String RESULT_NULL_CODE = "result null";
	// 正确
	public static final int RESULT_OK_CODE = 200;
	public static final String RESULT_OK = "999";
	public static final String JSON_USERID_ERROR = "userId error";
	// json错误
	public static final String JSON_ERROR_CODE = "103";
	// 异常
	public static final String EXCEPTION_CODE = "RequestException";
	// 经纬度为null
	public static final String LAT_LNG_NULL = "lat lng null";

//	public static final String CLIENT_ERROR = "100004";
//	public static final String NO_GRANT_TYPE = "100005";
//	public static final String OAUTH_FAIL = "10006";// 用户鉴权失败
//	public static final String NO_REFRESH_TOKEN = "100007";
//	public static final String REFRESH_TOKEN_NOEXIST = "100008";
//	public static final String ACCESS_TOKEN_NO_REFRESH = "100009";
//	public static final String LINITE_ACCASS = "100010";// 访问受限
//	public static final String UNKNOW_ERROR = "100011";
//	public static final String COPY_EMAIL = "20006";// 邮箱重复
//	public static final String COPY_USER = "20007";// 用户名重复
//	public static final String NO_USER = "200009";
//	public static final String NOT_FOLLOW_ME = "20010";// 用户不能关注自己
//	public static final String NO_TRIP_ID = "300001";
//	public static final String INVALID_TRIP_ID = "300002";
//	public static final String NO_TRIP = "30004";
//	public static final String INVALID_TRACK_ID = "300007";
//	public static final String NO_TRACK = "300008";
//	public static final String UNFINISH_TRIP = "30026";
//	public static final String UPFILE_ERROR = "500001";
	// 刷新accesstoken时的错误
	public static final String ERROR_INVALID_ACCESS_TOKEN_CODE = "401";
	public static final String ERROR_502 = "502";
	public static final String ERROR_204 = "204";
	public static final String ERROR_404 = "404";
	public static final String ERROR_401 = "401";
	public static final String REQUEST_FAIL = "request fail";
	public static final String YOULE = "/youle/";
	public static final String CACHE = "/.cache";
	public static String SOUND_PATH = "";
	public static int autio_time = 0;

	// 发送定位广播
	public static String BROADCAST_COUNTER_ACTION = "com.youle.chooseCity";
	public static String COUNTER_VALUE = "getloaction";
	
	public static final int MENU_STATE_CLOSE = 0;
	public static final int MENU_STATE_OPEN = 1;
	
	public static final int DURATION_TIME = 500; 	//滑动持续时间
	
	public static int screenWidth; // 屏幕宽度
	public static int screenHeight; // 屏幕高度（不包含状态栏）
	public static float screenDensity;	//屏幕密度
	
	
	
	public static void setScreenSize(int width, int height, float density){
		screenWidth = width;
		screenHeight = height;
		screenDensity = density;
	}
}
