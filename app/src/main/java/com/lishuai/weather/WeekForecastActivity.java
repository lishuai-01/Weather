package com.lishuai.weather;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lishuai.weather.bean.Weather;
import com.lishuai.weather.databinding.ActivityWeekForecastBinding;
import com.lishuai.weather.databinding.LayoutWeatherDetailBinding;
import com.lishuai.weather.util.DisplayUtil;

import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

public class WeekForecastActivity extends AppCompatActivity {

    private ActivityWeekForecastBinding binding;
    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeekForecastBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        weather = (Weather) getIntent().getSerializableExtra("weather");
        binding.tvTitle.setText(weather.getCity());
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initIndicator();
        initViewPager();
    }

    private void initIndicator() {
        final CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.25f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return weather.getData() == null ? 0 : weather.getData().size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                Weather.DataBean dataBean = weather.getData().get(index);
                int startIndex = dataBean.getDay().indexOf("（");
                int endIndex = dataBean.getDay().indexOf("）");
                String title = dataBean.getDay().substring(startIndex + 1, endIndex);

                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
                View customLayout = LayoutInflater.from(context).inflate(R.layout.layout_weather_header, null);
                final TextView tvDay = customLayout.findViewById(R.id.tvDay);
                final TextView tvDate = customLayout.findViewById(R.id.tvDate);

                DateTime date = DateTime.parse(dataBean.getDate(), DateTimeFormat.forPattern("yyyy-MM-dd"));

                tvDay.setText(title);
                tvDate.setText(date.getMonthOfYear() + "/" + date.getDayOfMonth());
                commonPagerTitleView.setMinimumWidth(DisplayUtil.dip2px(getApplicationContext(), 80));
                commonPagerTitleView.setPadding(DisplayUtil.dip2px(getApplicationContext(), 20), 0, 0, 0);
                commonPagerTitleView.setContentView(customLayout);
                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.viewPager.setCurrentItem(index);
                    }
                });

                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
                        tvDay.setTextColor(Color.WHITE);
                        tvDate.setTextColor(Color.WHITE);
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        tvDay.setTextColor(Color.parseColor("#C5E5FC"));
                        tvDate.setTextColor(Color.parseColor("#C5E5FC"));
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

                    }
                });
                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setColors(Color.parseColor("#ffffff"));
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(false);
        binding.magicIndicator.setNavigator(commonNavigator);
    }

    private void initViewPager() {
        final ArrayList<LayoutWeatherDetailBinding> views = new ArrayList<>();
        for (int i = 0; i < weather.getData().size(); i++) {
            views.add(LayoutWeatherDetailBinding.inflate(getLayoutInflater()));
        }
        WeatherPagerAdapter weatherPagerAdapter = new WeatherPagerAdapter(this, views);
        binding.viewPager.setAdapter(weatherPagerAdapter);
        ViewPagerHelper.bind(binding.magicIndicator, binding.viewPager);
    }

    class WeatherPagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<LayoutWeatherDetailBinding> views;

        public WeatherPagerAdapter(Context context, ArrayList<LayoutWeatherDetailBinding> views) {
            this.context = context;
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            LayoutWeatherDetailBinding binding = views.get(position);
            Weather.DataBean dataBean = weather.getData().get(position);
            binding.tvWea.setText(dataBean.getWea());
            binding.tvTemps.setText(dataBean.getTem1().replace("℃", "") + "/" + dataBean.getTem2());

            String win = dataBean.getWin().get(0).replace("[", "").replace("]", "");

            binding.tvWindDirection.setText(win + dataBean.getWin_speed());
            binding.tvHumi.setText(dataBean.getHumidity() + "");
            binding.tvLifeIndex.setText("");
            for (Weather.DataBean.IndexBean bean : dataBean.getIndex()) {
                String index = bean.getTitle() + " : " + bean.getLevel() + "," + bean.getDesc();
                binding.tvLifeIndex.append(index);
                binding.tvLifeIndex.append("\n\n");
            }
            container.addView(binding.getRoot());
            return binding.getRoot();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position).getRoot());
        }
    }
}