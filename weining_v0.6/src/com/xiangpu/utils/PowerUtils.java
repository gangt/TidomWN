package com.xiangpu.utils;

import com.xiangpu.bean.UserInfoBean;
import com.xiangpu.common.Constants;

import java.util.List;

/**
 * Created by fangfumin on 2017/10/17.
 */

public class PowerUtils {

    /**
     * 登录权限检查
     *
     * @param weilianType     登录空间（海宝娃）
     * @param spatialBeanList 权限数组
     * @return 是否有权限登录
     */
    public static boolean checkLoginPower(String weilianType, List<UserInfoBean.DataBean.SpatialBean> spatialBeanList) {
        if (Constants.WEI_LIAN_HAI.equals(weilianType)) weilianType = Constants.HAI;
        if (Constants.WEI_LIAN_BAO.equals(weilianType)) weilianType = Constants.BAO;
        if (Constants.WEI_LIAN_WA.equals(weilianType)) weilianType = Constants.WA;
        for (UserInfoBean.DataBean.SpatialBean spatialBean : spatialBeanList) {
            if (weilianType.equals(spatialBean.getSpaceCode()) && spatialBean.getHaspower() == 1) {
                return true;
            }
        }
        return false;
    }

}
