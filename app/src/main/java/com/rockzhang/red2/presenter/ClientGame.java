package com.rockzhang.red2.presenter;

import com.rockzhang.red2.log.VLog;
import com.rockzhang.red2.model.UIPanel;
import com.rockzhang.red2.network.NetworkHandler;
import com.rockzhang.red2.network.NetworkThread;
import com.rockzhang.red2.role.PlayerStatus;
import com.rockzhang.red2.view.IGameView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ClientGame implements IClientGamePresenter {

    private NetworkHandler mNetworkHandler;
    private IGameView mUIView;
    private String mPlayerName;
    private int mWeSeatPos;
    private int mPlayerStatus;
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
                if (action == null) {
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
                    int activePos = obj.getInt("active_pos");
                    if (-1 < activePos && activePos < 4) {
                        mUIView.getUIPanelList().get((activePos + 4 - mWeSeatPos) % 4).showTimer(true);
                    }

                    // login handler
                    if (obj.has("status_all")) {
                        JSONArray list = obj.getJSONArray("status_all");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject singleUser = list.getJSONObject(i);
                            String playerName = singleUser.getString("player_name");
                            int seatPos = singleUser.getInt("position");
                            int playerStatus = singleUser.getInt("status");

                            if (playerStatus == PlayerStatus.Logined.getValue() && playerName.equals(mPlayerName)) {
                                mUIView.OnLoginResult(true, "We seat pos " + seatPos);
                                mWeSeatPos = seatPos;
                            }

                            if (playerName.equals(mPlayerName)) {
                                mPlayerStatus = playerStatus;
                                mUIView.OnPlayerStatusChanged(mPlayerStatus, mWeSeatPos == activePos);
                            }
                        }

                        if (mWeSeatPos == -1) {
                            mWeSeatPos = obj.getInt("recover_pos");
                            VLog.info("We recovered from network issue , pos is " + mWeSeatPos);
                        }

                        JSONArray centerJsonArray = obj.getJSONArray("center_pokers");
                        List<Integer> centerDispatchPokers = new ArrayList<>(centerJsonArray.length());
                        for (int j = 0; j < centerJsonArray.length(); j++) {
                            centerDispatchPokers.add((Integer) centerJsonArray.get(j));
                        }

                        mUIView.getUIPanelList().get(4).showPokers(centerDispatchPokers);

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject singleUser = list.getJSONObject(i);
                            String playerName = singleUser.getString("player_name");
                            int seatPos = singleUser.getInt("position");
                            int playerStatus = singleUser.getInt("status");
                            String message = singleUser.getString("message");

                            int layoutIndex = (seatPos + 4 - mWeSeatPos) % 4;
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
    private List<Integer> mOwnedPokers = new ArrayList<>(32);

    public ClientGame(IGameView view) {
        mUIView = view;
        mWeSeatPos = -1;
        mPlayerStatus = PlayerStatus.Offline.getValue();
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
        boolean handoutPokers = (cards!=null) && !cards.isEmpty();
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
