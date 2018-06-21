package com.lssl.action;


import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

/**
 * Created by suneee on 2016/12/20.
 */

public class BaseAction {

    public <T> T json2Object(String jsonStr, Class<T> cls) {

        //获取T的类型
        Type mySuperClass = getClass().getGenericSuperclass();

        T result = JSON.parseObject(jsonStr, cls);
        return result;
    }

}
