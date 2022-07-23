package com.rockzhang.red2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
import com.rockzhang.red2.role.CardMode;
import com.rockzhang.red2.role.PlayerStatus;
import com.rockzhang.red2.view.IGameView;
import com.rockzhang.red2.view.PokerView;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements IGameView, View.OnClickListener {
    Handler mUIHandler = new Handler(Looper.getMainLooper());
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
    private TextView mBottomName;
    private List<UIPanel> mUIPanelList = new ArrayList<>(4);
    private int mPlayerStatus = PlayerStatus.Offline.getValue();

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置屏幕为横屏, 设置后会锁定方向
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VLog.info("FullscreenActivity onCreate run");
        Intent startIntent = getIntent();
        mPlayerName = startIntent.getStringExtra("player_name");
        mServerAddress = startIntent.getStringExtra("server_address");

        mPresenter = new ClientGame(this);
        mPresenter.login(mServerAddress, mPlayerName);

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

        // Left Panel
        TextView leftMessage = (TextView) findViewById(R.id.left_user_message);
        TextView leftName = (TextView) findViewById(R.id.left_user_name);
        ImageView leftTimer = (ImageView) findViewById(R.id.left_user_timer_img);
        TextView leftPokerNum = (TextView) findViewById(R.id.left_user_poker_num);
        ImageView leftPokerImg = (ImageView) findViewById(R.id.left_user_poker_img);

        UIPanel leftPanel = new UIPanel() {
            @Override
            public void showName(String name) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        leftName.setText(name);
                    }
                });

            }

            @Override
            public void showMessage(String message, boolean shouldDialog) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        leftMessage.setText(message);
                    }
                });
            }

            @Override
            public void showPokers(List<Integer> cards) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        leftPokerImg.setVisibility(cards.size() > 0 ? View.VISIBLE : View.INVISIBLE);
                        if (cards.size() <= 5 && cards.size() > 0) {
                            leftPokerNum.setText(String.valueOf(cards.size()));
                        } else {
                            leftPokerNum.setText("");
                        }
                    }
                });
            }

            @Override
            public void showTimer(Boolean show) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        leftTimer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            }
        };


        // Top Panel
        TextView topName = (TextView) findViewById(R.id.top_user_name);
        TextView topMessage = (TextView) findViewById(R.id.top_user_message);
        ImageView topTimer = (ImageView) findViewById(R.id.top_user_timer_img);
        TextView topPokerNum = (TextView) findViewById(R.id.top_user_poker_num);
        ImageView topPokerImg = (ImageView) findViewById(R.id.top_user_poker_img);

        UIPanel topPanel = new UIPanel() {
            @Override
            public void showName(String name) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        topName.setText(name);
                    }
                });
            }

            @Override
            public void showMessage(String message, boolean shouldDialog) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        topMessage.setText(message);
                    }
                });
            }

            @Override
            public void showPokers(List<Integer> cards) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        topPokerImg.setVisibility(cards.size() > 0 ? View.VISIBLE : View.INVISIBLE);
                        if (cards.size() <= 5 && cards.size() > 0) {
                            topPokerNum.setText(String.valueOf(cards.size()));
                        } else {
                            topPokerNum.setText("");
                        }
                    }
                });
            }

            @Override
            public void showTimer(Boolean show) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        topTimer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            }
        };
        // Right Panel
        TextView rightName = (TextView) findViewById(R.id.right_user_name);
        TextView rightMessage = (TextView) findViewById(R.id.right_user_message);
        ImageView rightTimer = (ImageView) findViewById(R.id.right_user_timer_img);
        TextView rightPokerNum = (TextView) findViewById(R.id.right_user_poker_num);
        ImageView rightPokerImg = (ImageView) findViewById(R.id.right_user_poker_img);
        UIPanel rightPanel = new UIPanel() {
            @Override
            public void showName(String name) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rightName.setText(name);
                    }
                });
            }

            @Override
            public void showMessage(String message, boolean shouldDialog) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rightMessage.setText(message);
                    }
                });
            }

            @Override
            public void showPokers(List<Integer> cards) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rightPokerImg.setVisibility(cards.size() > 0 ? View.VISIBLE : View.INVISIBLE);
                        if (cards.size() <= 5 && cards.size() > 0) {
                            rightPokerNum.setText(String.valueOf(cards.size()));
                        } else {
                            rightPokerNum.setText("");
                        }
                    }
                });
            }

            @Override
            public void showTimer(Boolean show) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rightTimer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            }
        };
        // Bottom Panel
        mBottomName = (TextView) findViewById(R.id.bottom_name);
        mBottomPokerView = (PokerView) findViewById(R.id.bottom_poker_view);
        mBottomMessage = (TextView) findViewById(R.id.bottom_message);

        UIPanel bottomPanel = new UIPanel() {
            @Override
            public void showName(String name) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBottomName.setText(name);
                    }
                });
            }

            @Override
            public void showMessage(String message, boolean shouldDialog) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBottomMessage.setText(message);
                        if (shouldDialog) {
                            showNotifyDialog("Info", message, false);
                        }
                    }
                });
            }

            @Override
            public void showPokers(List<Integer> cards) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBottomPokerView.setDisplayCards(cards);
                    }
                });
            }

            @Override
            public void showTimer(Boolean show) {
                VLog.info("It's our turn to do action");
            }
        };

        mUIPanelList.add(bottomPanel);
        mUIPanelList.add(rightPanel);
        mUIPanelList.add(topPanel);
        mUIPanelList.add(leftPanel);

        // Center Panel.
        mCenterPokerView = (PokerView) findViewById(R.id.center_poker_view);

        UIPanel centerPanel = new UIPanel() {
            @Override
            public void showName(String name) {

            }

            @Override
            public void showMessage(String message, boolean shouldDialog) {

            }

            @Override
            public void showPokers(List<Integer> cards) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCenterPokerView.setDisplayCards(cards);
                    }
                });
            }

            @Override
            public void showTimer(Boolean show) {

            }
        };

        mUIPanelList.add(centerPanel);

        // Hide all.
        for (int i = 1; i < mUIPanelList.size(); i++) {
            mUIPanelList.get(i).showMessage("", false);
            mUIPanelList.get(i).showName("");
            mUIPanelList.get(i).showPokers(new ArrayList<Integer>());
            mUIPanelList.get(i).showTimer(false);
        }

        // Bottom Buttons.
        mStartGameButton = (Button) findViewById(R.id.start_game_button);
        mDoActionButton = (Button) findViewById(R.id.do_action_button);
        mDoNegativeButton = (Button) findViewById(R.id.do_negative_button);

        mStartGameButton.setOnClickListener(this);
        mDoActionButton.setOnClickListener(this);
        mDoNegativeButton.setOnClickListener(this);


        mStartGameButton.setEnabled(false);
        mDoActionButton.setEnabled(false);
        mDoNegativeButton.setEnabled(false);
        VLog.info("PlayerName is " + mPlayerName + " Server address is " + mServerAddress);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果点击了返回键
            //声明并初始化弹出对象
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("是否退出游戏？");
            builder.setIcon(R.drawable.icon);
            //点击对话框以外的区域是否让对话框消失
            builder.setCancelable(true);
            //设置确认按钮
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();//退出程序
                }
            });
            //设置取消按钮
            builder.setPositiveButton("取消", null);
            //显示弹框
            builder.show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnPlayerStatusChanged(final int playStatus, boolean isActive) {
        mPlayerStatus = playStatus;
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (playStatus == PlayerStatus.Logined.getValue()) {
                    mStartGameButton.setEnabled(true);
                } else if (playStatus == PlayerStatus.Started.getValue()) {
                    mStartGameButton.setEnabled(false);
                } else if (playStatus == PlayerStatus.SingleOne.getValue()) {
                    mStartGameButton.setEnabled(false);
                    mDoActionButton.setText("抢2");
                    mDoActionButton.setEnabled(isActive);
                    mDoNegativeButton.setEnabled(isActive);
                    mDoNegativeButton.setText("不抢");
                } else if (playStatus == PlayerStatus.NoTake.getValue()) {
                    mDoActionButton.setEnabled(isActive);
                    mDoNegativeButton.setEnabled(isActive);
                    VLog.warning("FullscreenActivity received NoTake State");
                } else if (playStatus == PlayerStatus.Share2.getValue()) {
                    mDoActionButton.setEnabled(true);
                    mDoActionButton.setText("分2");
                    mDoNegativeButton.setEnabled(true);
                    mDoNegativeButton.setText("偷鸡");
                } else if (playStatus == PlayerStatus.NoShare.getValue()) {
                    VLog.warning("FullscreenActivity received NoShare State");
                } else if (playStatus == PlayerStatus.Handout.getValue()) {
                    mDoActionButton.setEnabled(isActive);
                    mDoActionButton.setText("出牌");


                    mDoNegativeButton.setText("过牌");
                    if (mPresenter.getWeSeatPos() == mPresenter.centerPokerIssuer()
                    || mPresenter.centerPokerIssuer() == -1) {
                        mDoNegativeButton.setEnabled(false);
                    } else  {
                        mDoNegativeButton.setEnabled(isActive);
                    }

                } else if (playStatus == PlayerStatus.RunOut.getValue()) {
                    mDoActionButton.setEnabled(false);
                    mDoNegativeButton.setEnabled(false);
                } else if (playStatus == PlayerStatus.GameOver.getValue()) {
                    mDoActionButton.setEnabled(false);
                    mDoNegativeButton.setEnabled(false);
                    mStartGameButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        VLog.info("FullscreenActivity onDestroy run in");
        mPresenter.logout(mPlayerName);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game_button:
                mPresenter.newPlayerStatus(PlayerStatus.Started, new ArrayList<>());
                break;
            case R.id.do_action_button:
                if (mPlayerStatus == PlayerStatus.SingleOne.getValue()) {
                    mPresenter.newPlayerStatus(PlayerStatus.SingleOne, new ArrayList<Integer>());
                } else if (mPlayerStatus == PlayerStatus.Share2.getValue()) {
                    mPresenter.newPlayerStatus(PlayerStatus.Share2, new ArrayList<Integer>());
                } else if (mPlayerStatus == PlayerStatus.Handout.getValue()) {
                    mPresenter.newPlayerStatus(PlayerStatus.Handout, mBottomPokerView.getSelectedCards());
                } else {
                    VLog.warning("We click the Active Button in wrong status " + mPlayerStatus);
                }
                break;

            case R.id.do_negative_button:
                if (mPlayerStatus == PlayerStatus.SingleOne.getValue()) {
                    mPresenter.newPlayerStatus(PlayerStatus.NoTake, new ArrayList<Integer>());
                } else if (mPlayerStatus == PlayerStatus.Share2.getValue()) {
                    mPresenter.newPlayerStatus(PlayerStatus.NoShare, new ArrayList<>());
                } else if (mPlayerStatus == PlayerStatus.Handout.getValue()) {
                    mPresenter.newPlayerStatus(PlayerStatus.Handout, new ArrayList<Integer>());
                } else {
                    VLog.warning("We click the Negative Button in wrong status " + mPlayerStatus);
                }

                break;

        }
    }

    @Override
    public void showNotifyDialog(String title, String message, boolean finishActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.icon);
        //点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (finishActivity) {
                    finish();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void OnLoginResult(boolean success, String message) {
        VLog.info("FullscreenActivity OnLoginResult result: " + success + " Message is " + message);
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!success) {
                    showNotifyDialog("Error", message, true);
                } else {
                    mBottomMessage.setText(message);
                    mBottomName.setText(mPlayerName);
                    showNotifyDialog("Info", String.format("登陆成功，座位号" + message), false);
                }
            }
        });

    }

    @Override
    public List<UIPanel> getUIPanelList() {
        return mUIPanelList;
    }

}