package com.xiangpu.appversion;

public class UpdataInfo {
    private String version;
    private String url;
    private String description;

    private String apkFile;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBinFile() {
        return apkFile;
    }

    public void setBinFile(String binFile) {
        this.apkFile = binFile;
    }
}
