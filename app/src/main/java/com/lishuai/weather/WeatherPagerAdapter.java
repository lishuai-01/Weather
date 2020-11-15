package com.lishuai.weather;



import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class WeatherPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mCityList;

    public WeatherPagerAdapter(FragmentManager fm, List<String> channels) {
        super(fm);
        this.mCityList = channels;
    }

    public void update(List<String> cityList){
        this.mCityList = cityList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return WeatherFragment.newInstance(mCityList.get(position));
    }

    @Override
    public int getCount() {
        return mCityList != null ? mCityList.size() : 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
