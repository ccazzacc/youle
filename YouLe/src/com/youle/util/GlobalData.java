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
	public final static String ACTION_CROP_IMAGE = "android.intent.action.CROP";
	public final static String IMAGE_URI = "image_uri";
	public final static String CROP_IMAGE_URI = "crop_image_uri";
	// key
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	// 登录
	public static final String GRANT_TYPE = "grant_type";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String NEW_PASSWORD = "new_password";
	public static final String U_ID = "uid";
	// 注册
	public static final String EMAIL = "email";
	public static final String DEVICE_TOKEN = "device_token";
	public static final String P_FORM = "platform";
	public static final String MEMBER_INFO = "member_info";
	public static final String GENDER = "gender";
	public static final String PRIVATE_UNREAD = "private_unread";
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
	public static final String TYPE = "type";
	public static final String LEVEL = "level";
	public static final String POINTS = "points";
	//优惠券
	public static final String MAP = "map";
	public static final String CODE = "code";
	public static final String EXPIRED = "expired";
	public static final String COUPONS = "coupons";
	public static final String MERCHANT_ID = "merchant_id";
	public static final String MERCHANT = "merchant";
	public static final String COUPON = "coupon";
	public static final String EXPIRE_AT = "expire_at";
	public static final String MERCHANT_NAME = "merchant_name";
	public static final String TO_DATE = "to_date";
	public static final String QR_CODE = "qr_code";
	public static final String CONFIRM = "confirm";
	// 获取信息列表
    public static final String USER_TYPE = "user_type";	
	public static final String RADIO_ID = "radio_id";
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
	//申请成为商家
	public static final String CITY_ID = "city_id";
	public static final String CATEGORY_ID = "category_id";
	public static final String NAME="name";
	public static final String ADDRESS="address";
	public static final String PHONE= "phone";
	public static final String CONTACT = "contact";
	public static final String LICENSE = "license";
	public static final String COMPANY = "company";
	public static final String NO = "no";
	//获取论坛列表
	public static final String FORUMS = "forums";
	public static final String LOGO_URL = "logo_url";
	public static final String TOTAL_POSTS = "total_posts";
	public static final String FORUM_ID = "forum_id";
	//用户资料编辑
	public static final String AGE = "age";
	// 结果为null
	public static final String RESULT_NULL_CODE = "result null";
	// 正确
	public static final String RESULT_OK = "999";
	// json错误
	public static final String JSON_ERROR_CODE = "103";
	
	public static final String ERROR_502 = "502";
	public static final String ERROR_204 = "204";
	public static final String ERROR_404 = "404";
	public static final String ERROR_401 = "401";
	public static final String REQUEST_FAIL = "request fail";
	public static final String YOULE = "/youle/";
	public static final String CACHE = "/.cache";

	// 发送定位广播
	public static String BROADCAST_COUNTER_ACTION = "com.youle.chooseCity";
	public static String COUNTER_VALUE = "getloaction";
	
	public static final int MENU_STATE_CLOSE = 0;
	public static final int MENU_STATE_OPEN = 1;
	
	public static final int DURATION_TIME = 500; 	//滑动持续时间
	public static int screenWidth; // 屏幕宽度
	public static int screenHeight; // 屏幕高度（不包含状态栏）
	public static float screenDensity;	//屏幕密度
	public static boolean isIn_Vehicle;	//是否是车载模式
	public static void setScreenSize(int width, int height, float density){
		screenWidth = width;
		screenHeight = height;
		screenDensity = density;
	}
}
