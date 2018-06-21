package com.suneee.common.events;

/**
 * Name: ScanResultEvents
 * Author: sien
 * Email:
 * Comment: //二维码扫描结果事件
 * Date: 2016-06-04 10:14
 */
public class ScanResultEvents extends Object{
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    public static final String TYPE_LINK_QRCODE = "link";
    public static final String TYPE_FRIEND_QRCODE = "friend";
    public static final String TYPE_DISCUSSION_QRCODE = "discussion";
    public static final String TYPE_GROUP_QRCODE = "group";
    public static final String TYPE_TEXT_QRCODE = "qrtext";
    public static final String TYPE_TEXT_BARCODE = "bartext";

    private int status;//扫描状态
    private String result;//扫描结果
    private String type;//扫描类型

    public ScanResultEvents(int status,String result,String type){
        this.status = status;
        this.result = result;
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public String getType() {
        return type;
    }
}
