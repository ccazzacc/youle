package com.youle.managerData.info;

import com.youle.util.OtherUtil;


public class Token {
	private String access_token;
	private long expires_in;
	private String refresh_token;
	private String user_id;
	private int current_time;

	public Token(String access_token, long expires_in, String refresh_token,
			String user_id,int current_time) {
		super();
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.refresh_token = refresh_token;
		this.user_id = user_id;
		this.current_time = current_time;
	}

	public String getAccess_token() {
		if(OtherUtil.isNullOrEmpty(access_token))
			return "";
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getCurrent_time() {
		return current_time;
	}

	public void setCurrent_time(int current_time) {
		this.current_time = current_time;
	}

}
