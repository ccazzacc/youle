package com.youle.util;

import java.io.File;

import android.content.SharedPreferences;
import android.os.Environment;

public class GlobalData {
	// 拍照
	public static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	public static final int PHOTO_REQUEST_GALLERY = 2;
	// 录音
	public static final int SOUND_REQUEST = 6;
	public static final int SOUND_RESULT = 100;
	public final static String ACTION_CROP_IMAGE = "android.intent.action.CROP";
	public final static String IMAGE_URI = "iamge_uri";
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
	// 注册
	public static final String EMAIL = "email";
	// 创建旅程
	public static final String NAME = "name";
	public static final String LNG = "lng";
	public static final String LAT = "lat";
	public static final String IS_PUBLIC = "is_public";
	public static final String IS_WIFI = "wifi";
	public static final String SHARE = "share";
	public static final String IS_ME = "is_me";
	// 设置旅程
	public static final String TRIP_ID = "trip_id";
	// 上传事件之后
	public static final String TRACK_ID = "track_id";
	// json用
	public static final String ACCESS_TOKEN = "access_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String USER_ID = "user_id";
	public static final String ERROR_CODE = "error_code";
	public static final String ERROR_DESCRIPTION = "error_description";
	public static final String AVATAR_SMALL_URL = "avatar_small_url";

	// 获取旅程列表用的
	public static final String TRIPS = "trips";
	public static final String FINISHED = "finished";
	public static final String LOCATION = "location";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String DURATION = "duration";
	public static final String CREATED_DATE = "created_date";
	public static final String END_LOCATION = "end_location";
	public static final String FINISHED_DATE = "finished_date";

	// 获取旅程事件列表
	public static final String TRIP_NAME = "trip_name";
	public static final String COVER_URL = "cover_url";
	public static final String RID = "rid";
	public static final String TRACKS = "tracks";
	public static final String ELEVATION = "elevation";
	public static final String LIKES = "likes";
	public static final String IS_LIKE = "is_like";
	public static final String LIKE_LIST = "like_list";
	public static final String AVATAR_URL = "avatar_url";
	public static final String COMMENTS = "comments";
	public static final String PLACE = "place";
	public static final String FLAG = "flag";
	public static final String SPEED = "speed";
	public static final String EVENT = "event";
	public static final String TEXT = "text";
	public static final String EDIT_TEXT = "edit_text";
	public static final String IMAGE_URL = "image_url";
	public static final String AUDIO_URL = "audio_url";
	public static final String AUDIO_TIME = "audio_time";
	public static final String COST = "cost";
	public static final String AMOUNT = "amount";
	public static final String TYPE = "type";
	public static final String CURRENCY = "currency";
	public static final String MEMO = "memo";
	public static final String IMAGE_HEIGHT = "image_height";
	public static final String IMAGE_WIDTH = "image_width";
	public static final String TRIP_URL = "trip_url";
	public static final String TRACK_URL = "track_url";
	// 喜欢
	public static final String LIKE = "like";
	public static final String TOTAL = "total";
	public static final String LIKE_USERS = "like_users";
	// 评论
	public static final String TXT = "txt";
	public static final String COMMENT_ID = "comment_id";
	public static final String USER_INFO = "user_info";
	// 用户
	public static final String FOLLOWED = "followed";
	public static final String FAVORITES = "favorites";
	public static final String KILOS = "kilos";
	public static final String FOLLOWERS = "followers";
	public static final String AVATAR_MEDIUM_URL = "avatar_medium_url";
	public static final String NEW_FOLLOWERS = "new_followers";
	public static final String UNREAD = "private_unread";
	// 收藏
	public static final String FAVORITE = "favorite";
	public static final String IS_FAVORITE = "is_favorite";
	// 关注
	public static final String FOLLOW_TO = "follow_to";
	public static final String IS_FOLLOWED = "is_followed";
	public static final String USERS = "users";
	// 获取hotTrip
	public static final String DIRECTION = "direction";
	public static final String PAGE = "page";
	public static final String SIZE = "size";
	// live
	public static final String TIMESTAMP = "timestamp";
	public static final String LIVES = "lives";
	public static final String HEIGHT = "height";
	public static final String WIDTH = "width";
	public static final String PHOTO_URL = "photo_url";
	// 地址解析
	public static final String RESULTS = "results";
	public static final String ADDRESS_COMP = "address_components";
	public static final String LONG_NAME = "long_name";
	public static final String TYPES = "types";
	
	//私信
	public static final String MESSAGE = "message";
	public static final String MESSAGES = "messages";
	public static final String FROM_USER = "from_user";
	public static final String MEMBERS = "members";

	// 添加评论
	public static final int REQUEST_CODE_COMMENT_ADD = 3;
	public static final int RESULT_CODE_COMMENT_OK = 888;
	public static final String AUD = "aud";
	public static final String AUD_T = "aud_t";
	public static final String AUDIO_LENGTH = "audio_length";
	// 记账
	public static final int REQUEST_CODE_COST_ADD = 4;
	public static final int RESULT_CODE_COST_OK = 5;
	public static final String COST_PATH = "costPath";
	public static String costPath;
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

	public static final String CLIENT_ERROR = "100004";
	public static final String NO_GRANT_TYPE = "100005";
	public static final String OAUTH_FAIL = "10006";// 用户鉴权失败
	public static final String NO_REFRESH_TOKEN = "100007";
	public static final String REFRESH_TOKEN_NOEXIST = "100008";
	public static final String ACCESS_TOKEN_NO_REFRESH = "100009";
	public static final String LINITE_ACCASS = "100010";// 访问受限
	public static final String UNKNOW_ERROR = "100011";
	public static final String COPY_EMAIL = "20006";// 邮箱重复
	public static final String COPY_USER = "20007";// 用户名重复
	public static final String NO_USER = "200009";
	public static final String NOT_FOLLOW_ME = "20010";// 用户不能关注自己
	public static final String NO_TRIP_ID = "300001";
	public static final String INVALID_TRIP_ID = "300002";
	public static final String NO_TRIP = "30004";
	public static final String INVALID_TRACK_ID = "300007";
	public static final String NO_TRACK = "300008";
	public static final String UNFINISH_TRIP = "30026";
	public static final String UPFILE_ERROR = "500001";
	// 刷新accesstoken时的错误
	public static final String ERROR_INVALID_ACCESS_TOKEN_CODE = "401";
	public static final String ERROR_502 = "502";
	public static final String ERROR_204 = "204";
	public static final String ERROR_404 = "404";
	public static final String ERROR_401 = "401";
	public static final String REQUEST_FAIL = "request fail";
	public static final String PATHTRIP = "/sdcard/pathtrip/";
	public static final String CACHE = "/sdcard/.cache";
	public static String SOUND_PATH = "";
	public static int autio_time = 0;
	
	//发送定位广播
	public static String BROADCAST_COUNTER_ACTION="com.youle.chooseCity";
	public static String COUNTER_VALUE="getloaction";


}
