package com.rockzhang.red2.utils;

import com.rockzhang.red2.R;

public enum SoundType {

    SOUND_GAME_LOSE(R.raw.losegame),
    SOUND_GAME_WIN(R.raw.wingame),
    SOUND_GAME_BOMB(R.raw.bomb),
    SOUND_GAME_CLOCK(R.raw.timetick),
    SOUND_GAME_RED2(R.raw.red22),
    SOUND_GAME_START(R.raw.startgame);

    private int mResID = 0;

    SoundType(int resID) {
        mResID = resID;
    }

    public int getResID() {
        return mResID;
    }

}
