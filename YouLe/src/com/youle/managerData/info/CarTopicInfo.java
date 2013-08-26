package com.youle.managerData.info;

public class CarTopicInfo {
	private String postId;
	private String content;
	private String imgUrl;
	private String audUrl;
	private String userId;
	private String userName;
	private String avaUrl;
	private String reply;
	private String createTime;
	public CarTopicInfo(String postId, String content, String imgUrl,
			String audUrl, String userId, String userName, String avaUrl,
			String reply, String createTime) {
		super();
		this.postId = postId;
		this.content = content;
		this.imgUrl = imgUrl;
		this.audUrl = audUrl;
		this.userId = userId;
		this.userName = userName;
		this.avaUrl = avaUrl;
		this.reply = reply;
		this.createTime = createTime;
	}
	public String getPostId() {
		return postId;
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
	};
	
}
