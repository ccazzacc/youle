package com.youle.managerData.info;

public class ConsumeInfo {
	private String month;
	private String total;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public ConsumeInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ConsumeInfo(String month, String total) {
		super();
		this.month = month;
		this.total = total;
	}
	
}
