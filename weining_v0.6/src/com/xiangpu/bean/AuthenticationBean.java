package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by fangfumin on 2017/7/18.
 */

public class AuthenticationBean extends DataSupport {

    /**
     * id : 7
     * orgName : SUNEEE
     * version : 1.1
     * env : Android1
     * homeUrl : http://test.xiangyi.weilian.cn:9902/src/index.html?type=7
     * mallUrl : http://con.vr.weilian.cn:8112/static/H5/index.html
     * loginLogoDir : upload/20170829/f90f5ad1-4f97-4d17-91db-65af95ac8490.png
     * authLogoDir : upload/20170829/9feab255-5c88-41d7-91f0-1852e3e47c0b.png
     * indexLogoDir : upload/20170829/0d83b2ed-f617-480f-96e5-61f15ae26785.png
     * aboutLogoDir : upload/20170829/2a52be22-7052-4a35-a9d3-d281d38708a0.png
     * createTime : 2017-08-29 10:04:44
     * updateTime : 2017-08-29 10:13:50
     * weilianlUrl :
     * providerUrl : null
     * versionAbout : null
     */

    private int id;
    private String orgName;
    private String version;
    private String env;
    private String homeUrl;
    private String mallUrl;
    private String loginLogoDir;
    private String authLogoDir;
    private String indexLogoDir;
    private String aboutLogoDir;
    private String createTime;
    private String updateTime;
    private String weilianlUrl;
    private String providerUrl;
    private String versionAbout;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getHomeUrl() {
        return isNullUrl(homeUrl);
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getMallUrl() {
        return isNullUrl(mallUrl);
    }

    public void setMallUrl(String mallUrl) {
        this.mallUrl = mallUrl;
    }

    public String getLoginLogoDir() {
        return loginLogoDir;
    }

    public void setLoginLogoDir(String loginLogoDir) {
        this.loginLogoDir = loginLogoDir;
    }

    public String getAuthLogoDir() {
        return authLogoDir;
    }

    public void setAuthLogoDir(String authLogoDir) {
        this.authLogoDir = authLogoDir;
    }

    public String getIndexLogoDir() {
        return indexLogoDir;
    }

    public void setIndexLogoDir(String indexLogoDir) {
        this.indexLogoDir = indexLogoDir;
    }

    public String getAboutLogoDir() {
        return aboutLogoDir;
    }

    public void setAboutLogoDir(String aboutLogoDir) {
        this.aboutLogoDir = aboutLogoDir;
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

    public String getWeilianlUrl() {
        return isNullUrl(weilianlUrl);
    }

    public void setWeilianlUrl(String weilianlUrl) {
        this.weilianlUrl = weilianlUrl;
    }

    public String getProviderUrl() {
        return isNullUrl(providerUrl);
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public String getVersionAbout() {
        return versionAbout;
    }

    public void setVersionAbout(String versionAbout) {
        this.versionAbout = versionAbout;
    }

    public String isNullUrl(String url) {
        return url.isEmpty() ? null : url;
    }

    @Override
    public String toString() {
        return "AuthenticationBean{" +
                "id=" + id +
                ", orgName='" + orgName + '\'' +
                ", version='" + version + '\'' +
                ", env='" + env + '\'' +
                ", homeUrl='" + homeUrl + '\'' +
                ", mallUrl='" + mallUrl + '\'' +
                ", loginLogoDir='" + loginLogoDir + '\'' +
                ", authLogoDir='" + authLogoDir + '\'' +
                ", indexLogoDir='" + indexLogoDir + '\'' +
                ", aboutLogoDir='" + aboutLogoDir + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", weilianlUrl='" + weilianlUrl + '\'' +
                ", providerUrl=" + providerUrl +
                ", versionAbout=" + versionAbout +
                '}';
    }
}
