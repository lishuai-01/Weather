package com.lishuai.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.lishuai.weather.bean.Weather;


public class WeatherCache {

    public static Weather getWeather(Context context, String cityName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String weather = prefs.getString("weather_" + cityName, "");
        if (TextUtils.isEmpty(weather)) {
            // 无城市列表
            return null;
        } else {
            try {
                return GsonUtil.parserJsonToArrayBean(weather, Weather.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void saveWeather(Context context, String city, String json) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("weather_" + city, json);
        editor.apply();
    }

}
