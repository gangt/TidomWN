/*
    Suneee Android Client, DownTimerListener
    Copyright (c) 2014 Suneee Tech Company Limited
 */
package com.suneee.common.utils;

/**
 * [倒计时监听类]
 * 
 * @author huxinwu
 * @version 1.0
 * @date 2015-5-13
 * 
 **/
public interface DownTimerListener {

	/**
	 * [倒计时每秒方法]<BR>
	 * @param millisUntilFinished
	 */
	public void onTick(long millisUntilFinished);
	
	/**
	 * [倒计时完成方法]<BR>
	 */
	public void onFinish();
}

