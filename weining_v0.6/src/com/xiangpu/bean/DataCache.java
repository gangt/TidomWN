package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by fangfumin on 2017/9/8.
 */

public class DataCache extends DataSupport {

    private String url;
    private String compId;
    private String userName;
    private String jsonData;

    public DataCache() {
    }

    public DataCache(String url, String compId, String userName, String jsonData) {
        this.url = url;
        this.compId = compId;
        this.userName = userName;
        this.jsonData = jsonData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public String toString() {
        return "DataCache{" +
                "compId='" + compId + '\'' +
                ", userName='" + userName + '\'' +
                ", url='" + url + '\'' +
                ", jsonData='" + jsonData + '\'' +
                '}';
    }
}
