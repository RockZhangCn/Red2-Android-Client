package com.rockzhang.red2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rockzhang.red2.log.VLog;
import com.rockzhang.red2.model.UIPanel;
import com.rockzhang.red2.presenter.ClientGame;
import com.rockzhang.red2.presenter.IClientGamePresenter;
import com.rockzhang.red2.utils.SPUtils;
import com.rockzhang.red2.view.IGameView;

import java.util.List;


public class MainActivity extends AppCompatActivity implements IGameView, View.OnClickListener {

    private String mServerAddress;
    private String mPlayerName;
    private EditText mServerAddressWidget;
    private EditText mPlayerNameWidget;
    private IClientGamePresenter mPresenter;

    Handler mUIHandler = new Handler(Looper.getMainLooper());
    private Button mStartGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mStartGame = findViewById(R.id.start_game_item);
        mServerAddressWidget = findViewById(R.id.server_address);
        mPlayerNameWidget = findViewById(R.id.player_name);
        mStartGame.setOnClickListener(this);

        mPresenter = ClientGame.getInstance();
        mPresenter.setUIView(this);

        mServerAddress = (String) SPUtils.get(this, "server_address", "");
        mPlayerName = (String) SPUtils.get(this, "player_name", "");

        if (!TextUtils.isEmpty(mServerAddress)) {
            mServerAddressWidget.setText(mServerAddress);
        }

        if (!TextUtils.isEmpty(mPlayerName)) {
            mPlayerNameWidget.setText(mPlayerName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_game_item:

                mServerAddress = mServerAddressWidget.getText().toString();
                mPlayerName = mPlayerNameWidget.getText().toString();

                mPresenter.login(mServerAddress, mPlayerName);

                SPUtils.put(this, "server_address", mServerAddress);
                SPUtils.put(this, "player_name", mPlayerName);

                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra("server_address", mServerAddress);
                intent.putExtra("player_name", mPlayerName);
                startActivity(intent);

                break;
        }
    }

    @Override
    public List<UIPanel> getUIPanelList() {
        return null;
    }

    @Override
    public void OnLoginResult(boolean success, String message) {
        VLog.info("MainActivity OnLoginResult success " + success + " Message is " + message);
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!success) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Info");
                    builder.setMessage(message);
                    builder.setIcon(R.drawable.icon);
                    //点击对话框以外的区域是否让对话框消失
                    builder.setCancelable(true);
                    //设置正面按钮
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
//                    //设置反面按钮
//                    builder.setNegativeButton("不是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
//                    Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
//                    intent.putExtra("server_address", mServerAddress);
//                    SPUtils.put(MainActivity.this, "server_address", mServerAddress);
//                    intent.putExtra("player_name", mPlayerName);
//                    SPUtils.put(MainActivity.this, "player_name", mPlayerName);
//
//                    startActivity(intent);
                }
            }
        });

    }

}
