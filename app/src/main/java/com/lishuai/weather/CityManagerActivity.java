package com.lishuai.weather;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.lishuai.weather.databinding.ActivityCityManagerBinding;
import com.lishuai.weather.util.CityCache;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class CityManagerActivity extends AppCompatActivity {

    private ActivityCityManagerBinding binding;
    private List<String> cityList = new ArrayList<>();
    private CityAdapter mCityAdapter;
    private Boolean isEdit = false;
    private JDCityPicker mCityPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCityManagerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        cityList = CityCache.getCityList(this);

        initRecyclerView();
        initListener();
    }

    private void initRecyclerView() {
        mCityAdapter = new CityAdapter(cityList);
        mCityAdapter.addChildClickViewIds(R.id.ivDelete, R.id.ivSort);
        mCityAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.ivDelete) {
                    cityList.remove(position);
                    CityCache.saveCitys(CityManagerActivity.this, cityList);
                    mCityAdapter.notifyDataSetChanged();
                }
            }
        });

        OnItemDragListener listener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                CityCache.saveCitys(CityManagerActivity.this, cityList);
            }
        };
        mCityAdapter.getDraggableModule().setOnItemDragListener(listener);
        binding.rvCity.setAdapter(mCityAdapter);
    }


    private void initListener() {
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCityPicker == null) {
                    initPicker();
                }

                mCityPicker.showCityPicker();
            }
        });

        binding.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = !isEdit;

                if (isEdit) {
                    binding.btDone.setVisibility(View.VISIBLE);
                    mCityAdapter.getDraggableModule().setDragEnabled(true);
                } else {
                    binding.btDone.setVisibility(View.GONE);
                }

                mCityAdapter.notifyDataSetChanged();
            }
        });

        binding.btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
                binding.btDone.setVisibility(View.GONE);
                mCityAdapter.getDraggableModule().setDragEnabled(false);
                mCityAdapter.notifyDataSetChanged();
            }
        });
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

                CityCache.addCity(CityManagerActivity.this, city.getName());
                cityList.add(city.getName());
                mCityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {

            }
        });
    }
}