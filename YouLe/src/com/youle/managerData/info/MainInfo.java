package com.youle.managerData.info;

public class MainInfo {
	private String avaUrl;
	private String msgContent;
	private String imgUrl;
	private String address;
	private String time;
	private int status;
	public String getAvaUrl() {
		return avaUrl;
	}
	public void setAvaUrl(String avaUrl) {
		this.avaUrl = avaUrl;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public MainInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MainInfo(String avaUrl, String msgContent, String imgUrl,
			String address, String time, int status) {
		super();
		this.avaUrl = avaUrl;
		this.msgContent = msgContent;
		this.imgUrl = imgUrl;
		this.address = address;
		this.time = time;
		this.status = status;
	}
	
}
