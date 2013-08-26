package com.youle.managerData.info;

import java.util.List;

public class CarListInfo {
	private String avaUrl;
	private String name;
	private String tPost;
	private String tReply;//总共帖子数
	private List<CarTopicInfo> list;
	public CarListInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getAvaUrl() {
		return avaUrl;
	}
	public String getName() {
		return name;
	}
	public String gettPost() {
		return tPost;
	}
	public String gettReply() {
		return tReply;
	}
	public List<CarTopicInfo> getList() {
		return list;
	}
	public void setAvaUrl(String avaUrl) {
		this.avaUrl = avaUrl;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void settPost(String tPost) {
		this.tPost = tPost;
	}
	public void settReply(String tReply) {
		this.tReply = tReply;
	}
	public void setList(List<CarTopicInfo> list) {
		this.list = list;
	}
	public CarListInfo(String avaUrl, String name, String tPost, String tReply,
			List<CarTopicInfo> list) {
		super();
		this.avaUrl = avaUrl;
		this.name = name;
		this.tPost = tPost;
		this.tReply = tReply;
		this.list = list;
	}
	
}
