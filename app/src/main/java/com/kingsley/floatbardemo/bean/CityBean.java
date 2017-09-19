package com.kingsley.floatbardemo.bean;

import android.support.annotation.NonNull;

import com.kingsley.floatbardemo.util.PYUtils;


public class CityBean implements Comparable<CityBean> {
    public static final String DEFAULT_STR = "#";

    private String mInitial;
    private String mCityName;
    private String mCityNamePinyin;
    private String mAbbreviation;
    private String mCityId;

    public CityBean(String cityName, String cityNamePinyin, String cityId) {
        this.mCityName = cityName;
        this.mCityNamePinyin = cityNamePinyin;
        mAbbreviation = PYUtils.getAbbreviation(cityName);
        mInitial = DEFAULT_STR;
        this.mCityId = cityId;
    }

    public String getAbbreviation() {
        return mAbbreviation;
    }

    public String getInitial() {
        return mInitial;
    }

    public void setInitial(String mInitial) {
        this.mInitial = mInitial;
    }

    public String getCityName() {
        return mCityName;
    }


    public String getCityNamePinyin() {
        return mCityNamePinyin;
    }

    public String getCityId() {
        return mCityId;
    }


    @Override
    public int compareTo(@NonNull CityBean city) {

        if (mCityName.equals(city.getCityName())) {
            return 0;
        }
        return getInitial().compareTo(city.getInitial());
    }

    @Override
    public String toString() {
        return "CityBean{" +
                "mInitial='" + mInitial + '\'' +
                ", mCityName='" + mCityName + '\'' +
                ", mCityNamePinyin='" + mCityNamePinyin + '\'' +
                ", mCityId='" + mCityId + '\'' +
                '}';
    }
}
