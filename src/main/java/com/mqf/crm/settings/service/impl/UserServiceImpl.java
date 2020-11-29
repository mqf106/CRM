package com.mqf.crm.settings.service.impl;

import com.mqf.crm.exception.LoginException;
import com.mqf.crm.settings.dao.UserDao;
import com.mqf.crm.settings.domain.User;
import com.mqf.crm.settings.service.UserService;
import com.mqf.crm.utils.DateTimeUtil;
import com.mqf.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    //注解
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);
        if (user == null){
            throw new LoginException("账号密码错误");
        }
        //如果执行到这里就是数据库有数据，再进行对时间限制，ip限制，锁限制
        //验证失效时间
        String expireTime = user.getExpireTime();
        String nowTime = DateTimeUtil.getSysTime();
        int count = expireTime.compareTo(nowTime);
        if (count<0){
            throw new LoginException("账号使用时间已经失效");
        }
        //判断锁定状态
        if("0".equals(user.getLockState())){
            throw new LoginException("账号已被锁定");
        }
        if (!user.getAllowIps().contains(ip)){
            throw new LoginException("用户的ip地址无权限");
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList = userDao.selectUserList();
        return userList;
    }
}
