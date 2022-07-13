package com.rockzhang.red2.role;

public class PlayerStatus {
    public static PlayerStatus Offline  = new PlayerStatus(-1);
    public static PlayerStatus Logined  = new PlayerStatus(0);
    public static PlayerStatus Started  = new PlayerStatus(1);
    public static PlayerStatus SingleOne  = new PlayerStatus(2);
    public static PlayerStatus NoTake  = new PlayerStatus(3);
    public static PlayerStatus Share2  = new PlayerStatus(4);
    public static PlayerStatus NoShare  = new PlayerStatus(5);
    public static PlayerStatus Handout  = new PlayerStatus(6);
    public static PlayerStatus RunOut  = new PlayerStatus(7);
    public static PlayerStatus GameOver  = new PlayerStatus(8);

    private int mInternalValue = -1;
    private PlayerStatus(int status) {
        mInternalValue = status;
    }

    public int getValue() {
        return mInternalValue;
    }

    public static PlayerStatus fromValue(int value) {
        return new PlayerStatus(value);
    }

}
