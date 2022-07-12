package com.rockzhang.red2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.rockzhang.red2.log.VLog;
import com.rockzhang.red2.model.UIPanel;
import com.rockzhang.red2.presenter.ClientGame;
import com.rockzhang.red2.presenter.IClientGamePresenter;
import com.rockzhang.red2.view.IGameView;
import com.rockzhang.red2.view.PokerView;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements IGameView, View.OnClickListener {
    private View mContentView;
    private String mServerAddress;
    private String mPlayerName;
    private IClientGamePresenter mPresenter;

    private PokerView mBottomPokerView;

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置屏幕为横屏, 设置后会锁定方向
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置屏幕为横屏, 设置后会锁定方向
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.full_screen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mBottomPokerView = (PokerView) findViewById(R.id.bottom_poker_view);
        mContentView = findViewById(R.id.content_view);
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().hide(
            WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        mPresenter = ClientGame.getInstance();
        mPresenter.setUIView(this);

        Intent startIntent = getIntent();
        mPlayerName = startIntent.getStringExtra("player_name");
        mServerAddress = startIntent.getStringExtra("server_address");

        VLog.info("PlayerName is " + mPlayerName + " Server address is " + mServerAddress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game_item:

                break;
        }
    }

    @Override
    public List<UIPanel> getUIPanelList() {
        return null;
    }

    @Override
    public void OnLoginResult(boolean success, String message) {

    }
}