package com.rockzhang.red2.app;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.rockzhang.red2.BuildConfig;

public class UpdateManager {
    private Context mContext;
    private static final String sApkMetaUrl = "http://rockzhang.com/red2/output-metadata.json";
    private static final String sApkFileUrl = "http://rockzhang.com/red2/Red2-Android-release.apk";

    public UpdateManager(Context context) {
        mContext = context;
    }


    public void update() {
        int versionCode = BuildConfig.VERSION_CODE;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(sApkFileUrl));
        //设置在什么网络情况下进行下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        //设置通知栏标题
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle("Red2升级");
        request.setDescription("apk正在下载");
        request.setAllowedOverRoaming(false);
        //设置文件存放目录
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "red2");

        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;
        //设置文件存放路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Red2-Android-release.apk");
        DownloadManager downManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        downManager.enqueue(request);
    }
}
