package com.rockzhang.red2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rockzhang.red2.app.UpdateManager;
import com.rockzhang.red2.utils.SPUtils;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mServerAddress;
    private String mPlayerName;
    private EditText mServerAddressWidget;
    private EditText mPlayerNameWidget;

    private UpdateManager mUpdateManager;
    private Button mStartGame;
    private Button mUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mStartGame = findViewById(R.id.login_game_item);
        mUpdateButton = findViewById(R.id.update_button);
        mUpdateButton.setOnClickListener(this);

        mServerAddressWidget = findViewById(R.id.server_address);
        mPlayerNameWidget = findViewById(R.id.player_name);
        mStartGame.setOnClickListener(this);

        mServerAddress = (String) SPUtils.get(this, "server_address", "");
        mPlayerName = (String) SPUtils.get(this, "player_name", "");

        if (!TextUtils.isEmpty(mServerAddress)) {
            mServerAddressWidget.setText(mServerAddress);
        }

        if (!TextUtils.isEmpty(mPlayerName)) {
            mPlayerNameWidget.setText(mPlayerName);
        }

        mUpdateManager = new UpdateManager(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_game_item:

                mServerAddress = mServerAddressWidget.getText().toString();
                mPlayerName = mPlayerNameWidget.getText().toString();

                if (TextUtils.isEmpty(mPlayerName) || TextUtils.isEmpty(mServerAddress)) {
                    Toast.makeText(MainActivity.this, "请填写用户名或地址", Toast.LENGTH_LONG).show();
                    return;
                }

                SPUtils.put(this, "server_address", mServerAddress);
                SPUtils.put(this, "player_name", mPlayerName);

                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra("server_address", mServerAddress);
                intent.putExtra("player_name", mPlayerName);
                startActivity(intent);

                break;
            case R.id.update_button:
                mUpdateManager.update();
                break;
        }
    }

    //https://blog.51cto.com/u_5018054/3402655 更新
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
//
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
