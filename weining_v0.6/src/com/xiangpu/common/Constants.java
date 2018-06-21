package com.xiangpu.common;

import android.os.Environment;

import java.io.File;

/**
 * 静态变量和常量
 * Created by andi on 2017/2/23 0020.QQ:629585040
 */
public class Constants {

    public static final String APP_ID = "1105808397";
    public final static String CURRENT_COMMAND_ID = "currentCommandId";

    public final static String HOST = "58.250.204.31";
    public final static String PORT = "18880";

    public final static String REQUEST_TYPE_JSON = "json";
    public final static String DEVICE_TYPE = "android";
    public final static String TARGET_PAGE = "targetPage";
    public final static String CREATE_CONTENT = "createContent";
    public final static String DAELETE_CONTENT = "deleteContent";

    public final static String REFRESH_MESSAGE_LIST = "refresh_message_list";//刷新消息列表
    public final static String SEND_ROUTER_TO_UCP = "send_Router_To_Ucp";//发送路由地址到定子链
    public final static String IS_OPEN_MESSAGE_PUSH = "is_open_message_push";//消息推送的开关
    public final static String CLIENT_ID = "client_id";//个推推送设备编号
    public final static String MODULE_UCP = "ucp";//定子链
    public final static String MODULE_HNQ = "hnq";//汇能器

    public final static String SEARCH = "search"; // 搜索
    public final static String MY = "my"; // 我的
    public final static String MESSAGE = "message"; // 消息
    public final static String LOGOUT = "logout"; // 登录
    public final static String WEI_LIAN_TYPE = "weiliantype"; // 登录入口类型
    public final static String WEI_LIAN_HAI = "WEILIANHAI"; // 微链海
    public final static String WEI_LIAN_BAO = "WEILIANBAO";// 微链宝
    public final static String WEI_LIAN_WA = "WEILIANWA";// 微链娃

    public static final String HAI = "HAI"; // 微链宝企业编码
    public static final String BAO = "BAO"; // 微链宝企业编码
    public static final String WA = "WA"; // 微链娃企业编码

    public static final String FUNCTION_INTRODUCE = "http://xp.weilian.cn/XPhelpWord"; // 功能介绍页面地址

    public final static String USER_ID = "userId";
    // 密码标识
    public static final String PSDWORD_SP = "psdWord";
    public final static String SELECT_COMPANY_ID = "selectCompanyId";//在七星页选择的集团编码或子公司编码

    public static boolean is_debug = true; // false表示正式环境，true表示测试环境

    //    public static final String URL_CONFIG_BASE_TEST = "http://172.19.6.104:8091/NVRCT"; // 内网测试地址
    public static final String URL_CONFIG_BASE_TEST_OUTERNET = "http://xptest.wn.sunmath.cn:8199/NVRCT"; // 外网测试地址
    public static final String URL_CONFIG_BASE_FROMAL = "http://xp.wn.sunmath.cn:8199/NVRCT"; // 外网正式地址

    // 通过集团名称查询鉴权配置列表
    public static final String URL_CONFIG_AUTHENTICATION = (is_debug ? URL_CONFIG_BASE_TEST_OUTERNET : URL_CONFIG_BASE_FROMAL) + "/authController/getAuthenticationForClient";

    // 获取七星页配置接口
    public static final String GET_SEVEN_STAR_BY_ORGNAME = (is_debug ? URL_CONFIG_BASE_TEST_OUTERNET : URL_CONFIG_BASE_FROMAL) + "/sevenStarController/getSevenStarByOrgNameForMobile";
    // 获取所有公司信息（包含多级关系）
    public static final String GET_ALL_COMPANY_BY_INDUSTRY = (is_debug ? URL_CONFIG_BASE_TEST_OUTERNET : URL_CONFIG_BASE_FROMAL) + "/sevenStarController/getAllCompanyByIndustry";
    public static final String GET_COMPANY_TREE = "http://172.19.4.157:8080/NVRCT/appController/getAllCompany";

    // 获取首页配置信息
    public static final String URL_CONFIG_PATH = (is_debug ? URL_CONFIG_BASE_TEST_OUTERNET : URL_CONFIG_BASE_FROMAL) + "/appController/getApplicationForClient";

    public static int stg = 1;   //1:开发环境
    public static final String DEVELOP_BASE_COMMAND_CENTER_URL = "http://172.19.5.177:9090";   //开发环境
    public static final String TEST_BASE_COMMAND_CENTER_URL = "http://172.19.6.104:8091/NVRCT";  //测试环境
    public static final String PRODUCT_BASE_COMMAND_CENTER_URL = "http://xpsgbak.weilian.cn:33122/NVRCT";  //生产环境
    public static final String BASE_COMMAND_CENTER_URL = (stg == 0 ? DEVELOP_BASE_COMMAND_CENTER_URL : PRODUCT_BASE_COMMAND_CENTER_URL);

    public final static String TEST_Domain = "vr12.weilian.cn";
    public final static String TEST_BASE_URL = "http://" + TEST_Domain + "/account_auth_admin";  // 正式环境地址
    public final static String TEST_BASE_URL_GETUI = "http://172.19.6.104:8081/vr-push/user/regit"; //getui
    public final static String TEST_BASE_SEARCH = "http://172.19.6.104:8088//solr/mycore/select/";

    public final static String UC_Domain = "uc.weilian.cn";
    public final static String UC_BASE_URL = "http://" + UC_Domain + "/account_auth_admin";
    public final static String UC_BASE_URL_GETUI = "http://xpomg.weilian.cn/vr-push";
    public final static String UC_BASE_SEARCH = "http://xpomg.weilian.cn/solr/mycore/select/";

    public static final String module_user = (is_debug ? TEST_BASE_URL : UC_BASE_URL);
    public static final String moduleSearchAudio = (is_debug ? TEST_BASE_SEARCH : UC_BASE_SEARCH);
    public static final String modulePushToken = (is_debug ? TEST_BASE_URL_GETUI : UC_BASE_URL_GETUI);
    public static final String BASE_UPLOADIMG = "http://" + (is_debug ? TEST_Domain : UC_Domain) + "/file-service/file-api.upload?domain=user&type=photo"; //上传图片
    public static final String BASE_HEADIMG_URL = "http://" + (is_debug ? TEST_Domain : UC_Domain) + "/file-service/file-api.download?useOld=false&domain=user&type=photo&fileName="; //图片网络地址

    public static final String CONSTRUCTING_URL = "http://xp.weilian.cn/nouse/websiteupgrade.html";   //建设中页面

    public static final String TEST_SERVICE_URL = "http://172.19.6.178:8090";//测试
    public static final String UC_SERVICE_URL = "http://suneee.oa.weilian.cn";//生产
    public static final String BASE_SERVICE_URL = (is_debug ? TEST_SERVICE_URL : UC_SERVICE_URL);

    public static final String SERVICE_URL = BASE_SERVICE_URL + "/command-web/customer/";   //客满聊天页面

    //注册页面
    public static final String TEST_REGISTER = "10.0.0.97:83";   //注册测试地址ip
    public static final String moduleRegisterBase = "http://" + (is_debug ? TEST_REGISTER : UC_Domain);
    public static final String moduleRegister = moduleRegisterBase;

    public static final String TEST = "http://172.19.6.176:8280";
    public static final String FORMAL = "http://suneee.oa.weilian.cn";

    public static final String ZHUANZILIAN_DEBUG = TEST + "/frame/index.html?type=4";    //  转子链
    public static final String LIZILIAN_DEBUG = TEST + "/frame/index.html?type=5";    //  粒子链

    public static final String ZHUANZILIAN_FORMAL = FORMAL + "/frame/index.html?type=4";    //  转子链
    public static final String LIZILIAN_FORMAL = FORMAL + "/frame/index.html?type=5";    //  粒子链

    public static final String ZHUANZILIAN_URL = (is_debug ? ZHUANZILIAN_DEBUG : ZHUANZILIAN_FORMAL);    //  转子链
    public static final String LIZILIAN_URL = (is_debug ? LIZILIAN_DEBUG : LIZILIAN_FORMAL);    //  粒子链


    public static final String PERSPECTIVE_VIEW_URL = "http://ecmp.weilian.cn/"; // 视点观
    public static final String LANDSCAPE_VIEW_URL = "http://scn.vr.weilian.cn:8111/sale/H5/mobile/manufacture.html"; // 场景观
    public static final String LIVELY_VIEW_URL = "http://scn.vr.weilian.cn:8111/sale/H5/mobile/credit.html"; // 活流观
    public static final String BUSINESS_CENTER_URL = "http://scn.vr.weilian.cn:8111/sale/H5/mobile/jnq_index.html"; // 商业中心

    public static String DISNEYORDER_URL = "http://suneee.dcp.weilian.cn/disneyOrder/listTodayOrder"; // 焦点圈数据
    public static final String COMMAND_CENTER_URL = "http://suneee.oa.weilian.cn/command-web/"; // 指挥中心
    public static final String SPACETIME_URL = "http://xpsgbak.weilian.cn/activity/activity-vivw.html"; // 时空中心
    public static final String HUINENGQI_URL = "http://suneee.dcp.weilian.cn/sunEee-gather/index.html";// 汇能器
    public static final String JUNENGQI_URL = "http://suneee.dcp.weilian.cn/sunEee-ju/view/index.html"; // 聚能器
    public static final String FUNENGQI_URL = "http://suneee.dcp.weilian.cn/sunEee-fu/view/index.html";// 赋能器
    public static final String POINT_SEARCH_URL = "http://suneee.dcp.weilian.cn/skzx/index.html";// 小圆点搜索
    public static final String XIANGYISHUYUAN = "http://xpsgbak.weilian.cn:33086/learnmobile/"; // 象翌书院

    //视频聊天
    public static final String Video_Domain = "http://microservice.weilian.cn";
    public static final String Video_Test = "http://172.19.4.232:4444";

    public static final String module_VIDEO_DOMAINs = (is_debug ? Video_Test : Video_Domain);
    public static final String VIDEO_AUDIO_CREATE_MEETING = module_VIDEO_DOMAINs + "/vr-push/create_meeting";//创建会议
    public static final String VIDEO_AUDIO_HANG_UP = module_VIDEO_DOMAINs + "/vr-push/hang_up";//挂断会议
    public static final String VIDEO_AUDIO_IS_MEETING = module_VIDEO_DOMAINs + "/vr-push/is_meeting";//查询用户是否在会议中
    public static final String VIDEO_AUDIO_UPDATE_MEETING = module_VIDEO_DOMAINs + "/vr-push/update_meeting";//更新会议
    public static final String VIDEO_AUDIO_QUERY_MEETING = module_VIDEO_DOMAINs + "/vr-push/query_meeting";//查询会议

    // 蒲公英app
    private static final String APPID_TEXT = "086374a1ee8176121fc7d8be6054c503";
    private static final String APPID_FORMAL = "086374a1ee8176121fc7d8be6054c503";

    public static final String APPID_PGY = (is_debug ? APPID_TEXT : APPID_FORMAL); // 蒲公英APPID

    public static final String DISNEYORDER_POWER_URL = module_user + "/personal-api.checkRole"; // 新权限接口

    //登录
    public static final String moduleLogin = module_user + "/personal-api.newloginPower"; // 多企业B用户的新接口
    //退出
    public static final String moduleLogout = module_user + "/personal-api.logout";
    //获取用户信息
    public static final String moduleDetails = module_user + "/personal-api.getUserInfo";
    //修改个人信息
    public static final String moduleUpdateUserInfo = module_user + "/personal-api.updateUserInfo";
    //修改密码
    public static final String moduleUpdatePwd = module_user + "/personal-api.changePassword";
    //重置密码
    public static final String moduleResetPwd = module_user + "/resetPwd";
    //获取手机验证码
    public static final String moduleGetMobileCheckCode = module_user + "/personal-api.findPasswordStepOne";
    //获取邮箱验证码
    public static final String moduleGetEmailCheckCode = module_user + "/personal-api.findPasswordStepOneCode";
    //忘记密码后设置新密码
    public static final String moduleSetNewPassword = module_user + "/personal-api.findPasswordStepTwo";
    //帐号绑定人脸识别
    public static final String BIND_AUTH_ACCOUNT_FACETOKEN = module_user + "/personal-api.bindAuthAccountFacktoken";

    // 版本检测
    public static final String moduleVersion = (is_debug ? URL_CONFIG_BASE_TEST_OUTERNET : URL_CONFIG_BASE_FROMAL) + "/appVersionUpgradeController/getAppVerUpPackNewest";

    public static final String SWITCH_GESTURE_FINGERPRINT = "switch_gesture_fingerprint";
    public static final String IS_OPEN_FINGERPRINT = "is_open_fingerprint";
    public static final String IS_OPEN_GESTURE = "is_open_gesture";  //手势密码是否开启

    private static final String ROOT = "xiangpu1";

    public static final String GET_APP_VERSION = is_debug ? Constants.URL_CONFIG_BASE_TEST_OUTERNET : Constants.URL_CONFIG_BASE_FROMAL;

    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT + File.separator + "Images";

    public static final String CROP_CACHE_FILE_NAME = "xiangpu_crop_cache_file";
    public static final String GESTURE_PASSWORD = "GesturePassword";

    public static String getConfigLogoPath(String path) {
        StringBuffer url = new StringBuffer();
        url.append((is_debug ? URL_CONFIG_BASE_TEST_OUTERNET : URL_CONFIG_BASE_FROMAL)).append("/").append(path);
        return url.toString();
    }

    // Face++相关的key
    public static final String API_KEY = "VBH5uF1yGbWDN5U4U74nDSilU-8np3ve";
    // Face++相关的secret
    public static final String API_SECRET = "mhvw3FwivUq1rRo4vC64eAdKy7Rnwl8F";
    // Face++相关的facetoken
    public static final String FACE_TOKEN = "face_token";
    // Face++人脸辨识度
    public static final String FACE_DIS_VALUE = "face_dis_value";
    // 刷脸登录失败，返回activity的result
    public static final int FACE_LOGIN_FAIL = 101;

    // 个人资料 start-->
    public static final String PERSON_CENTER_TEST = "http://10.0.0.97:83/frame_phone/index.html#";
    public static final String PERSON_CENTER_FROMAL = "http://uc.weilian.cn/frame_phone/index.html#";
    // 个人昵称
    public static final String PERSON_NICK_NAME = "nickName";
    // 个性签名
    public static final String PERSON_SIGNATURE = "signature";
    // 个人地区
    public static final String PERSON_AREA = "area";
    // 手机号
    public static final String PERSON_MOBLIE = "mobile";
    // 座机号
    public static final String PERSON_PHONE = "phone";
    // 确认
    public static final String PERSON_CONFIRM = "confirm";
    // 跳转Activity的key
    public static final String INTENT_ACTIVITY = "intent_activity";

    // 个人信息
    public static final String PERSON_CENTER_DATA = (is_debug ? PERSON_CENTER_TEST : PERSON_CENTER_FROMAL) + "/information?sessionId=";
    // 个人信息二级页面
    public static final String PERSON_CENTER_DATA_SECOND = (is_debug ? PERSON_CENTER_TEST : PERSON_CENTER_FROMAL) + "/amendinfo?sessionId=";
    // 修改密码
    public static final String PERSON_CENTER_CHANGE_PASSWORD = (is_debug ? PERSON_CENTER_TEST : PERSON_CENTER_FROMAL) + "/accountSafety?sessionId=";
    // 帮助与反馈
    public static final String PERSON_CENTER_HELP_FEED = "http://xptest.wn.sunmath.cn/WNhelpWord/#/";
    // 个人资料 end-->

}
