package com.lishuai.weather;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;


public class CityAdapter extends BaseQuickAdapter<String, BaseViewHolder> implements DraggableModule {

    public CityAdapter(List<String> data) {
        super(R.layout.layout_item_city, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvCityName, item);
        helper.setVisible(R.id.ivDelete, getDraggableModule().isDragEnabled());
        helper.setVisible(R.id.ivSort, getDraggableModule().isDragEnabled());
    }
}
