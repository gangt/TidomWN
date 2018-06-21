package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by huangda on 2017/7/17.
 */

public class CompanyInfo extends DataSupport {

    private int IndustryNum; //产业编号
    private String subCompCode; //子公司编码
    private String popName; //子公司名称
    private String updateTime; //字端更新时间

    public int getIndustryNum() {
        return IndustryNum;
    }

    public void setIndustryNum(int industryNum) {
        IndustryNum = industryNum;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSubCompCode() {
        return subCompCode;
    }

    public void setSubCompCode(String subCompCode) {
        this.subCompCode = subCompCode;
    }

    private String version; //App版本

    public String getPopName() {
        return popName;
    }

    public void setPopName(String popName) {
        this.popName = popName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
