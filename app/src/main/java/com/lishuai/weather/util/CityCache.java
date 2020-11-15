package com.lishuai.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class CityCache {

    public static List<String> getCityList(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String cityListJson = prefs.getString("cityList", "");
        if (TextUtils.isEmpty(cityListJson)) {
            // 无城市列表
            return new ArrayList<>();
        } else {
            try {
                return GsonUtil.parserJsonToArrayBeans(cityListJson, String.class);
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    public static void addCity(Context context, String city) {
        List<String> cityList = getCityList(context);
        cityList.add(city);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("cityList", GsonUtil.toJsonString(cityList));
        editor.apply();
    }

    public static void saveCitys(Context context, List<String> cityList) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("cityList", GsonUtil.toJsonString(cityList));
        editor.apply();
    }

}
