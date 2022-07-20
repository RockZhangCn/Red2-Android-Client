package com.rockzhang.red2.utils;

import android.media.MediaPlayer;
import android.os.Handler;

import com.rockzhang.red2.app.Red2App;

public class GameSound {
    private static volatile GameSound s_instance = null;

    private Handler soundHandler;
    private MediaPlayer mMediaPlayer = null;
    private int mPlayerCount = 1;

    private GameSound() {
        mMediaPlayer = new MediaPlayer();
        soundHandler = new Handler(SoundPlayThread.getInstance().getLooper());
    }

    public static GameSound getInstance() {
        if (s_instance == null) {
            synchronized (GameSound.class) {
                if (s_instance == null) {
                    s_instance = new GameSound();
                }
            }
        }

        return s_instance;
    }

    public void playSound(SoundType sound) {
        playSound(sound, 1);
    }


    public void playSound(final SoundType sound, final int count) {
        mPlayerCount = count;
        soundHandler.post(new Runnable() {
            @Override
            public void run() {
                while (mPlayerCount-- > 0) {
                    mMediaPlayer.release();
                    mMediaPlayer = MediaPlayer.create(Red2App.getInstance(), sound.getResID());
                    mMediaPlayer.start();
                }
            }
        });
    }

    public void stopSound() {
        mPlayerCount = 0;
        soundHandler.post(new Runnable() {
            @Override
            public void run() {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });
    }

}


