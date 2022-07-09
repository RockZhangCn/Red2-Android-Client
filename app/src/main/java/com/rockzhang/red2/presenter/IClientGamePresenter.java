package com.rockzhang.red2.presenter;

import com.rockzhang.red2.role.PlayerStatus;

import java.util.List;

public interface IClientGamePresenter {
    void Login(String wsAddress, String loginName);
    void NewPlayerStatus(PlayerStatus status, List<Integer> cards);
}
