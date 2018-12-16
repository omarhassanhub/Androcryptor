package com.myproject.androcryptor.model;

public class drawerNavItem {
	
	private String title;
	private int icon;
	private String count = "0";

	private boolean isCounterVisible = false;
	
	public drawerNavItem(){}

	public drawerNavItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	
	public drawerNavItem(String title, int icon, boolean isCounterVisible, String count){
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}
	
	public String getDrawerTitle(){
		return this.title;
	}
	
	public int getDrawerIcon(){
		return this.icon;
	}
	
	public String getDrawerCount(){
		return this.count;
	}
	
	public boolean getDrawerCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setDrawerIcon(int icon){
		this.icon = icon;
	}
	
	public void setDrawerCount(String count){
		this.count = count;
	}
	
	public void setDrawerCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
}
