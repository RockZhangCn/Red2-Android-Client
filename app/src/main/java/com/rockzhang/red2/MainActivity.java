package com.rockzhang.red2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Handler mainHandler = new Handler(Looper.getMainLooper());
    private Toolbar mToolbar;
    private Button mStartGame;
    private Button mStartLayout;
    private Button mOtherFunc;
    private Button mTestFunc;

    private ProgressDialog progressDialog = null;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
//        mToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);

        mStartGame = findViewById(R.id.start_game_item);
        mStartLayout = findViewById(R.id.layout_chess);
        mOtherFunc = findViewById(R.id.other_func);
        mTestFunc = findViewById(R.id.test_func);

        mStartGame.setOnClickListener(this);
        mStartLayout.setOnClickListener(this);
        mOtherFunc.setOnClickListener(this);
        mTestFunc.setOnClickListener(this);

        checkUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jun_qi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.quit_item) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInProgress(boolean show) {
        //display a dialog in progress.
        if (show) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("正在登录");
            progressDialog.setMessage("登录中，请稍等。。。");
            progressDialog.setCancelable(true);
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_game_item:
                showInProgress(true);
//                presenter.loginDefaultRoom();
                break;

            case R.id.layout_chess:
//                startActivity(new Intent(MainActivity.this, LayoutActivity.class));
                break;
            case R.id.other_func:
//                startActivity(new Intent(MainActivity.this, JunQiActivity.class));
                break;

            case R.id.test_func:
                /*
                 DownloadManager manager = DownloadManager.getInstance(MainActivity.this);
                 manager.setApkName("appupdate.apk")
                         .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
                         .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                         .setSmallIcon(R.mipmap.ic_launcher)
                         //可设置，可不设置
                         //.setConfiguration(configuration)
                         .download();
                */

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("正在登录");
                progressDialog.setMessage("登录中，请稍等。。。");
                progressDialog.setCancelable(true);
                progressDialog.show();
                break;
        }
    }

    private void checkUpdate() {
        String versionName;
        int versionCode;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;


        } catch (Exception e) {
            versionName = "1.0";
            versionCode = 1;
        }

//        Request request = new Request.Builder()
//                .url("http://v.rockzhang.com/fourwar/app/config")
//                .build();

//        FourWarApp.getInstance().getNetworkClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        showUpdateDialog();
//                    }
//                });
//
//            }
//        });
    }

    private void showUpdateDialog() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle("信息通告");
        normalDialog.setMessage("检测到新版本需要升级");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        normalDialog.show();
    }

}
