package com.youle.managerData.info;


public class CarReplyInfo {
	private String postId;
	private String parentId;
	private String content;
	private String imgUrl;
	private String audUrl;
	private String audTime;
	private String userId;
	private String userName;
	private String avaUrl;
	private String reply;
	private String createTime;
	private int width;
	private int height;
	

	public CarReplyInfo(String postId, String parentId, String content,
			String imgUrl, String audUrl, String audTime, String userId,
			String userName, String avaUrl, String reply, String createTime,
			int width, int height) {
		super();
		this.postId = postId;
		this.parentId = parentId;
		this.content = content;
		this.imgUrl = imgUrl;
		this.audUrl = audUrl;
		this.audTime = audTime;
		this.userId = userId;
		this.userName = userName;
		this.avaUrl = avaUrl;
		this.reply = reply;
		this.createTime = createTime;
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getAudTime() {
		return audTime;
	}

	public String getPostId() {
		return postId;
	}
	public String getParentId() {
		return parentId;
	}
	public String getContent() {
		return content;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public String getAudUrl() {
		return audUrl;
	}
	public String getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	public String getAvaUrl() {
		return avaUrl;
	}
	public String getReply() {
		return reply;
	}
	public String getCreateTime() {
		return createTime;
	}
	
}
