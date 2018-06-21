package com.lssl.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.evernote.android.job.JobManager;
import com.konecty.rocket.chat.R;
import com.lssl.utils.AppSharedPreferences;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiangpu.activity.ForwardActivity;
import com.xiangpu.appversion.UpdataInfo;
import com.xiangpu.bean.UserCompBean;
import com.xiangpu.bean.UserInfo;
import com.xiangpu.bean.UserInfoBean;
import com.xiangpu.utils.AlarmSound;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.StoredData;
import com.xiangpu.utils.URLConfigManage;
import com.xiangpu.utils.UncaughtException;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;
import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import chat.rocket.android.BuildConfig;
import chat.rocket.android.InitializeUtils;
import chat.rocket.android.RocketChatApplication;
import chat.rocket.android.RocketChatCache;
import chat.rocket.android.RocketChatJobCreator;
import chat.rocket.android.helper.Logger;
import chat.rocket.android.helper.OkHttpHelper;
import chat.rocket.android.service.ServerConnectivity;
import chat.rocket.android.widget.RocketChatWidgets;
import chat.rocket.android_ddp.DDPClient;
import chat.rocket.core.models.ServerInfo;
import chat.rocket.persistence.realm.RealmStore;
import chat.rocket.persistence.realm.RocketChatPersistenceRealm;
import io.agora.openvcall.model.CurrentUserSettings;
import io.agora.openvcall.model.WorkerThread;
import io.agora.openvcall.ui.ChatActivity;
import io.agora.openvcall.ui.ChatAudioActivity;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by
 */
public class SuneeeApplication extends MultiDexApplication {

    private static final String TAG = "SuneeeApplication";
    public static final String PRESH_KEY_FRIEST_USE = "first_use";

    private static Context context;
    private static SuneeeApplication app;

    public static String ConfigMenuPath = "http://172.19.5.179:8080/NVRCT/jconfig/config.json";//菜单配置文件

    public static String configFilePath = "";

    public static UserInfo user = null;

    public static void setUser(UserInfo user) {
        SuneeeApplication.user = user;
        SharedPrefUtils.saveUserInfoData(getInstance().getApplicationContext(), user);
    }

    public static UserInfo getUser() {
        if (user.isEmpty()) {
            user = SharedPrefUtils.getUserInfo(getInstance().getApplicationContext());
        }
        return user;
    }

    public static String weilianType = "WEILIANHAI";  // {weilianhai 、weilianbao 、weilianwa}

    public static List<UserInfoBean.DataBean.SpatialBean> spatial;

    //版本
    public UpdataInfo updatainfo = new UpdataInfo();

    public static void setSpatial(List<UserInfoBean.DataBean.SpatialBean> spatial) {
        getInstance().spatial = spatial;
    }

    public static List<UserInfoBean.DataBean.SpatialBean> getSpatial() {
        if (spatial == null) {
            spatial = new ArrayList<>();
        }
        return spatial;
    }

    private static UserCompBean userCompBean;

    public static void setUserCompBean(UserCompBean userCompBean) {
        SuneeeApplication.userCompBean = userCompBean;
    }

    public static UserCompBean getUserCompBean() {
        if (userCompBean == null) {
            userCompBean = new UserCompBean();
        }
        return userCompBean;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    public int screenWidth;
    public int screenHeight;

    public static URLConfigManage configManage = new URLConfigManage();

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private CopyOnWriteArrayList<Activity> activityStack;
    public static boolean checkVersion = false;
    public static boolean clickMsg = false; // 点击消息

    public static SuneeeApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = getApplicationContext();

        activityStack = new CopyOnWriteArrayList<>();

        LitePal.initialize(this); //初始化LitePal
        init();
        initWorkerThread();

        initImageLoader();

        initFileDownloader();
        RocketChatApplication.onCreate(this);
        initCloudChannel(this);//初始化阿里云
        ShortcutBadger.removeCount(context);    //初始化图标小红点为0

        Connector.getDatabase();  // 创建数据库表
//        RocketChatCache.INSTANCE.setConnectStus(ServerConnectivity.STATE_DISCONNECTED+"");
//        chat.rocket.android.service.ChatConnectivityManager.getInstance(this).keepAliveServer();
//        String hostname = RocketChatCache.INSTANCE.getSelectedServerHostname();
//        if (hostname == null) {
//            InitializeUtils.getInstance().serviceConnection(this);
//        }
        CrashReport.initCrashReport(getApplicationContext(), "5c3525f345", true);
//        initRocketChatApplication(this);
    }

    private void initRocketChatApplication(Context context) {
        RocketChatCache.INSTANCE.initialize(context);
        JobManager.create(context).addJobCreator(null);
        DDPClient.initialize(OkHttpHelper.INSTANCE.getClientForWebSocket());
//        Fabric.with(this, new Kit[]{new Crashlytics()});
        RocketChatPersistenceRealm.init(context);
        List<ServerInfo> serverInfoList =chat.rocket.android.service.ChatConnectivityManager.getInstance(context).getServerList();
        Iterator var2 = serverInfoList.iterator();

        while (var2.hasNext()) {
            ServerInfo serverInfo = (ServerInfo) var2.next();
            RealmStore.put(serverInfo.getHostname());
        }

        RocketChatWidgets.initialize(context, OkHttpHelper.INSTANCE.getClientForDownloadFile());
        if (Build.VERSION.SDK_INT < 21) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        RxJavaPlugins.setErrorHandler((e) -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }

            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }

            Logger.INSTANCE.report(e);
        });
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
                RocketChatCache.INSTANCE.setAliyunDeviceId(pushService.getDeviceId());
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        MiPushRegister.register(applicationContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
        HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
    }

    /**
     * 初始化图片加载库
     */
    public void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
//                .showImageOnLoading(R.drawable.ic_stub)            //加载图片时的图片
//                .showImageForEmptyUri(R.drawable.ic_empty)         //没有图片资源时的默认图片
//                .showImageOnFail(R.drawable.ic_error)              //加载失败时的图片
                .cacheInMemory(true)                               //启用内存缓存
                .cacheOnDisk(true)                                 //启用外存缓存
                .considerExifParams(true)                          //启用EXIF和JPEG图像格式
//                .displayer(new RoundedBitmapDisplayer(20))         //设置显示风格这里是圆角矩形
                .build();

        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions(480, 800) // maxwidth, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .discCacheFileCount(100) //缓存的文件数量
                .discCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(options)
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for releaseapp
                .build();//开始构建

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 初始化文件下载类
     */
    private void initFileDownloader() {
        // 1、创建Builder
        FileDownloadConfiguration.Builder builder = new FileDownloadConfiguration.Builder(this);

        // 2.配置Builder
        // 配置下载文件保存的文件夹
        builder.configFileDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "FileDownloader");
        // 配置同时下载任务数量，如果不配置默认为2
        builder.configDownloadTaskSize(1);
        // 配置失败时尝试重试的次数，如果不配置默认为0不尝试
        builder.configRetryDownloadTimes(0);
        // 开启调试模式，方便查看日志等调试相关，如果不配置默认不开启
        builder.configDebugMode(true);
        // 配置连接网络超时时间，如果不配置默认为15秒
        builder.configConnectTimeout(25000);// 25秒

        // 3、使用配置文件初始化FileDownloader
        FileDownloadConfiguration configuration = builder.build();
        FileDownloader.init(configuration);
    }

    /**
     * 初始化异常处理类
     */
    private void init() {
        UncaughtException mUncaughtException = UncaughtException.getInstance();
        mUncaughtException.init();

//        sessionClient = new SessionClient(this);
        user = new UserInfo();
        messages = new ArrayList<>();
        configFilePath = this.getFilesDir().getAbsolutePath() + "/appconfig.json";
    }

    public static Context getContext() {
        return context;
    }


    /**
     * 是否是第一次使用
     *
     * @return
     */
    public static boolean isFirstUse() {
        String isLogin = getProperty(PRESH_KEY_FRIEST_USE);
        if ("false".equals(isLogin)) {
            return false;
        }
        return true;
    }

    /**
     * 按Key存储配置信息
     *
     * @param key
     * @param value
     */
    public static void setProperty(String key, String value) {
        if (null != value) {
            AppSharedPreferences.getInstance(app).set(key, value);
        }
    }

    /**
     * 根据Key获取配置信息
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        String value = AppSharedPreferences.getInstance(app).get(key);
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

    public ArrayList<String> messages = null;

    private WorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

    public static final CurrentUserSettings mVideoSettings = new CurrentUserSettings();


    public AlarmSound sound = null;

    public void playSounde(int nTime) {
        if (sound == null) {
            sound = new AlarmSound(this);
        }
        sound.playBeepSound(nTime, R.raw.beep);
    }

    public void stopSound() {
        if (sound != null) {
            sound.stopAlarm();
        }
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
        // 判断当前集合中不存在该Activity
        if (!activityStack.contains(activity)) {
            activityStack.add(activity); // 把当前Activity添加到集合中
        }
    }

    /**
     * 是否已经存在Activity
     *
     * @param cls
     */
    public boolean isActivityExists(Class<?> cls) {
        // 判断当前集合中是否存在该Activity
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
        // 判断当前集合中存在该Activity
        if (activityStack.contains(activity)) {
            activityStack.remove(activity); // 从集合中移除
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Class<?> cls) {
        // 判断当前集合中存在该Activity
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                activity.finish();
                activityStack.remove(activity);
            }
        }
    }

    /**
     * 将堆中存在的activities全部销毁
     */
    public void finishAllActivities() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : activityStack) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : activityStack) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 内存不够时
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_MODERATE) {
            //开始自杀，清场掉所有的activity ,下面这个是自己写的方法
            Log.e("warning", "Out of memory, ready to close");
            removeALLActivity_();
        }
    }

    private NotificationManager mNotificationManager;
    private Notification mNotification;

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    /**
     * 创建挂机图标
     */
    @SuppressWarnings("deprecation")
    public void showNotification() {
        boolean isShowNotify = true;

        if (isShowNotify) {
            mNotificationManager = getNotificationManager();
            mNotification = new Notification();

            long when = System.currentTimeMillis();
            mNotification = new Notification(R.mipmap.icon, this
                    .getResources().getString(R.string.app_name), when);

            // 放置在"正在运行"栏目中
            mNotification.flags = Notification.FLAG_ONGOING_EVENT;

            RemoteViews contentView = new RemoteViews(getPackageName(),
                    R.layout.notify_status_bar);
            contentView.setImageViewResource(R.id.icon, R.mipmap.icon);
            contentView.setTextViewText(
                    R.id.title,
                    this.getResources().getString(R.string.app_name)
                            + "正在后台运行"
            );
            // contentView.setTextViewText(R.id.text, "");
            // contentView.setLong(R.id.time, "setTime", when);
            // 指定个性化视图
            mNotification.contentView = contentView;

            Intent intent = new Intent(this, ForwardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 指定内容意图
            mNotification.contentIntent = contentIntent;
            mNotificationManager.notify(R.string.app_name, mNotification);

        } else {

        }
    }

    /**
     * 创建挂机图标
     */
    @SuppressWarnings("deprecation")
    public void showNotificationAudioVideo(int type) {

        closeNotificationVideo();

        String title = "";
        if (type == 1) {
            title = "正在单人视频聊天";
        } else {
            title = "正在单人音频聊天";
        }

        //if (type == 1)
        {
            mNotificationManager = getNotificationManager();
            mNotification = new Notification();

            long when = System.currentTimeMillis();
            mNotification = new Notification(R.mipmap.icon, this
                    .getResources().getString(R.string.app_name), when);

            // 放置在"正在运行"栏目中
            mNotification.flags = Notification.FLAG_ONGOING_EVENT;

            RemoteViews contentView = new RemoteViews(getPackageName(),
                    R.layout.notify_status_bar);
            contentView.setImageViewResource(R.id.icon, R.mipmap.icon);
            contentView.setTextViewText(
                    R.id.title, title
            );
            // contentView.setTextViewText(R.id.text, "");
            // contentView.setLong(R.id.time, "setTime", when);
            // 指定个性化视图
            mNotification.contentView = contentView;

            Intent intent = null;
            if (type == 1) {
                intent = new Intent(this, ChatActivity.class);
            } else {
                intent = new Intent(this, ChatAudioActivity.class);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 指定内容意图
            mNotification.contentIntent = contentIntent;
            mNotificationManager.notify(10000, mNotification);
        }
    }

    public void closeNotificationVideo() {
        mNotificationManager = getNotificationManager();
        mNotificationManager.cancel(10000);
    }

    public void closeNotificationVideo(int notifyMessageId) {
        mNotificationManager = getNotificationManager();
        mNotificationManager.cancel(notifyMessageId);
    }

    public static void wakeUpAndUnlock() {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        //kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        //wl.release();
    }

    public String getAppVersion() {
        return String.valueOf(StoredData.getAppVersion(this));
    }

    public void getAppServerVersion(String sysUrl, String appVersion, String appDes) {
        updatainfo.setUrl(sysUrl);
        updatainfo.setVersion(appVersion);
        updatainfo.setDescription(appDes);
    }
}
