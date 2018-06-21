package com.xiangpu.bean;

/**
 * description: 小圆点企业和个人信息
 * autour: Andy
 * date: 2017/12/2 19:07
 * update: 2017/12/2
 * version: 1.0
 */
public class PointUserInfoBean {

    private String avatar; // 头像地址
    private String userName; // 用户姓名
    private String companyCode; // 企业编码
    private String companyName; // 企业名称

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "PointUserInfoBean{" +
                "avatar='" + avatar + '\'' +
                ", userName='" + userName + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
