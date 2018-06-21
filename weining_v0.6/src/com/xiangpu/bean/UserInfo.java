package com.xiangpu.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.Utils;

public class UserInfo {

    private String userId;
    private String userName;
    private String name;
    private String nick;
    private String sex;
    private String aliasName;
    private String photo;
    private String address;
    private String signature;
    private String backgroundImg;
    private String mobile;
    private String email;
    private String account;

    public String headName = "";
    public String headImg = ""; //获取用户头像的网络url地址
    public Bitmap headerBmp = null;

    public boolean isHavMessage = false;

    public String compId = "1"; // 企业id
    public String sessionId = "";
    public boolean isCommandCreater = false; //是否有创建指挥的权限
    public CommandBean curCommand = null;
    public String affiliatedCompanyId = ""; // 员工所属企业编码
    private String selectCompanyId = "";     //最终进去主页选择的企业编码

    public String getSelectCompanyId() {
        if (StringUtils.isEmpty(selectCompanyId)) {
            return SharedPrefUtils.getSelectCompanyCode(SuneeeApplication.getContext());
        }
        return selectCompanyId;
    }

    public void setSelectCompanyId(String selectCompanyId) {
        SharedPrefUtils.saveSelectCompanyCode(SuneeeApplication.getContext(), selectCompanyId);
        this.selectCompanyId = selectCompanyId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Bitmap getHeader() {
        if (!headImg.equals("")) {
            if (headImg.startsWith("http")) {
                //从网络获取
                LogUtil.i("PersonSetActivity", "获取网络头像:" + headImg);
                headerBmp = Utils.getUrlToImage(headImg);
            } else {
                //从本地获取
                headerBmp = BitmapUtils.decodeBitmap(headImg, 240, 240);
            }
        }
        if (headerBmp == null) {
            Bitmap bm = BitmapFactory.decodeResource(SuneeeApplication.getInstance().getResources(),
                    R.drawable.push);
            return BitmapUtils.getRoundImage(bm, 360);
        }
        return headerBmp;
    }

    public boolean isEmpty() {
        if (getUserId() == null || getUserName() == null) return true;
        return false;
    }

}
