package com.rockzhang.red2.presenter;

import com.rockzhang.red2.role.PlayerStatus;

import java.util.List;

public interface IClientGamePresenter {
    void login(String wsAddress, String loginName);

    void logout(String loginName);

    void newPlayerStatus(PlayerStatus status, List<Integer> cards);
}
