package com.xiangpu.utils;

import com.xiangpu.bean.UserCompanyBean;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * description: 键值对排序
 * autour: Andy
 * date: 2018/1/29 13:31
 * update: 2018/1/29
 * version: 1.0
 */
public class MapSortUtils {

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, List<UserCompanyBean>> sortMapByKey(Map<String, List<UserCompanyBean>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, List<UserCompanyBean>> sortMap = new TreeMap(new VerifyUtils.MapKeyComparator());
        sortMap.putAll(map);

        return sortMap;
    }

}
