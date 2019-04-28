package com.lzc.changeforecast;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.lzc.myweatherforecast.MainActivity;
import com.lzc.myweatherforecast.R;

public class ChangeWeb extends Activity implements View.OnClickListener{
    private WebView webView;
    private ImageView mCitySelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change);

        webView=(WebView)findViewById(R.id.webView1);
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.weather.com.cn");

        ImageView bj=(ImageView)findViewById(R.id.title_back);
        bj.setOnClickListener(this);

        //取得启动该Activity的Intent对象
        Intent intent =getIntent();
        openUrl(intent.getStringExtra("citycode"));

    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.title_back:
                Intent intent2 = new Intent(this,MainActivity.class);
                startActivity(intent2);
                break;
            case R.id.title_city_manager:
                Intent intent = new Intent(this,Select.class);
                startActivity(intent);
                break;
        }
    }

    private void openUrl(String id){
        webView.loadUrl("http://m.weather.com.cn/mweather/"+id+".shtml");
    }
}
