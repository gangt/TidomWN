package com.xiangpu.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by huangda on 2017/7/19.
 */

public class IndustryBean extends DataSupport{

    private int number;

    private String name;
    private String updateTime;

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IndustryBean{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
