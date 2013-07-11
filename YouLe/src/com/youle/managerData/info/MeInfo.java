package com.youle.managerData.info;

public class MeInfo {
	private String userName;
	private String userId;
	private int type;
	private int gender;
	private String avaUrl;
	private String level;
	private String points;
	private String age;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public MeInfo(String userName, String userId, int type, int gender,
			String avaUrl, String level, String points) {
		super();
		this.userName = userName;
		this.userId = userId;
		this.type = type;
		this.gender = gender;
		this.avaUrl = avaUrl;
		this.level = level;
		this.points = points;
	}
	public String getUserName() {
		return userName;
	}
	public String getUserId() {
		return userId;
	}
	public int getType() {
		return type;
	}
	public int getGender() {
		return gender;
	}
	public String getAvaUrl() {
		return avaUrl;
	}
	public String getLevel() {
		return level;
	}
	public String getPoints() {
		return points;
	}
	
}
