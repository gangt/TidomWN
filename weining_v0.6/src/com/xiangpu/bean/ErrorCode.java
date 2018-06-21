package com.xiangpu.bean;

/**
 * Created by huangda on 2018/1/28.
 */

public class ErrorCode {
    public static final String APP_NO_FIND = "APP_NO_FIND";//应用不存在
    public static final String UNAUTHORIZED_ACCESS_APP = "UNAUTHORIZED_ACCESS_APP";//未经授权应用，不可以访问统一用户中心
    public static final String ENTERPRISE_NOT_ENABLED = "ENTERPRISE_NOT_ENABLED";//应用对应的企业已被禁用
    public static final String NO_EXISTS_ACCOUNT = "NO_EXISTS_ACCOUNT";//帐号不存在
    public static final String PLEASE_MOBILE_EMAIL_LOGIN = "PLEASE_MOBILE_EMAIL_LOGIN";//账号重复，请用手机或邮箱登录
    public static final String USER_DISABLED = "USER_DISABLED";//该帐号已经被管理员禁用，请联系管理员!
    public static final String ILLEGAL_ARGUMENT = "ILLEGAL_ARGUMENT";//会话不能为空
    public static final String CONNECTION_FAIL = "CONNECTION_FAIL";//连接获取失败或者已经失效，请重新获取
}
