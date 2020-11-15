package com.lishuai.weather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lishuai.weather.util.CityCache;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private MagicIndicator mMagicIndicator;
    private ImageView mIvAddCity;
    private TextView mTvCurrentCity;
    private JDCityPicker mCityPicker;

    private WeatherPagerAdapter mWeatherPagerAdapter;
    private List<String> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.viewPager);
        mMagicIndicator = findViewById(R.id.magicIndicator);
        mIvAddCity = findViewById(R.id.ivAddCity);
        mTvCurrentCity = findViewById(R.id.tvCurrentCity);

        mIvAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityManagerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkCityList();
            }
        }, 100);
        initViewPager();

    }

    /**
     * 检查是否有城市列表，如果没有则自动弹出城市选择器
     */
    private void checkCityList() {
        if (CityCache.getCityList(MainActivity.this).isEmpty()) {
            if (mCityPicker == null) {
                initPicker();
            }
            mCityPicker.showCityPicker();
        }
    }

    /**
     * 初始化 ViewPager
     */
    private void initViewPager() {
        cityList = CityCache.getCityList(this);
        mWeatherPagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), cityList);
        mViewPager.setAdapter(mWeatherPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentCity.setText(cityList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        if (!cityList.isEmpty()) {
            mTvCurrentCity.setText(cityList.get(0));
        }
        initIndicator();
    }

    /**
     * 初始化指示器
     */
    private void initIndicator() {
        CircleNavigator circleNavigator = new CircleNavigator(this);
        circleNavigator.setCircleCount(cityList.size());
        circleNavigator.setCircleColor(Color.WHITE);
        circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
            @Override
            public void onClick(int index) {
                mViewPager.setCurrentItem(index);
            }
        });
        mMagicIndicator.setNavigator(circleNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    /**
     * 初始化城市选择器
     */
    private void initPicker() {
        mCityPicker = new JDCityPicker();
        JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

        jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
        mCityPicker.init(this);
        mCityPicker.setConfig(jdCityConfig);
        mCityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                Log.d("picker", "城市选择结果：\n" + province.getName() + "(" + province.getId() + ")\n"
                        + city.getName() + "(" + city.getId() + ")\n"
                        + district.getName() + "(" + district.getId() + ")");

                CityCache.addCity(MainActivity.this, city.getName());
                cityList.add(city.getName());
                mWeatherPagerAdapter.update(cityList);
                mViewPager.setCurrentItem(cityList.size());
                if (!cityList.isEmpty()) {
                    mTvCurrentCity.setText(cityList.get(cityList.size() - 1));
                }
                initIndicator();
            }

            @Override
            public void onCancel() {

            }
        });
    }
}