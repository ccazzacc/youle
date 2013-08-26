package com.youle.managerData.info;

import java.util.List;

public class ConsumeListInfo {
	private String today;
	private String month;
	private List<ConsumeInfo> list;
	
	public ConsumeListInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getToday() {
		return today;
	}
	public String getMonth() {
		return month;
	}
	public List<ConsumeInfo> getList() {
		return list;
	}
	public void setToday(String today) {
		this.today = today;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public void setList(List<ConsumeInfo> list) {
		this.list = list;
	}
	
}
