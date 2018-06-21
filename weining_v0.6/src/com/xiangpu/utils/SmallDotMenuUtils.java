package com.xiangpu.utils;

import android.content.Context;

import com.lssl.activity.SuneeeApplication;
import com.xiangpu.bean.MenuBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 小圆点菜单数据源解析工具类
 * Created by huangda on 2017/10/16.
 */

public class SmallDotMenuUtils {
    /**
     *
     * @param context
     * @param loadkey  模块名
     * @return
     */
    public static ArrayList<MenuBean.MenuItem> getMenuItems(Context context, String loadkey) {
        try {
            return (ArrayList<MenuBean.MenuItem>) readMenuItem(context, loadkey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读本地json
     *
     * @param key
     * @return
     * @throws IOException
     */
    private static List<MenuBean.MenuItem> readMenuItem(Context context, String key)
            throws IOException {

        List<MenuBean.MenuItem> list = new ArrayList<MenuBean.MenuItem>();

        if ("".equals(key)) {
            return list;
        }

        BufferedReader br = null;

        String localAppConfigFilePath = SuneeeApplication.getInstance().configFilePath;
        File file = new File(localAppConfigFilePath);

        if (file.exists()) {
            //读取服务器下载文件
            String config = SuneeeApplication.getInstance().ConfigMenuPath;
            FileInputStream fileInputStream = new FileInputStream(config);
            br = new BufferedReader(new InputStreamReader(fileInputStream));
        } else {
            //读取本地文件
            InputStream in = context.getAssets().open("config.json");
            br = new BufferedReader(new InputStreamReader(in));
            //in.close();
        }

        //读取本地文件
        //InputStream in = this.getAssets().open("config.json");
        //br = new BufferedReader(new InputStreamReader(in));

        if (br != null) {

            StringBuffer input = new StringBuffer();
            String line = null;

            while ((line = br.readLine()) != null) {
                input.append(line);
            }
            String result = input.toString();
            result = result.replaceAll("\r", "");
            result = result.replaceAll("	", "");

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONObject jsonOb = jsonObject.getJSONObject("config");

                // 返回json的数组
                JSONArray jsonArray = jsonOb.getJSONArray(key);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                    MenuBean.MenuItem menuItem = new MenuBean().new MenuItem();
                    menuItem.setMenuItemName(jsonObject2.getString("title"));
                    menuItem.setMenuItemIconName(jsonObject2.getString("icon"));

                    String type = jsonObject2.getString("type");
                    menuItem.setMenuItemType(type);
                    menuItem.setMenuItemSub(type, jsonObject2.getString("sub"));

                    list.add(menuItem);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
