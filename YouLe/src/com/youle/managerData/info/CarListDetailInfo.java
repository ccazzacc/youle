package com.youle.managerData.info;

public class CarListDetailInfo {
	private String avaUrl;
	private String content;
	private String photoUrl;
	private String soundUrl;
	private String name;
	private String reply;
	private String time;
	public String getAvaUrl() {
		return avaUrl;
	}
	public void setAvaUrl(String avaUrl) {
		this.avaUrl = avaUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getSoundUrl() {
		return soundUrl;
	}
	public void setSoundUrl(String soundUrl) {
		this.soundUrl = soundUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public CarListDetailInfo(String avaUrl, String content, String photoUrl,
			String soundUrl, String name, String reply, String time) {
		super();
		this.avaUrl = avaUrl;
		this.content = content;
		this.photoUrl = photoUrl;
		this.soundUrl = soundUrl;
		this.name = name;
		this.reply = reply;
		this.time = time;
	}
	public CarListDetailInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
