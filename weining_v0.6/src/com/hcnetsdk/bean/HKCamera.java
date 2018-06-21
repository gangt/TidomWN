package com.hcnetsdk.bean;

/**
 * Created by Andi on 2017/5/11.
 */

public class HKCamera {

    public String CamIP;
    public String CamPort;
    public String CamLoginName;
    public String CamLoginPwd;

    public HKCamera(String ip,String port,String user,String pwd){
        this.CamIP = ip;
        this.CamPort = port;
        this.CamLoginName = user;
        this.CamLoginPwd = pwd;
    }
}
