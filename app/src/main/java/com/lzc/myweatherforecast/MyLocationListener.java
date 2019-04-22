package com.lzc.myweatherforecast;


import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
public class MyLocationListener implements BDLocationListener {
    Button locBtn;
    MyLocationListener(Button locBtn)
    {
        this.locBtn = locBtn;
    }
    String cityName;

    public MyLocationListener() {

    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        cityName = bdLocation.getCity();
        locBtn.setText(cityName);
    }
}
