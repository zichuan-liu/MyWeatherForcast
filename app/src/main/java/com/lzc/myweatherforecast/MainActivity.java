
package com.lzc.myweatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lzc.bean.TodayWeather;
import com.lzc.theme.ThemeChangeActivity;
import com.lzc.user_login.Login;
import com.lzc.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private String updateCityCode = "-1";

    public static int login = -1;
    private static boolean first=true;


    private static LinearLayout temp;
    //更新按钮
    private ImageView mUpdataBtn;
    //城市选择按钮
    private ImageView mCitySelect;
    //设置按钮
    private ImageView mTheme;

    //相关的控件
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg,SelectCityBtn ,LocateBtn;

    //下拉收藏
    private Spinner spinner;
    public List<String> data_list;
    private ArrayAdapter<String> arr_adapter;


    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message message)
        {
            switch (message.what)
            {
                case 1:
                    updateTodayWeather((TodayWeather) message.obj);
                    break;
                default:
                    break;
            }
        }
    };



    //网络状态检查
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeChangeActivity.sCurrentTheme);
        setContentView(R.layout.weather_info);

        SelectCityBtn = (ImageView)findViewById(R.id.title_city_manager);
        SelectCityBtn.setOnClickListener(this);

        mUpdataBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdataBtn.setOnClickListener(this);

        LocateBtn = (ImageView)findViewById(R.id.title_location);
        LocateBtn.setOnClickListener(this);

        mTheme = (ImageView)findViewById(R.id.title_share);
        mTheme.setOnClickListener(this);

        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        //设置收藏城市下拉菜单
        spinner = (Spinner) findViewById(R.id.spinner);
        data_list = new ArrayList<String>();
        SelectCity.setDataList(data_list);
        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //此处还有点击bug
                if (first) {
                    first= false;
                    Log.e("我没被点击","200000000000000000");
                } else {
                    String cityName = spinner.getItemAtPosition(position).toString();
                    String cityCode = cityName.split("-")[1];
                    Log.e("我被点击",cityName);
                    first=true;
                    queryWeatherCode(cityCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //设置背景图片
        temp = (LinearLayout)findViewById(R.id.bg_img);
        if(ThemeChangeActivity.sCurrentTheme == R.style.theme_default) {
            temp.setBackgroundResource(R.drawable.bg);
        }else if (ThemeChangeActivity.sCurrentTheme == R.style.theme_sky){
            temp.setBackgroundResource(R.drawable.biz_plugin_weather_shenzhen_bg);
        }else if (ThemeChangeActivity.sCurrentTheme == R.style.theme_grass){
            temp.setBackgroundResource(R.drawable.bg2);
        }else {
            temp.setBackgroundResource(R.drawable.bg);
        }

        //起始页面
        updateCityCode = getIntent().getStringExtra("citycode");
        if(updateCityCode!="-1"&& updateCityCode != null)
        {
            queryWeatherCode(updateCityCode);
        }else
        {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    "CityCodePreference",Activity.MODE_PRIVATE);
            String defaultCityCode = sharedPreferences.getString("citycode","");
            if(defaultCityCode!=null){
                queryWeatherCode("101200101");
            }

        }
        if(login==-1){
            login = 1;
            Intent intent = new Intent(this,Login.class) ;    //切换Login Activity至User Activity
            startActivity(intent);
        }

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！", Toast.LENGTH_LONG).show();
        }

        initView();
    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.title_update_btn)
        {
            SharedPreferences mySharePre = getSharedPreferences("CityCodePreference",Activity.MODE_PRIVATE);
            String sharecode = mySharePre.getString("citycode","");
            if(!sharecode.equals(""))
            {
                Log.d("sharecode",sharecode);
                queryWeatherCode(sharecode);
            }else {
                queryWeatherCode("101200101");//101200101
            }
        }
        if(v.getId()==R.id.title_city_manager)
        {
            Log.d("click","title_city_manager");
            Intent intent = new Intent(this,SelectCity.class);
            startActivity(intent);
        }
        if(v.getId()== R.id.title_location){
            //Locate mLocation = new Locate(this);
            //mLocation.startLocation();
            Log.d("click","title_city_locate");
            Intent intent = new Intent(this,Locate.class);
            startActivity(intent);
        }
        if (v.getId()==R.id.title_share){
            Log.d("click","title_share");
            Intent intent = new Intent(this,ThemeChangeActivity.class) ;
            startActivity(intent);
        }

    }


    //返回主界面时，传递城市代码数据
    //修改更新按钮的单击事件处理程序，并编写onActivityResult函数用于接收返回的数据。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode= data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为"+newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    //获取网络数据
    private void queryWeatherCode(String cityCode)  {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    //解析网络数据
    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }

                        break;


                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    //初始化控键内容
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    //利用TodayWeather对象更新UI中的控件

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow()+"~"+todayWeather.getHigh());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());

        //更新图片
        if (todayWeather.getPm25()!=null){
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            if(pm25 <= 50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }else if(pm25 >= 51 && pm25 <= 100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }else if(pm25 >= 101 && pm25 <= 150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }else if(pm25 >= 151 && pm25 <= 200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }else if(pm25 >= 201 && pm25 <= 300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
        }
        switch (todayWeather.getType()){
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "雨加雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            default:
                //没有加载的图片
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_default);
                break;
        }

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

    }

}