package com.rockzhang.red2.utils;

import android.os.HandlerThread;

public class SoundPlayThread extends HandlerThread {
    public static volatile SoundPlayThread s_instance = null;

    private SoundPlayThread(String name) {
        super(name);
    }

    public static SoundPlayThread getInstance() {
        if (s_instance == null) {
            synchronized (SoundPlayThread.class) {
                if (s_instance == null) {
                    s_instance = new SoundPlayThread("SoundPlayThread");
                    s_instance.start();
                }
            }
        }
        return s_instance;
    }

}
