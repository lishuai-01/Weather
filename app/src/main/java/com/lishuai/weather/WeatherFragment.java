package com.lishuai.weather;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lishuai.weather.bean.Weather;
import com.lishuai.weather.databinding.FragmentWeatherBinding;
import com.lishuai.weather.util.GsonUtil;
import com.lishuai.weather.util.HttpUtil;
import com.lishuai.weather.util.WeatherCache;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherFragment extends Fragment {

    private static final String ARG_CITY = "city";

    private String mCurrentCity;

    private FragmentWeatherBinding binding;

    public static WeatherFragment newInstance(String city) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mCurrentCity = getArguments().getString(ARG_CITY);
            getWeather(mCurrentCity);
        }
//
        binding.indexHorizontalScrollView.setToday24HourView(binding.today24HourView);

    }

    private void initWeather(final Weather weather) {
        if (weather == null || weather.getData() == null) {
            return;
        }

        Weather.DataBean dataBean = weather.getData().get(0);


        if (!dataBean.getWin().isEmpty()) {
            String win = dataBean.getWin().get(0).replace("[", "").replace("]", "");
            binding.tvWinAndHumidity.setText(win + dataBean.getWin_speed() + " | 湿度" + dataBean.getHumidity() + "%");
        }

        binding.tvTemperature.setText(dataBean.getTem().replace("℃",""));
        binding.tvWea.setText(dataBean.getWea());

        binding.tvTodayWea.setText(dataBean.getWea());
        binding.tvTodayTemps.setText(dataBean.getTem1().replace("℃", "") + "/" + dataBean.getTem2());
        if (!TextUtils.isEmpty(dataBean.getAir_level())) {
            binding.tvTodayAirLevel.setText(dataBean.getAir_level());
            binding.tvTodayAirLevel.setVisibility(View.VISIBLE);
        }

        Weather.DataBean TomorrowDataBean = weather.getData().get(1);
        binding.tvTomorrowWea.setText(TomorrowDataBean.getWea());
        binding.tvTomorrowTemps.setText(TomorrowDataBean.getTem1().replace("℃", "") + "/" + TomorrowDataBean.getTem2());
        if (!TextUtils.isEmpty(TomorrowDataBean.getAir_level())) {
            binding.tvTomorrowAirLevel.setText(TomorrowDataBean.getAir_level());
            binding.tvTomorrowAirLevel.setVisibility(View.VISIBLE);
        }

        binding.tvLifeIndex.setText("");
        //生活建议
        for (Weather.DataBean.IndexBean bean : dataBean.getIndex()) {
            String index = bean.getTitle() + " : " + bean.getDesc();
            binding.tvLifeIndex.append(index);
            binding.tvLifeIndex.append("\n\n");
        }

        binding.bgWeekForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeekForecastActivity.class);
                intent.putExtra("weather", weather);
                startActivity(intent);
            }
        });
    }

    private void getWeather(String cityName) {
        cityName = cityName.replace("市", "");

        // 检查有没有缓存
        Weather cacheWeather = WeatherCache.getWeather(getActivity(), cityName);

        if (cacheWeather != null) {

            // 检查数据是否过期
            DateTime updateDate = DateTime.parse(cacheWeather.getUpdate_time(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
            DateTime currentDate = DateTime.now();

            if (updateDate.getDayOfYear() == currentDate.getDayOfYear()) {
                initWeather(cacheWeather);
                return;
            }
        }

        String weatherUrl = "https://tianqiapi.com/api?version=v1&appid=48757769&appsecret=1nYqTxbh&city=" + cityName;
        final String finalCityName = cityName;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = GsonUtil.gsonToBean(responseText, Weather.class);
                Log.d("getWeather", weather.toString());
                if (getActivity() != null) {
                    if (weather != null) {
                        // 缓存 Weather
                        WeatherCache.saveWeather(getActivity(), finalCityName, responseText);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initWeather(weather);
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
//                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}