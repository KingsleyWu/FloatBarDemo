package com.kingsley.floatbardemo.adapter;

import com.kingsley.floatbardemo.R;
import com.kingsley.floatbardemo.bean.CityBean;

import java.util.List;

import kingsley.rvlibrary.base.RVBaseAdapter;
import kingsley.rvlibrary.base.ViewHolder;

/**
 * author : Kingsley
 * created date : on 2017/9/19 09:50
 * file change date : on 2017/9/19 09:50
 * version: 1.0
 */

public class CityAdapter extends RVBaseAdapter<CityBean> {

    public CityAdapter(int layoutResId, List<CityBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(ViewHolder holder, CityBean item) {
        holder.setText(R.id.item_city_name,item.getCityName());
        holder.setText(R.id.item_city_pinyin,item.getCityNamePinyin());
    }
}
