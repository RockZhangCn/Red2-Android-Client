package com.rockzhang.red2.presenter;

import android.text.TextUtils;

import com.rockzhang.red2.log.VLog;
import com.rockzhang.red2.model.UIPanel;
import com.rockzhang.red2.network.NetworkHandler;
import com.rockzhang.red2.network.NetworkThread;
import com.rockzhang.red2.role.CardMode;
import com.rockzhang.red2.role.PlayerStatus;
import com.rockzhang.red2.utils.GameSound;
import com.rockzhang.red2.utils.SoundType;
import com.rockzhang.red2.view.IGameView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientGame implements IClientGamePresenter {

    private NetworkHandler mNetworkHandler;
    private IGameView mUIView;
    private String mPlayerName;
    private int mWeSeatPos;
    private int mPlayerStatus;
    private int mActivePos;
    private List<Integer> mCenterDispatchPokers = new ArrayList<>(16);
    private List<Integer> mOwnedPokers = new ArrayList<>(32);
    private int mCenterPokerIssuer = -1;

    private void clearAllInfo() {
        for (int layoutIndex = 0; layoutIndex < 4; layoutIndex++) {
            mUIView.getUIPanelList().get(layoutIndex).showName("");
            mUIView.getUIPanelList().get(layoutIndex).showMessage("", false);
            mUIView.getUIPanelList().get(layoutIndex).showTimer(false);
            mUIView.getUIPanelList().get(layoutIndex).showPokers(new ArrayList<Integer>());
        }

    }
    NetworkHandler.MessageCallback messageCallback = new NetworkHandler.MessageCallback() {
        @Override
        public void OnReceivedMessage(JSONObject obj) {
            // VLog.info("ClientGame OnReceivedMessage " + obj.toString());
            try {
                if (!obj.has("action")) {
                    errorHandler("Message format error", false);
                    return;
                }

                String action = (String) obj.get("action");
                if (TextUtils.isEmpty(action)) {
                    errorHandler("Message format error", false);
                    return;
                }

                if (action.equals("pong")) {
                    return;
                }

                VLog.info("ClientGame OnReceivedMessage " + obj.toString());
                if (action.equalsIgnoreCase("network_issue")) {
                    VLog.warning("We get error network_issue, will show Dialog");
                    mUIView.OnLoginResult(false, obj.getString("message"));
                } else if (action.equalsIgnoreCase("status_broadcast")) {
                    mActivePos = obj.getInt("active_pos");
                    if (obj.has("status_all")) {
                        JSONArray list = obj.getJSONArray("status_all");

                        if (mWeSeatPos == -1) {
                            mWeSeatPos = obj.getInt("recover_pos");
                        }

                        clearAllInfo();

                        // login handler
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject singleUser = list.getJSONObject(i);
                            String playerName = singleUser.getString("player_name");
                            int seatPos = singleUser.getInt("position");
                            int playerStatus = singleUser.getInt("status");

                            if (playerName.equals(mPlayerName) && (seatPos == obj.getInt("notify_pos"))) {
                                if (playerStatus == PlayerStatus.Logined.getValue()) {
                                    mUIView.OnLoginResult(true, String.valueOf(seatPos));
                                    mWeSeatPos = seatPos;
                                }

                                setPlayerStatus(playerStatus);
                                mUIView.OnPlayerStatusChanged(mPlayerStatus, mWeSeatPos == mActivePos);
                            }
                        }

                        JSONArray centerJsonArray = obj.getJSONArray("center_pokers");
                        mCenterDispatchPokers.clear();
                        mCenterPokerIssuer = obj.getInt("center_poker_issuer");
                        for (int j = 0; j < centerJsonArray.length(); j++) {
                            mCenterDispatchPokers.add((Integer) centerJsonArray.get(j));
                        }

                        mUIView.getUIPanelList().get(4).showPokers(mCenterDispatchPokers);

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject singleUser = list.getJSONObject(i);
                            String playerName = singleUser.getString("player_name");
                            int seatPos = singleUser.getInt("position");
                            int playerStatus = singleUser.getInt("status");
                            String message = singleUser.getString("message");

                            int layoutIndex = (seatPos + 4 - mWeSeatPos) % 4;
                            // SetTimer Icon.
                            if (mActivePos != -1) {
                                mUIView.getUIPanelList().get(layoutIndex).showTimer(seatPos == mActivePos);
                                GameSound.getInstance().playSound(SoundType.SOUND_GAME_CLOCK, 1);
                            }

                            if (playerStatus == PlayerStatus.Logined.getValue() || playerStatus == PlayerStatus.Started.getValue()) {
                                mUIView.getUIPanelList().get(layoutIndex).showName(playerName);
                                mUIView.getUIPanelList().get(layoutIndex).showMessage(message, false);
                            } else if (playerStatus == PlayerStatus.SingleOne.getValue() ||
                                    playerStatus == PlayerStatus.NoTake.getValue() ||

                                    playerStatus == PlayerStatus.Share2.getValue() ||
                                    playerStatus == PlayerStatus.NoShare.getValue() ||

                                    playerStatus == PlayerStatus.Handout.getValue() ||
                                    playerStatus == PlayerStatus.RunOut.getValue()
                            ) {
                                JSONArray jsonArray = singleUser.getJSONArray("pokers");
                                List<Integer> dispatchPokers = new ArrayList<>(jsonArray.length());
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    dispatchPokers.add((Integer) jsonArray.get(j));
                                }

                                if (-1 < seatPos && seatPos < 4) {
                                    mUIView.getUIPanelList().get(layoutIndex).showName(playerName);
                                    mUIView.getUIPanelList().get(layoutIndex).showMessage(message, false);
                                    mUIView.getUIPanelList().get(layoutIndex).showPokers(dispatchPokers);
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                VLog.error("ClientGame meet exception " + e.toString());
                mUIView.OnLoginResult(false, e.toString());
            }
        }
    };

    public void setPlayerStatus(int status) {
        mPlayerStatus = status;
        if (mPlayerStatus == PlayerStatus.SingleOne.getValue()) {
            GameSound.getInstance().playSound(SoundType.SOUND_GAME_START);
        }
    }

    public ClientGame(IGameView view) {
        mUIView = view;
        mWeSeatPos = -1;
        setPlayerStatus(PlayerStatus.Offline.getValue());
    }

    public List<Integer> getOwnedPokers() {
        return mOwnedPokers;
    }

    public void setOwnedPokers(List<Integer> mOwnedPokers) {
        this.mOwnedPokers = mOwnedPokers;
    }

    private UIPanel currentUser() {
        return mUIView.getUIPanelList().get(0);
    }

    private void errorHandler(String message, boolean shouldDialog) {
        VLog.error("ClientGame errorHandler message " + message);
        currentUser().showMessage(message, shouldDialog);
    }

    @Override
    public void logout(String loginName) {
        if (mNetworkHandler != null) {
            mNetworkHandler.logout();
        }
    }

    @Override
    public void login(String wsAddress, String loginName) {
        VLog.info("ClientGame Login create new NetworkHandler");
        mPlayerName = loginName;
        mNetworkHandler = new NetworkHandler(NetworkThread.getInstance().getLooper(),
                URI.create(wsAddress), messageCallback);
        //TODO
        //{"player_name": "nian", "action": "status", "handout_pokers": [], "req_status": 0}
        JSONObject obj = new JSONObject();
        try {
            obj.put("player_name", loginName);
            obj.put("action", "status");
            obj.put("handout_pokers", new JSONArray());
            obj.put("req_status", PlayerStatus.Logined.getValue());
        } catch (Exception e) {
            VLog.error("Websocket Close exception " + e.toString());
        }

        VLog.info("ClientGame Login send string " + obj.toString());
        mNetworkHandler.sendWebSocketMessage(obj.toString());
    }

    @Override
    public void newPlayerStatus(PlayerStatus status, List<Integer> cards) {

        if (status == PlayerStatus.Handout) {
            CardMode cm = CardMode.getCardMode(cards);
            if (cm == CardMode.MODE_INVALID) {
                mUIView.OnLoginResult(false, "出牌不符合规则");
                return;
            } else {
                CardMode centerCM = CardMode.getCardMode(mCenterDispatchPokers);
                if (cm == centerCM || cm == CardMode.MODE_BOMB || cm == CardMode.MODE_TWO_RED2 || mCenterPokerIssuer == mWeSeatPos) {
                    VLog.info(String.format(Locale.getDefault(),
                            "We pos [%d] issue poker, center poker was issued by [%d]", mWeSeatPos, mCenterPokerIssuer));
                } else {
                    mUIView.showNotifyDialog("Warning", "出牌不符合规则", false);
                    return;
                }
            }

        }

        boolean handoutPokers = (cards != null) && !cards.isEmpty();
        if (handoutPokers && (getOwnedPokers().size() == cards.size())) {
            status = PlayerStatus.RunOut;
        }

        JSONArray jsonArray = new JSONArray();
        if (handoutPokers) {
            for (Integer x : cards) {
                jsonArray.put(x.intValue());
            }
        }

        JSONObject sendJsonObject = new JSONObject();
        try {
            sendJsonObject.put("player_name", mPlayerName);
            sendJsonObject.put("action", "status");
            sendJsonObject.put("handout_pokers", jsonArray);
            sendJsonObject.put("req_status", status.getValue());
        } catch (Exception e) {
            errorHandler(e.toString(), false);
        }

        mNetworkHandler.sendWebSocketMessage(sendJsonObject.toString());
    }

}
