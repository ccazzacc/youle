package com.youle.managerData.info;

public class CarMainInfo {
	private String logoUrl;
	private String name;
	private String totalPosts;
	private String forumId;
	private String radioId;
	public CarMainInfo(String logoUrl, String name, String totalPosts,
			String forumId, String radioId) {
		super();
		this.logoUrl = logoUrl;
		this.name = name;
		this.totalPosts = totalPosts;
		this.forumId = forumId;
		this.radioId = radioId;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public String getName() {
		return name;
	}
	public String getTotalPosts() {
		return totalPosts;
	}
	public String getForumId() {
		return forumId;
	}
	public String getRadioId() {
		return radioId;
	}
	
}
