package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by fangfumin on 2017/7/20.
 */
public class UserCompBean {

    private String compId;//后台返回的企业编码
    private String groupId;//后台返回的集团编码
    private String userType;//用户类型，B类还是C类

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "UserCompBean{" +
                "compId='" + compId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
