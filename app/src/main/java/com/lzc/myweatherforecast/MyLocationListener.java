package com.lzc.myweatherforecast;


import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

public class MyLocationListener extends BDAbstractLocationListener {
    Button locBtn;
    MyLocationListener(Button locBtn)
    {
        this.locBtn = locBtn;
    }
    String cityName;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        cityName = bdLocation.getCity();
        String addr = bdLocation.getAddrStr();    //获取详细地址信息
        String country = bdLocation.getCountry();    //获取国家
        String province = bdLocation.getProvince();    //获取省份
        String city = bdLocation.getCity();    //获取城市
        String district = bdLocation.getDistrict();    //获取区县
        String street = bdLocation.getStreet();    //获取街道信息

        locBtn.setText(cityName);
    }
}
