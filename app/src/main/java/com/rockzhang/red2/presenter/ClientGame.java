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
        VLog.error(message);
        currentUser().showMessage(message, shouldDialog);
    }

    public void setOwnedPokers(List<Integer> mOwnedPokers) {
        this.mOwnedPokers = mOwnedPokers;
    }

    private List<Integer> mOwnedPokers = new ArrayList<>(32);

    private JSONArray mJsonArray;

    public ClientGame(IGameView view) {
        mUIView = view;
    }

    NetworkHandler.MessageCallback messageCallback = new NetworkHandler.MessageCallback() {
        @Override
        public void OnReceivedMessage(JSONObject obj) {
            //mUIView.getUIPanelList()
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

                if (action.equalsIgnoreCase("network_issue")) {
                    currentUser().showMessage(obj.getString("message"), true);
                } else if (action.equalsIgnoreCase("status_broadcast")) {

                }

            } catch (Exception e) {
                currentUser().showMessage(e.toString(), false);
            }
        }
    };

    @Override
    public void Login(String wsAddress, String loginName) {
        mPlayerName = loginName;
        mNetworkHandler = new NetworkHandler(NetworkThread.getInstance().getLooper(),
                URI.create("ws://love.rockzhang.com:5678"), messageCallback);
        mNetworkHandler.sendWebSocketMessage("");
    }

    @Override
    public void NewPlayerStatus(PlayerStatus status, List<Integer> cards) {
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
