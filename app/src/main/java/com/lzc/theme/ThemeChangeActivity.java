package com.lzc.theme;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.lzc.changeforecast.ChangeWeb;
import com.lzc.myweatherforecast.MainActivity;
import com.lzc.myweatherforecast.R;
import com.lzc.user_login.Login;

public class ThemeChangeActivity extends Activity implements View.OnClickListener {

    public static int sCurrentTheme = R.style.theme_sky;
    //退出
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 这句必须放在 setContentView() 之前, 否则不生效.
        // 一般的做法是把这句话放在你的 BaseActivity 里面.
        setTheme(sCurrentTheme);

        setContentView(R.layout.activity_change_theme);

        mReturnButton = (Button)findViewById(R.id.returnback);
        mReturnButton.setOnClickListener(this);
        findViewById(R.id.btn_switch_theme_default).setOnClickListener(this);
        findViewById(R.id.btn_switch_theme_sky).setOnClickListener(this);
        findViewById(R.id.btn_switch_theme_grass).setOnClickListener(this);
        findViewById(R.id.btn_change).setOnClickListener(this);

        // 演示如何用代码获取 attr 定义的主题相关的颜色
        View primaryColorPanel = findViewById(R.id.primary_color_panel);
        primaryColorPanel.setBackgroundColor(getCurrentPrimaryColor());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_switch_theme_sky:
                changeTheme(R.style.theme_sky);
                break;
            case R.id.btn_switch_theme_grass:
                changeTheme(R.style.theme_grass);
                break;
            case R.id.btn_switch_theme_default:
                changeTheme(R.style.theme_default);
                break;
        }

        if (view.getId()==R.id.returnback){
            //setContentView(R.layout.login);
            MainActivity.login=-1;
            Intent intent = new Intent(this,Login.class) ;
            startActivity(intent);
        }else if (view.getId()==R.id.btn_change){
            Intent intent = new Intent(this,ChangeWeb.class) ;
            startActivity(intent);
        }else {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void changeTheme(int theme) {
        // 改变主题时应该把当前主题设置保存进 SharedPreferences 里去.
        // 比如给这三个主题编号 101, 102, 103, 然后保存该编号, 供下次启动时设置为对应主题.
        // 这里省略了这部分逻辑, 只留主题相关逻辑.
        sCurrentTheme = theme;
        
        // 调用 Activity.recreate() 方法即可从 Activity.onCreate() 开始重新加载界面.
        // 该方法不会启动界面过场动画, 但重启时会有一下闪烁.
        recreate();
    }

    // 直接获取主题的主色颜色值
    public int getCurrentPrimaryColor() {
        return getColorByAttributeId(R.attr.color_primary);
    }

    // 使用代码获取主题属性颜色值的方法
    @ColorInt
    private int getColorByAttributeId(@AttrRes int attrIdForColor){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(attrIdForColor, typedValue, true);
        return typedValue.data;
    }
}