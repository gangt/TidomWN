package com.tayvision.gdmap.controler;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.tayvision.gdmap.util.AMapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 关键字查询控制类
 * Created by huangda on 2017/3/27.
 */

public class PoiKeywordSearchControler implements Inputtips.InputtipsListener {
    public Context mContext;

    public PoiKeywordSearchControler(Context mContext) {
        this.mContext = mContext;
    }

    public OnPoiSearchListener onPoiSearchListener;

    public void setOnPoiSearchListener(OnPoiSearchListener onPoiSearchListener) {
        this.onPoiSearchListener = onPoiSearchListener;
    }

    /**
     * @param keyword 输入的关键字
     * @param city    查询的城市名称或城市编码，传null或""则为全国
     */
    public void poiKeywordSerach(String keyword, String city) {
        if (!AMapUtil.IsEmptyOrNullString(keyword.trim())) {
            InputtipsQuery inputquery = new InputtipsQuery(keyword, city);
            Inputtips inputTips = new Inputtips(mContext, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
            ArrayList<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            onPoiSearchListener.onPoiSearchSuccess(listString, rCode);

        } else {
            onPoiSearchListener.onPoiSearchFail(rCode);
        }
    }

    public interface OnPoiSearchListener {
        void onPoiSearchSuccess(ArrayList<String> listString, int successCode);

        void onPoiSearchFail(int errorCode);
    }
}
