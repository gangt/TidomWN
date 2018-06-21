package com.tayvision.gdmap.controler;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.tayvision.gdmap.util.GDConstants;
import com.tayvision.gdmap.util.GDToastUtil;


/**
 * Created by huangda on 2017/3/24.
 */

public class RouteSearchControler implements RouteSearch.OnRouteSearchListener {

    public static final int ROUTE_TYPE_BUS = 1;
    public static final int ROUTE_TYPE_DRIVE = 2;
    public static final int ROUTE_TYPE_WALK = 3;
    public static final int ROUTE_TYPE_CROSSTOWN = 4;
    public static final int ROUTE_TYPE_RIDE = 5;


    private static String mCurrentCityName = "北京";

    private OnRouteSearchResultListener onRouteSearchResultListener;
    private RouteSearch mRouteSearch;
    private Context mContext;

    /**
     * 根据起点和终点规划路线
     *
     * @param context
     * @param mStartPoint
     * @param mEndPoint
     * @param routeType
     * @param mode
     */
    public void calculateRouteAsyn(Context context, LatLonPoint mStartPoint, LatLonPoint mEndPoint,
                                   int routeType, int mode) {
        mContext = context;

        if (mRouteSearch == null) {
            mRouteSearch = new RouteSearch(mContext);
        }
        mRouteSearch.setRouteSearchListener(this);
        {
            if (mStartPoint == null) {
                GDToastUtil.show(mContext, "起点未设置");
                return;
            }
            if (mEndPoint == null) {
                GDToastUtil.show(mContext, "终点未设置");
            }
            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                    mStartPoint, mEndPoint);
            if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
                RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,
                        mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
                mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
            } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
                RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                        null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
                mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
            } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
                RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
                mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
            } else if (routeType == ROUTE_TYPE_CROSSTOWN) {
                RouteSearch.FromAndTo fromAndTo_bus = new RouteSearch.FromAndTo(
                        mStartPoint, mEndPoint);
                RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo_bus, mode,
                        "呼和浩特市", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
                query.setCityd("农安县");
                mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
            } else if (routeType == ROUTE_TYPE_RIDE) {
                RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo, mode);
                mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
            }
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {
        handleRouteSearchedResult(busRouteResult, errorCode);
    }


    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {
        handleRouteSearchedResult(driveRouteResult, errorCode);
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {
        handleRouteSearchedResult(walkRouteResult, errorCode);
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int errorCode) {
        handleRouteSearchedResult(rideRouteResult, errorCode);
    }

    private void handleRouteSearchedResult(RouteResult result, int errorCode) {
        if (onRouteSearchResultListener == null) {
            return;
        }
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result instanceof BusRouteResult) {
                if (result != null && ((BusRouteResult) result).getPaths() != null) {
                    if (((BusRouteResult) result).getPaths().size() > 0) {
                        onRouteSearchResultListener.onRouteSearchSuccess(result, errorCode);
                    }
                } else {
                    onRouteSearchResultListener.onRouteSearchSuccess(result, GDConstants.NO_RESULT);
                }
            } else if (result instanceof DriveRouteResult) {
                if (result != null && ((DriveRouteResult) result).getPaths() != null) {
                    if (((DriveRouteResult) result).getPaths().size() > 0) {
                        onRouteSearchResultListener.onRouteSearchSuccess(result, errorCode);
                    }
                } else {
                    onRouteSearchResultListener.onRouteSearchSuccess(result, GDConstants.NO_RESULT);
                }
            } else if (result instanceof WalkRouteResult) {
                if (result != null && ((WalkRouteResult) result).getPaths() != null) {
                    if (((WalkRouteResult) result).getPaths().size() > 0) {
                        onRouteSearchResultListener.onRouteSearchSuccess(result, errorCode);
                    }
                } else {
                    onRouteSearchResultListener.onRouteSearchSuccess(result, GDConstants.NO_RESULT);
                }
            } else if (result instanceof RideRouteResult) {
                if (result != null && ((RideRouteResult) result).getPaths() != null) {
                    if (((RideRouteResult) result).getPaths().size() > 0) {
                        onRouteSearchResultListener.onRouteSearchSuccess(result, errorCode);
                    }
                } else {
                    onRouteSearchResultListener.onRouteSearchSuccess(result, GDConstants.NO_RESULT);
                }
            }

        } else {
            onRouteSearchResultListener.onRouteSearchFail(errorCode);
        }
    }


    public void setonRouteSearchResultListener(OnRouteSearchResultListener listener) {
        this.onRouteSearchResultListener = listener;
    }

    public interface OnRouteSearchResultListener {
        void onRouteSearchSuccess(RouteResult result, int successCode);

        void onRouteSearchFail(int errorCode);
    }
}
