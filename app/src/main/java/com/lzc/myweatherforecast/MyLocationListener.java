package com.lzc.myweatherforecast;


import android.util.Log;
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
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        cityName = bdLocation.getCity();
        Log.d("Locate",cityName);
        locBtn.setText(cityName);
    }
}