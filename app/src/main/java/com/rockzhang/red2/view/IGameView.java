package com.rockzhang.red2.view;

import com.rockzhang.red2.model.UIPanel;

import java.util.List;

public interface IGameView {
    List<UIPanel> getUIPanelList();

    void OnLoginResult(boolean success, String message);

    void OnPlayerStatusChanged(int playsStatus, boolean isActive);
}
