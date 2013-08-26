package com.youle.managerData.info;

public class HotComInfo {
	private String commentId;
	private String topicId;
	private String created;
	private String content;
	private String audUrl;
	private String audTime;
	private String imgUrl;
	private int imgWidth;
	private int imgHeight;
	private String userName;
	private String userId;
	private String avaUrl;
	
	public HotComInfo(String commentId, String topicId, String created,
			String content, String audUrl, String audTime, String imgUrl,
			int imgWidth, int imgHeight, String userName, String userId,
			String avaUrl) {
		super();
		this.commentId = commentId;
		this.topicId = topicId;
		this.created = created;
		this.content = content;
		this.audUrl = audUrl;
		this.audTime = audTime;
		this.imgUrl = imgUrl;
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.userName = userName;
		this.userId = userId;
		this.avaUrl = avaUrl;
	}
	public String getCommentId() {
		return commentId;
	}
	public String getTopicId() {
		return topicId;
	}
	public String getCreated() {
		return created;
	}
	public String getContent() {
		return content;
	}
	public String getAudUrl() {
		return audUrl;
	}
	public String getAudTime() {
		return audTime;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public String getUserName() {
		return userName;
	}
	public String getUserId() {
		return userId;
	}
	public String getAvaUrl() {
		return avaUrl;
	}
	public int getImgWidth() {
		return imgWidth;
	}
	public int getImgHeight() {
		return imgHeight;
	}
	
}
