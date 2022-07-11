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

public class ClientGame implements  IClientGamePresenter {

    private NetworkHandler mNetworkHandler;
    private IGameView mUIView;
    private String mPlayerName;

    public List<Integer> getOwnedPokers() {
        return mOwnedPokers;
    }

    private UIPanel currentUser() {
        return mUIView.getUIPanelList().get(0);
    }

    private void errorHandler(String message, boolean shouldDialog) {
        VLog.error("ClientGame errorHandler message " + message);
        currentUser().showMessage(message, shouldDialog);
    }

    public void setOwnedPokers(List<Integer> mOwnedPokers) {
        this.mOwnedPokers = mOwnedPokers;
    }

    private List<Integer> mOwnedPokers = new ArrayList<>(32);

    private JSONArray mJsonArray;

    public ClientGame() { }

    public void setUIView(IGameView view) {
        mUIView = view;
    }

    private static ClientGame s_Instance = null;
    public static ClientGame getInstance() {
        if (s_Instance == null) {
            synchronized (ClientGame.class) {
                if (s_Instance == null) {
                    s_Instance = new ClientGame();
                }
            }
        }
        return s_Instance;
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
                if (action == null) {
                    errorHandler("Message format error", false);
                    return;
                }

                if (action.equals("pong")) {
                    return;
                }

                VLog.info("ClientGame OnReceivedMessage " + obj.toString());
                if (action.equalsIgnoreCase("network_issue")) {
                    VLog.info("We get error network_issue");
                    mUIView.OnLoginResult(false, obj.getString("message"));
                } else if (action.equalsIgnoreCase("status_broadcast")) {

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
                                return;
                            }
                        }
                    }

                    mUIView.OnLoginResult(false, "unknown reason.");
                }

            } catch (Exception e) {
                VLog.error("ClientGame meet exception " + e.toString());
                mUIView.OnLoginResult(false, e.toString());
            }
        }
    };

    @Override
    public void login(String wsAddress, String loginName) {
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
        boolean handoutPokers = !cards.isEmpty();
        if ((getOwnedPokers().size() == cards.size()) && handoutPokers) {
            status = PlayerStatus.RunOut;
        } else {
            status = PlayerStatus.Handout;
        }

        mJsonArray = new JSONArray();
        if (handoutPokers) {
            for (Integer x : cards) {
                mJsonArray.put(x.intValue());
            }
        }

        JSONObject sendJsonObject = new JSONObject();
        try {
            sendJsonObject.put("player_name", mPlayerName);
            sendJsonObject.put("action", "status");
            sendJsonObject.put("handout_pokers", mJsonArray);
            sendJsonObject.put("req_status", status.getValue());
        } catch (Exception e) {
            errorHandler(e.toString(), false);
        }
        mNetworkHandler.sendWebSocketMessage(sendJsonObject.toString());
    }

}
