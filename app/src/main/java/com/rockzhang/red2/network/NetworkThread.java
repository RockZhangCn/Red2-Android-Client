package com.rockzhang.red2.network;

import android.os.HandlerThread;

public class NetworkThread extends HandlerThread {
    public static volatile NetworkThread s_instance = null;

    private NetworkThread(String name) {
        super(name);
    }

    public static NetworkThread getInstance() {
        if (s_instance == null) {
            synchronized (NetworkThread.class) {
                if (s_instance == null) {
                    s_instance = new NetworkThread("NetworkThread");
                    s_instance.start();
                }
            }
        }
        return s_instance;
    }

}
