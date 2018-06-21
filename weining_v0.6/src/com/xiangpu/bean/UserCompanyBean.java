package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

/**
 * description: 企业信息
 * autour: Andy
 * date: 2017/12/2 21:53
 * update: 2017/12/2
 * version: 1.0
 */
public class UserCompanyBean extends DataSupport {

    /**
     * id : 68
     * comp_id : 68
     * comp_name : 威宁集团
     * comp_code : icon_weining
     * comp_short_name : 威宁
     * comp_parent_code : 0
     * address :
     * logo :
     * contact :
     * telephone :
     * isGroup : Y
     * groupCode : icon_weining
     * remarks :
     * type : SYSTEM
     * hasPower : 1
     * cluster_code :
     * cluster_name :
     * createDate : 1503280890000
     * updateDate : 1503280890000
     * deleteFlag : 0
     * auditFlag : 1
     */

    private int id;
    private int comp_id;
    private String comp_name;
    private String comp_code;
    private String comp_short_name;
    private int comp_parent_code;
    private String address;
    private String logo;
    private String contact;
    private String telephone;
    private String isGroup;
    private String groupCode;
    private String remarks;
    private String type;
    private int hasPower;
    private String cluster_code;
    private String cluster_name;
    private long createDate;
    private long updateDate;
    private String deleteFlag;
    private String auditFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComp_id() {
        return comp_id;
    }

    public void setComp_id(int comp_id) {
        this.comp_id = comp_id;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public String getComp_code() {
        return comp_code;
    }

    public void setComp_code(String comp_code) {
        this.comp_code = comp_code;
    }

    public String getComp_short_name() {
        return comp_short_name;
    }

    public void setComp_short_name(String comp_short_name) {
        this.comp_short_name = comp_short_name;
    }

    public int getComp_parent_code() {
        return comp_parent_code;
    }

    public void setComp_parent_code(int comp_parent_code) {
        this.comp_parent_code = comp_parent_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHasPower() {
        return hasPower;
    }

    public void setHasPower(int hasPower) {
        this.hasPower = hasPower;
    }

    public String getCluster_code() {
        return cluster_code;
    }

    public void setCluster_code(String cluster_code) {
        this.cluster_code = cluster_code;
    }

    public String getCluster_name() {
        return cluster_name;
    }

    public void setCluster_name(String cluster_name) {
        this.cluster_name = cluster_name;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getAuditFlag() {
        return auditFlag;
    }

    public void setAuditFlag(String auditFlag) {
        this.auditFlag = auditFlag;
    }

    @Override
    public synchronized boolean save() {
        if (id != 0) {
            setComp_id(id);
        }
        return super.save();
    }
}
