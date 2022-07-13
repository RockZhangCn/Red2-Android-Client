package com.rockzhang.red2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockzhang.red2.log.VLog;
import com.rockzhang.red2.model.UIPanel;
import com.rockzhang.red2.presenter.ClientGame;
import com.rockzhang.red2.presenter.IClientGamePresenter;
import com.rockzhang.red2.view.IGameView;
import com.rockzhang.red2.view.PokerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
    private PokerView mCenterPokerView;

    private Button mStartGameButton;
    private Button mDoActionButton;
    private Button mDoNegativeButton;
    private TextView mBottomMessage;

    private List<UIPanel> mUIPanelList = new ArrayList<>(4);

    Handler mUIHandler = new Handler(Looper.getMainLooper());

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

        mBottomPokerView = (PokerView) findViewById(R.id.bottom_poker_view);
        mBottomMessage = (TextView) findViewById(R.id.bottom_message);

        // Left Pannel
        TextView leftMessage = (TextView) findViewById(R.id.left_user_message);
        TextView leftName = (TextView) findViewById(R.id.left_user_name);
        ImageView leftTimer = (ImageView) findViewById(R.id.left_user_timer_img);

        // Top Pannel
        TextView topName = (TextView) findViewById(R.id.top_user_name);
        TextView topMessage = (TextView) findViewById(R.id.top_user_message);
        ImageView topTimer = (ImageView) findViewById(R.id.top_user_timer_img);

        // Right Pannel
        TextView rigthName = (TextView) findViewById(R.id.right_user_name);
        TextView rightMessage = (TextView) findViewById(R.id.right_user_message);
        ImageView rightTimer = (ImageView) findViewById(R.id.right_user_timer_img);

        // Bottom Pannel

        mCenterPokerView = (PokerView) findViewById(R.id.center_poker_view);

        mStartGameButton = (Button) findViewById(R.id.start_game_item);
        mDoActionButton = (Button) findViewById(R.id.do_action_button);
        mDoNegativeButton = (Button) findViewById(R.id.do_negative_button);

        Intent startIntent = getIntent();
        mPlayerName = startIntent.getStringExtra("player_name");
        mServerAddress = startIntent.getStringExtra("server_address");

        mPresenter = ClientGame.getInstance();
        mPresenter.setUIView(this);
        mPresenter.login(mServerAddress, mPlayerName);

        UIPanel bottom = new UIPanel() {
            @Override
            public void showName(String name) {

            }

            @Override
            public void showMessage(String message, boolean shouldDialog) {

            }

            @Override
            public void showPokers(List<Integer> cards) {

            }

            @Override
            public void showTimer(Boolean show) {

            }
        }

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
    public void OnLoginResult(boolean success, String message) {
        VLog.info("FullscreenActivity OnLoginResult success " + success + " Message is " + message);
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!success) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this);
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
                    mBottomMessage.setText(message);
                }
            }
        });

    }

    @Override
    public List<UIPanel> getUIPanelList() {
        return mUIPanelList;
    }

}