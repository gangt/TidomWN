package com.xiangpu.bean;

import java.util.List;

/**
 * Created by fangfumin on 2017/7/25.
 */

public class UserInfoBean {

    /**
     * status : 1
     * code : null
     * message : 处理成功
     * data : {"userType":"BUSER","theone":"YINENG","facktoken":"","facedisValue":"75","dataPower":{"enterprise":{"companyChildren":[{"id":64,"createDate":1502347463000,"updateDate":1503561466000,"deleteFlag":"0","auditFlag":"1","comp_name":"翌能服务商","comp_code":"YINENG_EN","comp_short_name":"翌能服务商","comp_parent_code":60,"address":"","logo":"","contact":"","telephone":"","isGroup":"N","groupCode":"60","remarks":"","type":"SYSTEM","cluster_code":"YINENG","cluster_name":"广西翌能物联科技有限公司"},{"id":66,"createDate":1502868639000,"updateDate":1502868639000,"deleteFlag":"0","auditFlag":"1","comp_name":"翌能服务商FFS","comp_code":"YIENGN_FFS","comp_short_name":"FFS","comp_parent_code":60,"address":"","logo":"","contact":"","telephone":"","isGroup":"N","groupCode":"60","remarks":"","type":"SYSTEM","cluster_code":"","cluster_name":""},{"id":67,"createDate":1502899297000,"updateDate":1503566482000,"deleteFlag":"0","auditFlag":"1","comp_name":"广西创思特汽车服务有限责任公司","comp_code":"YINENG_CST","comp_short_name":"创思特汽车","comp_parent_code":60,"address":"","logo":"","contact":"","telephone":"","isGroup":"N","groupCode":"60","remarks":"","type":"SYSTEM","cluster_code":"YINENG","cluster_name":"广西翌能物联科技有限公司"}],"company":{"id":60,"comp_name":"广西翌能物联科技有限公司","comp_code":"YINENG","comp_short_name":"翌能集团","comp_parent_code":0,"address":"","logo":"","contact":"","telephone":"","isGroup":"Y","groupCode":"","remarks":"","type":"SYSTEM","cluster_code":"SUNEEE","cluster_name":"象翌微链"}},"resource":[]},"user":{"enterpriseCode":"","address":"","registerTime":{"date":16,"hours":11,"seconds":51,"month":9,"nanos":227000000,"timezoneOffset":-480,"year":117,"minutes":10,"time":1508123451227,"day":1},"signature":"","sex":false,"mobile":"13590817854","photo":"34475cd60000037f.jpg","appCode":"","sessionId":"dd21cb24fa3b4deb9366fb9766d352f8","userName":"1000046032","userId":46044,"enabled":true,"nick":"","password":"","backgroundImg":"","name":"","account":"1000046032","email":"","lastUpdateTime":{"date":16,"hours":11,"seconds":51,"month":9,"nanos":227000000,"timezoneOffset":-480,"year":117,"minutes":10,"time":1508123451227,"day":1}},"spatial":[{"spaceName":"微链海","haspower":1,"spaceCode":"WEILIANHAI"},{"spaceName":"微链宝","haspower":1,"spaceCode":"WEILIANBAO"},{"spaceName":"微链娃","haspower":1,"spaceCode":"WEILIANWA"}]}
     */

    private int status;
    private String code;
    private String message;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * userType : BUSER
         * theone : YINENG
         * facktoken :
         * facedisValue : 75
         * dataPower : {"enterprise":{"companyChildren":[{"id":64,"createDate":1502347463000,"updateDate":1503561466000,"deleteFlag":"0","auditFlag":"1","comp_name":"翌能服务商","comp_code":"YINENG_EN","comp_short_name":"翌能服务商","comp_parent_code":60,"address":"","logo":"","contact":"","telephone":"","isGroup":"N","groupCode":"60","remarks":"","type":"SYSTEM","cluster_code":"YINENG","cluster_name":"广西翌能物联科技有限公司"},{"id":66,"createDate":1502868639000,"updateDate":1502868639000,"deleteFlag":"0","auditFlag":"1","comp_name":"翌能服务商FFS","comp_code":"YIENGN_FFS","comp_short_name":"FFS","comp_parent_code":60,"address":"","logo":"","contact":"","telephone":"","isGroup":"N","groupCode":"60","remarks":"","type":"SYSTEM","cluster_code":"","cluster_name":""},{"id":67,"createDate":1502899297000,"updateDate":1503566482000,"deleteFlag":"0","auditFlag":"1","comp_name":"广西创思特汽车服务有限责任公司","comp_code":"YINENG_CST","comp_short_name":"创思特汽车","comp_parent_code":60,"address":"","logo":"","contact":"","telephone":"","isGroup":"N","groupCode":"60","remarks":"","type":"SYSTEM","cluster_code":"YINENG","cluster_name":"广西翌能物联科技有限公司"}],"company":{"id":60,"comp_name":"广西翌能物联科技有限公司","comp_code":"YINENG","comp_short_name":"翌能集团","comp_parent_code":0,"address":"","logo":"","contact":"","telephone":"","isGroup":"Y","groupCode":"","remarks":"","type":"SYSTEM","cluster_code":"SUNEEE","cluster_name":"象翌微链"}},"resource":[]}
         * user : {"enterpriseCode":"","address":"","registerTime":{"date":16,"hours":11,"seconds":51,"month":9,"nanos":227000000,"timezoneOffset":-480,"year":117,"minutes":10,"time":1508123451227,"day":1},"signature":"","sex":false,"mobile":"13590817854","photo":"34475cd60000037f.jpg","appCode":"","sessionId":"dd21cb24fa3b4deb9366fb9766d352f8","userName":"1000046032","userId":46044,"enabled":true,"nick":"","password":"","backgroundImg":"","name":"","account":"1000046032","email":"","lastUpdateTime":{"date":16,"hours":11,"seconds":51,"month":9,"nanos":227000000,"timezoneOffset":-480,"year":117,"minutes":10,"time":1508123451227,"day":1}}
         * spatial : [{"spaceName":"微链海","haspower":1,"spaceCode":"WEILIANHAI"},{"spaceName":"微链宝","haspower":1,"spaceCode":"WEILIANBAO"},{"spaceName":"微链娃","haspower":1,"spaceCode":"WEILIANWA"}]
         */

        private String userType;
        private String facktoken;
        private String facedisValue;
        private UserBean user;
        private List<SpatialBean> spatial;

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getFacktoken() {
            return facktoken;
        }

        public void setFacktoken(String facktoken) {
            this.facktoken = facktoken;
        }

        public String getFacedisValue() {
            return facedisValue;
        }

        public void setFacedisValue(String facedisValue) {
            this.facedisValue = facedisValue;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public List<SpatialBean> getSpatial() {
            return spatial;
        }

        public void setSpatial(List<SpatialBean> spatial) {
            this.spatial = spatial;
        }

        public static class UserBean {
            /**
             * enterpriseCode :
             * address :
             * registerTime : {"date":16,"hours":11,"seconds":51,"month":9,"nanos":227000000,"timezoneOffset":-480,"year":117,"minutes":10,"time":1508123451227,"day":1}
             * signature :
             * sex : false
             * mobile : 13590817854
             * photo : 34475cd60000037f.jpg
             * appCode :
             * sessionId : dd21cb24fa3b4deb9366fb9766d352f8
             * userName : 1000046032
             * userId : 46044
             * enabled : true
             * nick :
             * password :
             * backgroundImg :
             * name :
             * account : 1000046032
             * email :
             * lastUpdateTime : {"date":16,"hours":11,"seconds":51,"month":9,"nanos":227000000,"timezoneOffset":-480,"year":117,"minutes":10,"time":1508123451227,"day":1}
             */

            private String enterpriseCode;
            private String address;
            private String signature;
            private boolean sex;
            private String mobile;
            private String photo;
            private String appCode;
            private String sessionId;
            private String userName;
            private int userId;
            private boolean enabled;
            private String nick;
            private String password;
            private String backgroundImg;
            private String name;
            private String account;
            private String email;
            private String aliasName;


            public String getAliasName() {
                return aliasName;
            }

            public void setAliasName(String aliasName) {
                this.aliasName = aliasName;
            }

            public String getEnterpriseCode() {
                return enterpriseCode;
            }

            public void setEnterpriseCode(String enterpriseCode) {
                this.enterpriseCode = enterpriseCode;
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

            public boolean isSex() {
                return sex;
            }

            public void setSex(boolean sex) {
                this.sex = sex;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public String getAppCode() {
                return appCode;
            }

            public void setAppCode(String appCode) {
                this.appCode = appCode;
            }

            public String getSessionId() {
                return sessionId;
            }

            public void setSessionId(String sessionId) {
                this.sessionId = sessionId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getNick() {
                return nick;
            }

            public void setNick(String nick) {
                this.nick = nick;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getBackgroundImg() {
                return backgroundImg;
            }

            public void setBackgroundImg(String backgroundImg) {
                this.backgroundImg = backgroundImg;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }
        }

        public static class SpatialBean {
            /**
             * spaceName : 微链海
             * haspower : 1
             * spaceCode : WEILIANHAI
             */

            private String spaceName;
            private int haspower;
            private String spaceCode;

            public String getSpaceName() {
                return spaceName;
            }

            public void setSpaceName(String spaceName) {
                this.spaceName = spaceName;
            }

            public int getHaspower() {
                return haspower;
            }

            public void setHaspower(int haspower) {
                this.haspower = haspower;
            }

            public String getSpaceCode() {
                return spaceCode;
            }

            public void setSpaceCode(String spaceCode) {
                this.spaceCode = spaceCode;
            }
        }
    }
}
