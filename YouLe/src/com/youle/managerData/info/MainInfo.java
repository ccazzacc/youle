package com.youle.managerData.info;

import java.io.Serializable;

import com.amap.api.maps.model.LatLng;


public class MainInfo{
	private String trackId;
	private String userId;
    private String username;
	private String avaUrl;
	private String created;
	private String text;
	private int mark;
	private String audUrl;
	private int audTime;
	private String imgUrl;
	private String orImgUrl;
	private LatLng latLng;
	private String place;
	private int flags;
	private int width;
	private int height;
	private int uType;
//	private double lat;
//	private double lng;
//	private String avaUrl;
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
    public String getUserName(){
        return username;
    }
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	public String getAudUrl() {
		return audUrl;
	}
	public void setAudUrl(String audUrl) {
		this.audUrl = audUrl;
	}
	public int getAudTime() {
		return audTime;
	}
	public void setAudTime(int audTime) {
		this.audTime = audTime;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getOrImgUrl() {
		return orImgUrl;
	}
	public void setOrImgUrl(String orImgUrl) {
		this.orImgUrl = orImgUrl;
	}
	public LatLng getLatLng() {
		return latLng;
	}
	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	
	public String getAvaUrl() {
		return avaUrl;
	}
	public void setAvaUrl(String avaUrl) {
		this.avaUrl = avaUrl;
	}
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getUtype() {
		return uType;
	}
	public void setUtype(int uType) {
		this.uType = uType;
	}
	public MainInfo(String trackId, String userId, String username ,String avaUrl,
			String created, String text, int mark, String audUrl, int audTime,
			String imgUrl, String orImgUrl, LatLng latLng, String place,
			int flags, int width, int height,int uType) {
		super();
		this.trackId = trackId;
		this.userId = userId;
        this.username=username;
		this.avaUrl = avaUrl;
		this.created = created;
		this.text = text;
		this.mark = mark;
		this.audUrl = audUrl;
		this.audTime = audTime;
		this.imgUrl = imgUrl;
		this.orImgUrl = orImgUrl;
		this.latLng = latLng;
		this.place = place;
		this.flags = flags;
		this.width = width;
		this.height = height;
		this.uType = uType;
	}
	public MainInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
}
