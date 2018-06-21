package com.tayvision.gdmap.controler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.tayvision.gdmap.util.GDConstants;
import com.tayvision.gdmap.util.ErrorInfo;
import com.tayvision.gdmap.util.TTSController;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理导航逻辑的控制类
 * Created by huangda on 2017/3/26.
 */

public class NaviControler implements AMapNaviListener {
    private static final String TAG = "NaviControler";
    private AMapNavi mAMapNavi;
    private TTSController mTtsManager;
    private Context mContext;
    private NaviLatLng mStartLatLng;
    private NaviLatLng mEndLatLng;
    private int routeType;//交通方式

    private int strategy = 0;//驾车策略
    protected List<NaviLatLng> mWayPointList;


    public NaviControler(Context context) {
        mContext = context;
        //实例化语音引擎
        mTtsManager = TTSController.getInstance(context);
        mTtsManager.init();

        //
        mAMapNavi = AMapNavi.getInstance(context);
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);

        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(75);
    }

    public void calculateRideRoute(NaviLatLng startLatLng, NaviLatLng endLatLng) {
        routeType = GDConstants.ROUTE_TYPE_RIDE;
        mStartLatLng = startLatLng;
        mEndLatLng = endLatLng;
    }

    public void calculateWalkRoute(NaviLatLng startLatLng, NaviLatLng endLatLng) {
        routeType = GDConstants.ROUTE_TYPE_WALK;
        mStartLatLng = startLatLng;
        mEndLatLng = endLatLng;
    }

    public void calculateDriveRoute(NaviLatLng startLatLng, NaviLatLng endLatLng, List<NaviLatLng> wayPointList,
                                    boolean congestion, boolean avoidspeed, boolean cost, boolean hightspeed, boolean multipleRoute) {
        routeType = GDConstants.ROUTE_TYPE_DRIVE;
        mStartLatLng = startLatLng;
        mEndLatLng = endLatLng;
        mWayPointList = wayPointList;
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(congestion, avoidspeed, cost, hightspeed, multipleRoute);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        mTtsManager.stopSpeaking();
    }

    public void onDestroy() {
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
        mTtsManager.destroy();
    }

    @Override
    public void onInitNaviSuccess() {
        Log.i(TAG,"onInitNaviSuccess---");
        switch (routeType) {
            case GDConstants.ROUTE_TYPE_DRIVE:
                calculateDriveRoute(strategy);
                break;
            case GDConstants.ROUTE_TYPE_RIDE:
                mAMapNavi.calculateRideRoute(mStartLatLng, mEndLatLng);
                break;
            case GDConstants.ROUTE_TYPE_WALK:
                mAMapNavi.calculateWalkRoute(mStartLatLng, mEndLatLng);
                break;
            default:
                break;
        }
    }

    private void calculateDriveRoute(int strategy) {
        ArrayList<NaviLatLng> sList =new ArrayList<NaviLatLng>();
        ArrayList<NaviLatLng> eList =new ArrayList<NaviLatLng>();
        sList.add(mStartLatLng);
        eList.add(mEndLatLng);
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    @Override
    public void onInitNaviFailure() {
        Toast.makeText(mContext, "init navi Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCalculateRouteSuccess() {
        if (routeType == GDConstants.ROUTE_TYPE_DRIVE) {
            mAMapNavi.startNavi(NaviType.EMULATOR);
        } else {
            mAMapNavi.startNavi(NaviType.GPS);
        }
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        //路线计算失败
        Log.e("dm", "--------------------------------------------");
        Log.i("dm", "路线计算失败：错误码=" + errorInfo + ",Error Message= " + ErrorInfo.getError(errorInfo));
        Log.i("dm", "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/");
        Log.e("dm", "--------------------------------------------");
        Toast.makeText(mContext, "errorInfo：" + errorInfo + ",Message：" + ErrorInfo.getError(errorInfo), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }


    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {
        if (i == 0) {
            Toast.makeText(mContext, "当前在主辅路过渡", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在主辅路过渡");
            return;
        }
        if (i == 1) {
            Toast.makeText(mContext, "当前在主路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在主路");
            return;
        }
        if (i == 2) {
            Toast.makeText(mContext, "当前在辅路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在辅路");
        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }
}
