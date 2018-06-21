package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Andi on 2017/7/6.
 */

public class UrlConfigBean extends DataSupport {

    public static String TAG = "UrlConfigBean";

    /**
     * id : 1123
     * appVersion : 0.5
     * appEnvName : Android2
     * isActived : 1
     * btnPosId : 1
     * btnPosDes : 视点观
     * btnIconName : 视点观
     * btnIconDir : upload/20170825/68182651-491d-4ebb-b57f-771859cc15d2.png
     * redirUrl : http://con.vr.weilian.cn:8112/static/H5/index.html#main.html
     * otherParam :
     * compId : 57
     * compCode : SUNEEE
     * subCompCode : null
     * subCompName : null
     * ajaxUrl : null
     * userAuth : ["BUser","CUser","BUSER","CUSER"]
     * entrance : weilianhai
     * createTime : 2017-09-20 10:57:54
     * updateTime : 2017-10-18 15:51:51
     */

    private int id;
    private String appVersion;
    private String appEnvName;
    private int isActived;
    private int btnPosId;
    private String btnPosDes;
    private String btnIconName;
    private String btnIconDir;
    private String redirUrl;
    private String otherParam;
    private int compId;
    private String compCode;
    private String subCompCode;
    private String subCompName;
    private String ajaxUrl;
    private String userAuth;
    private String entrance;
    private String createTime;
    private String updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppEnvName() {
        return appEnvName;
    }

    public void setAppEnvName(String appEnvName) {
        this.appEnvName = appEnvName;
    }

    public int getIsActived() {
        return isActived;
    }

    public void setIsActived(int isActived) {
        this.isActived = isActived;
    }

    public int getBtnPosId() {
        return btnPosId;
    }

    public void setBtnPosId(int btnPosId) {
        this.btnPosId = btnPosId;
    }

    public String getBtnPosDes() {
        return btnPosDes;
    }

    public void setBtnPosDes(String btnPosDes) {
        this.btnPosDes = btnPosDes;
    }

    public String getBtnIconName() {
        return btnIconName;
    }

    public void setBtnIconName(String btnIconName) {
        this.btnIconName = btnIconName;
    }

    public String getBtnIconDir() {
        return btnIconDir;
    }

    public void setBtnIconDir(String btnIconDir) {
        this.btnIconDir = btnIconDir;
    }

    public String getRedirUrl() {
        return redirUrl;
    }

    public void setRedirUrl(String redirUrl) {
        this.redirUrl = redirUrl;
    }

    public String getOtherParam() {
        return otherParam;
    }

    public void setOtherParam(String otherParam) {
        this.otherParam = otherParam;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public String getSubCompCode() {
        return subCompCode;
    }

    public void setSubCompCode(String subCompCode) {
        this.subCompCode = subCompCode;
    }

    public String getSubCompName() {
        return subCompName;
    }

    public void setSubCompName(String subCompName) {
        this.subCompName = subCompName;
    }

    public String getAjaxUrl() {
        return ajaxUrl;
    }

    public void setAjaxUrl(String ajaxUrl) {
        this.ajaxUrl = ajaxUrl;
    }

    public String getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(String userAuth) {
        this.userAuth = userAuth;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UrlConfigBean{" +
                "id=" + id +
                ", appVersion='" + appVersion + '\'' +
                ", appEnvName='" + appEnvName + '\'' +
                ", isActived=" + isActived +
                ", btnPosId=" + btnPosId +
                ", btnPosDes='" + btnPosDes + '\'' +
                ", btnIconName='" + btnIconName + '\'' +
                ", btnIconDir='" + btnIconDir + '\'' +
                ", redirUrl='" + redirUrl + '\'' +
                ", otherParam='" + otherParam + '\'' +
                ", compId=" + compId +
                ", compCode='" + compCode + '\'' +
                ", subCompCode=" + subCompCode +
                ", subCompName=" + subCompName +
                ", ajaxUrl=" + ajaxUrl +
                ", userAuth='" + userAuth + '\'' +
                ", entrance='" + entrance + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
