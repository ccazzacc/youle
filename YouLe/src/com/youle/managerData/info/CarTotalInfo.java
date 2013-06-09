package com.youle.managerData.info;

public class CarTotalInfo {
	private String avaUrl;
	private String uName;
	private String content;
	private String time;
	private String phoUrl;
	private String audUrl;
	public CarTotalInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CarTotalInfo(String avaUrl, String uName, String content,
			String time, String phoUrl, String audUrl) {
		super();
		this.avaUrl = avaUrl;
		this.uName = uName;
		this.content = content;
		this.time = time;
		this.phoUrl = phoUrl;
		this.audUrl = audUrl;
	}
	public String getAvaUrl() {
		return avaUrl;
	}
	public void setAvaUrl(String avaUrl) {
		this.avaUrl = avaUrl;
	}
	public String getuName() {
		return uName;
	}
	public void setuName(String uName) {
		this.uName = uName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPhoUrl() {
		return phoUrl;
	}
	public void setPhoUrl(String phoUrl) {
		this.phoUrl = phoUrl;
	}
	public String getAudUrl() {
		return audUrl;
	}
	public void setAudUrl(String audUrl) {
		this.audUrl = audUrl;
	}
	
}
