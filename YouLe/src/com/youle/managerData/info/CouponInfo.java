package com.youle.managerData.info;

public class CouponInfo {
	private String sPhoto;
	private String sInfo;
	private String sTime;
	private String sKilo;
	public String getsPhoto() {
		return sPhoto;
	}
	public void setsPhoto(String sPhoto) {
		this.sPhoto = sPhoto;
	}
	public String getsInfo() {
		return sInfo;
	}
	public void setsInfo(String sInfo) {
		this.sInfo = sInfo;
	}
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	public String getsKilo() {
		return sKilo;
	}
	public void setsKilo(String sKilo) {
		this.sKilo = sKilo;
	}
	public CouponInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CouponInfo(String sPhoto, String sInfo, String sTime, String sKilo) {
		super();
		this.sPhoto = sPhoto;
		this.sInfo = sInfo;
		this.sTime = sTime;
		this.sKilo = sKilo;
	}
	
}
