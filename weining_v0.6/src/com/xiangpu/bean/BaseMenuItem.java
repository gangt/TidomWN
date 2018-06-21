package com.xiangpu.bean;

import java.util.HashMap;

/**
 * Created by Andi on 2017/5/13.
 */

public class BaseMenuItem {

    private static HashMap<String,String> baseMenu = new HashMap<>();

    BaseMenuItem(){
        if(baseMenu.isEmpty()){
            //搜索
            baseMenu.put("Search","com.lssl.activity.SearchActivity");
            //创建指挥
            baseMenu.put("CommandCenter_CreateCommand","com.xiangpu.activity.commandcenter.CreateCommandActivity");
            //删除内容
            baseMenu.put("CommandCenter_Deletecommand","com.xiangpu.activity.commandcenter.AddCommandActivity");
            //新增内容
            baseMenu.put("CommandCenter_Addcommand","com.xiangpu.activity.commandcenter.AddCommandActivity");
            //中控室
            baseMenu.put("CommandCenter_Controlroom","com.xiangpu.activity.commandcenter.ControlRoomActivity_new");

            //指挥切换
            baseMenu.put("CommandCenter_SwitchCommand","com.xiangpu.activity.commandcenter.SwitchCommandActivity");
            //指挥管理
            baseMenu.put("CommandCenter_CommandManage","com.xiangpu.activity.commandcenter.CommandManageActivity");
            //历史查询
            baseMenu.put("CommandCenter_HistorySearch","com.xiangpu.activity.commandcenter.CommandHistoryActivity");

        }
    }

    public String changeItemSub(String type,String sub){
        if("url".equals(type)){//url
            return sub;
        }else if("action".equals(type)){//atction
            if(baseMenu.isEmpty()){
                return sub;
            }else{
                String child = baseMenu.get(sub);
                if(child != null){
                    return child;
                }else{
                    return sub;
                }
            }
        }
        return "";
    }
}
