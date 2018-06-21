package com.xiangpu.utils;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static android.util.Base64.DEFAULT;

/**
 * Created by fangfumin on 2017/10/23.
 */

public class VerifyUtils {

    /**
     * 参数组合加密方法
     *
     * @param mapJson 请求参数
     * @param signKey 加密字符串
     * @return 加密后的字符串
     */
    public static String signValueEncryption(String mapJson, String signKey) {
        String signString = signValueEncryption(toMap(mapJson), signKey);
//        Log.e("mapJson", mapJson);
//        String signString = encrypt(mapJson, signKey);
//        Log.e("signString", signString);
        return signString;
    }

    /**
     * 参数组合加密方法
     *
     * @param parameters 请求参数
     * @param signKey    加密字符串
     * @return 加密后的字符串
     */
    public static String signValueEncryption(Map<String, Object> parameters, String signKey) {
        Map<String, Object> resultMap = sortMapByKey(parameters);
//        Log.e("resultMap", resultMap.toString());
        String mapJson = new Gson().toJson(resultMap);
//        Log.e("mapJson", mapJson);
        String signString = encrypt(mapJson, signKey);
//        Log.e("signString", signString);
        return signString;
    }

    /**
     * @param str Json字符串
     * @return Map
     * @创建人: ZYC
     * @Method描述: 字符串返回成Map
     * @创建时间: 2015年11月27日上午9:50:01
     */
    public static Map<String, Object> toMap(String str) {
        Gson gson = new Gson();
        Map<String, Object> gsonMap = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
        }.getType());
        return gsonMap;
    }

    /**
     * 使用Map按key进行排序
     *
     * @param map 需要排序的map
     * @return 排序后的map
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());
        sortMap.putAll(map);

        return sortMap;
    }

    /**
     * Base64转码算法加密
     *
     * @param sSrc 需要加密字符串
     * @param sKey 加密字符串
     * @return Base64加密后的字符串
     */
    public static String encrypt(String sSrc, String sKey) {
        if (sKey == null) {
            Log.e("encrypt", "Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            Log.e("encrypt", "Key长度不是16位");
            return null;
        }

        byte[] raw;
        try {
            raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return Base64.encodeToString(encrypted, DEFAULT);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }

}
