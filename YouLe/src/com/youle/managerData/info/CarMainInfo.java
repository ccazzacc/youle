package com.youle.managerData.info;

public class CarMainInfo {
	private String avaUrl;
	private String name;
	private String num;
	public String getAvaUrl() {
		return avaUrl;
	}
	public void setAvaUrl(String avaUrl) {
		this.avaUrl = avaUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public CarMainInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CarMainInfo(String avaUrl, String name, String num) {
		super();
		this.avaUrl = avaUrl;
		this.name = name;
		this.num = num;
	}
	
}
