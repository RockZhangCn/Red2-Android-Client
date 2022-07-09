package com.rockzhang.red2.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.rockzhang.red2.biz.GameMessage;
import com.rockzhang.red2.log.VLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;


public class NetworkHandler extends Handler {
    private  WebSocketClient mClientWS;
    private  MessageCallback mMessageCallback;

    public interface MessageCallback {
        void OnReceivedMessage(JSONObject obj) ;
    }

    private void errorHandler(String errorStr) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", "network_issue");
            obj.put("position", -1);
            obj.put("pokers", new JSONArray());
            obj.put("status", -1);
            obj.put("message", errorStr);
        } catch (Exception e) {
            VLog.error("Websocket Close exception " + e.toString());
        }

        VLog.info("WS received reason " + errorStr);
        mMessageCallback.OnReceivedMessage(obj);
    }

    public NetworkHandler(@NonNull Looper looper, URI connAddr, MessageCallback callback) {
        super(looper);
        mMessageCallback = callback;
        mClientWS = new WebSocketClient(connAddr) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {

            }

            @Override
            public void onMessage(String message) {
                VLog.info("WS received message " + message);
                GameMessage.fromString(message);
                mMessageCallback.OnReceivedMessage(GameMessage.fromString(message));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                String errorStr = String.format("{} closed WS with code {} and reason {}",
                        remote?"Server":"Client", code, reason);
                errorHandler(errorStr);
            }

            @Override
            public void onError(Exception ex) {
                errorHandler(ex.toString());
            }
        };

        startWebSocket();
    }

    public void startWebSocket() {
        try {
            mClientWS.connectBlocking();
        } catch (Exception e) {
            errorHandler(e.toString());
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }

    public void sendWebSocketMessage(String message) {
        VLog.info("Client Websocket will send message " + message);
        post(new Runnable() {
            @Override
            public void run() {
                mClientWS.send(message);
            }
        });
    }
}
