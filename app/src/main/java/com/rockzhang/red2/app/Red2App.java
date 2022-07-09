package com.rockzhang.red2.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

//import okhttp3.OkHttpClient;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;

public class Red2App extends Application {

    private static Context mAppContext;
    private static Red2App m_singleInstance;
//    private static OkHttpClient networkClient;
    Handler mainHandler = new Handler(Looper.getMainLooper());
//    private Retrofit mRetrofit;

    public static Red2App getInstance() {
        return m_singleInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        m_singleInstance = this;
        mAppContext = m_singleInstance;

//        networkClient = new OkHttpClient();
//        mRetrofit = new Retrofit.Builder()
//                .baseUrl("http://rockzhang.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
    }

//    public OkHttpClient getNetworkClient() {
//        return networkClient;
//    }
//
//    public Retrofit getAPIClient() {
//        return mRetrofit;
//    }

    @Override
    public Context getApplicationContext() {
        return mAppContext;
    }
}
