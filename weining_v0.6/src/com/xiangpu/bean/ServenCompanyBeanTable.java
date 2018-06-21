package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * description: 七星页公司数据结构表
 * autour: Andy
 * date: 2018/1/18 16:35
 * update: 2018/1/18
 * version: 1.0
 */
public class ServenCompanyBeanTable extends DataSupport {

    /**
     * compId : 110
     * compName : 储备粮
     * compCode : CHUBEILIANG
     * compParentCode : 68
     * industryId : 2
     * posId : 3
     */

    private int compId;           // 公司ID
    private String compName;      // 公司名称
    private String compCode;      // 公司编码
    private int compParentCode;   // 父公司ID
    private int industryId;       // 所属七星轴 从1开始
    private int posId;            // 位置ID
    private int level;            // 第几级
    private int rank;            // 排列位置  从1开始
    private boolean hasSubCompany;    // 是否有子公司

    public boolean isHasSubCompany() {
        return hasSubCompany;
    }

    public void setHasSubCompany(boolean hasSubCompany) {
        this.hasSubCompany = hasSubCompany;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public int getCompParentCode() {
        return compParentCode;
    }

    public void setCompParentCode(int compParentCode) {
        this.compParentCode = compParentCode;
    }

    public int getIndustryId() {
        return industryId;
    }

    public void setIndustryId(int industryId) {
        this.industryId = industryId;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }
}
