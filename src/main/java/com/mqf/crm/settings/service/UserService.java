package com.mqf.crm.settings.service;

import com.mqf.crm.exception.LoginException;
import com.mqf.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
