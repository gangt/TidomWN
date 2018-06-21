package com.lssl.entity;

/**
 * description: 进度条实体类
 * autour: Andy
 * date: 2017/12/16 14:12
 * update: 2017/12/16
 * version: 1.0
 */
public class DonutProgressInfo {

    public String topStr;
    public String midStr;
    public String bottomStr;
    public String location;
    public String expandStr;
    public int progress;

    @Override
    public String toString() {
        return "topStr: " + topStr + " && "
                + "midStr: " + midStr + " && "
                + "bottomStr: " + bottomStr + " && "
                + "expandStr: " + expandStr + " && "
                + "location: " + location;
    }

}
