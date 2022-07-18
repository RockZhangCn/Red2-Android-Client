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
    private WebSocketClient mClientWS;
    private MessageCallback mMessageCallback;

    public NetworkHandler(@NonNull Looper looper, URI connAddr, MessageCallback callback) {
        super(looper);
        mMessageCallback = callback;
        mClientWS = new WebSocketClient(connAddr) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                VLog.info("WS onOpen called");
            }

            @Override
            public void onMessage(String message) {
                VLog.debug("WS received message " + message);
                mMessageCallback.OnReceivedMessage(GameMessage.fromString(message));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                VLog.info("WS onClose called");
                String errorStr = String.format("%s closed WS with code %d and reason %s",
                        remote ? "Server" : "Client", code, reason);
                //errorHandler(errorStr);
            }

            @Override
            public void onError(Exception ex) {
                VLog.info("WS onError called");
                errorHandler(ex.toString());
            }
        };

        startWebSocket();
    }

    public void logout() {
        mMessageCallback = null;
        post(new Runnable() {
            @Override
            public void run() {
                mClientWS.close(-1, "User logout");
                mClientWS = null;
            }
        });

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
        if (mMessageCallback != null) {
            mMessageCallback.OnReceivedMessage(obj);
        }
    }

    public void startWebSocket() {
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    mClientWS.connectBlocking();
                } catch (Exception e) {
                    mClientWS = null;
                    errorHandler(e.toString());
                }
            }
        });

    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }

    public void sendWebSocketMessage(String message) {
        VLog.info("Client WebSocket will send message " + message);
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mClientWS != null)
                        mClientWS.send(message);
                } catch (Exception e) {
                    VLog.error("Send message exception " + e.toString());
                }
            }
        });
    }

    public interface MessageCallback {
        void OnReceivedMessage(JSONObject obj);
    }
}
