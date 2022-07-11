package com.rockzhang.red2.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.rockzhang.red2.log.VLog;

//import okhttp3.OkHttpClient;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;

public class Red2App extends Application {

    private static Context mAppContext;
    private static Red2App m_singleInstance;

    public static Red2App getInstance() {
        return m_singleInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        m_singleInstance = this;
        mAppContext = m_singleInstance;
        getScreenInfo();
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int[] getScreenInfo() {
        WindowManager mWindowManager  = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        int width = metrics.widthPixels;//获取到的是px，像素，绝对像素，需要转化为dpi
        int height = metrics.heightPixels;

        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        VLog.info("density is " + density + " width height is " + width + "x" + height + " screenWidth = " + screenWidth + "x"
         + screenHeight);
        return new int[] {width, height};
    }

    @Override
    public Context getApplicationContext() {
        return mAppContext;
    }
}
