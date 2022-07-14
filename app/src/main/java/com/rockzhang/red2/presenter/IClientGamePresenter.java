package com.rockzhang.red2.presenter;

import com.rockzhang.red2.role.PlayerStatus;
import com.rockzhang.red2.view.IGameView;

import java.util.List;

public interface IClientGamePresenter {
    void login(String wsAddress, String loginName);
    void newPlayerStatus(PlayerStatus status, List<Integer> cards);
}
