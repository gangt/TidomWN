package com.suneee.common.utils;

import android.annotation.SuppressLint;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 */
public class StringUtils {

	/**
	 * 判断字符不能为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || "".equals(str) || "null".equalsIgnoreCase(str) || "".equals(str.trim())){
			return true;
		}
		return false;
	}
	
	/**
	 * 二行制转字符串
	 */
	@SuppressLint("DefaultLocale")
	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString();
	}

	/**
	 * 判断数组是否为空
	 * 
	 * @param list
	 * @return
	 */
	public static <T> boolean isEmpty(List<T> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}
	
	/** 判断List是否为空 */
	public static boolean listIsNullOrEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}
	
	/**
	 * 请选择
	 */
	final static String PLEASE_SELECT = "请选择...";
	public static boolean notEmpty(Object o) {
		return o != null && !"".equals(o.toString().trim()) && !"null".equalsIgnoreCase(o.toString().trim())
				&& !"undefined".equalsIgnoreCase(o.toString().trim()) && !PLEASE_SELECT.equals(o.toString().trim());
	}

    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
