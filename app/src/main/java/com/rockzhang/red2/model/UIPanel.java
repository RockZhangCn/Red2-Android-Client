package com.rockzhang.red2.model;

import java.util.List;

public interface UIPanel {
    void showName(String name);

    void showMessage(String message, boolean shouldDialog);

    void showPokers(List<Integer> cards);

    void showTimer(Boolean show);

}
