package com.rockzhang.red2.utils;

import com.rockzhang.red2.R;

public enum SoundType {
    SOUND_GAME_START(R.raw.gamestart),
    SOUND_GAME_LOSE(R.raw.losegame),
    SOUND_GAME_WIN(R.raw.wingame),

    SOUND_WAS_KILLED(R.raw.chesssuicide),
    SOUND_KILL_ONE(R.raw.killchess),
    SOUND_CHESS_EQUAL(R.raw.chessequal),

    SOUND_GAME_CLOCK(R.raw.timetick),
    SOUND_GAME_MOVE(R.raw.chessmove);

    private int mResID = 0;

    SoundType(int resID) {
        mResID = resID;
    }

    public int getResID() {
        return mResID;
    }

}
