package com.rockzhang.red2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rockzhang.red2.utils.SPUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mServerAddress;
    private String mPlayerName;
    private EditText mServerAddressWidget;
    private EditText mPlayerNameWidget;


    private Button mStartGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mStartGame = findViewById(R.id.login_game_item);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_game_item:

                mServerAddress = mServerAddressWidget.getText().toString();
                mPlayerName = mPlayerNameWidget.getText().toString();

                SPUtils.put(this, "server_address", mServerAddress);
                SPUtils.put(this, "player_name", mPlayerName);

                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra("server_address", mServerAddress);
                intent.putExtra("player_name", mPlayerName);
                startActivity(intent);

                break;
        }
    }


}
