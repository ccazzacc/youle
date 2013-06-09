package com.youle.managerData.info;

import java.util.List;

public class CarListInfo {
	private String avaUrl;
	private String name;
	private String reply;
	private String tiezi;
	private List<CarListDetailInfo> list;
	public CarListInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CarListInfo(String avaUrl, String name, String reply, String tiezi,
			List<CarListDetailInfo> list) {
		super();
		this.avaUrl = avaUrl;
		this.name = name;
		this.reply = reply;
		this.tiezi = tiezi;
		this.list = list;
	}
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
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public String getTiezi() {
		return tiezi;
	}
	public void setTiezi(String tiezi) {
		this.tiezi = tiezi;
	}
	public List<CarListDetailInfo> getList() {
		return list;
	}
	public void setList(List<CarListDetailInfo> list) {
		this.list = list;
	}
	
}
