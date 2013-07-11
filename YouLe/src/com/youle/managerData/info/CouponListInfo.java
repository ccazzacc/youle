package com.youle.managerData.info;

import java.io.Serializable;

public class CouponListInfo implements Serializable{
	private int merchantId;
	private String merName;
	private String merAddress;
	private String distance;
	private String imgUrl;
	private String couponName;
	private String expireAt;
	private String qrCode;//只有set get，在构造方法里没有
	
	public String getQrCode() {
		return qrCode;
	}
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	public CouponListInfo(int merchantId, String merName, String merAddress,
			String distance, String imgUrl, String couponName, String expireAt) {
		super();
		this.merchantId = merchantId;
		this.merName = merName;
		this.merAddress = merAddress;
		this.distance = distance;
		this.imgUrl = imgUrl;
		this.couponName = couponName;
		this.expireAt = expireAt;
	}
	public int getMerchantId() {
		return merchantId;
	}
	public String getMerName() {
		return merName;
	}
	public String getMerAddress() {
		return merAddress;
	}
	public String getDistance() {
		return distance;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public String getCouponName() {
		return couponName;
	}
	public String getExpireAt() {
		return expireAt;
	}
	
}
