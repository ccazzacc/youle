package com.youle.http_helper;

/**
 * Encapsulation a Requst error, when request can not be implemented
 * successful.封装了请求error时的数据
 * 
 */
public class RequestException extends Exception {

	private int statusCode = -1;

	public RequestException(String msg) {
		super(msg);
	}

	public RequestException(Exception cause) {
		super(cause);
	}

	public RequestException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public RequestException(String msg, Exception cause) {
		super(msg, cause);
	}

	public RequestException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public RequestException() {
		super();
	}

	public RequestException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public RequestException(Throwable throwable) {
		super(throwable);
	}

	public RequestException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
