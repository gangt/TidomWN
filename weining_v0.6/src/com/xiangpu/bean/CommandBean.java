package com.xiangpu.bean;

/**
 * 指挥管理Bean
 * @author chengang
 *
 */
public class CommandBean {
	public String commandid;//id

	public CommandBean() {
	}

	public CommandBean(String name) {

		this.name = name;
	}

	public Boolean select;// 是否选中
	public String name; // 指挥名称
	public String startTime;// 指挥开始时间
	public Boolean getSelect() {
		return select;
	}
	public void setSelect(Boolean select) {
		this.select = select;
	}
	public String getName() {
		return name;
	}
	public String getCommandid() {
		return commandid;
	}
	public void setCommandid(String commandid) {
		this.commandid = commandid;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
}
