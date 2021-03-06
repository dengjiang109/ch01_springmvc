package com.yang.settings.service.impl;

import com.yang.exception.LoginException;
import com.yang.settings.dao.UserDao;
import com.yang.settings.entity.User;
import com.yang.settings.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("service")
public class MyServiceImpl implements MyService {
    @Resource
    private UserDao userDao;

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT,
            readOnly = false,
            rollbackFor = {
                    LoginException.class
            }
    )
    @Override
    public User login(String loginAct,String loginPwd,String ip) throws LoginException {
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);
        if (user == null) {
            throw new LoginException("账户密码错误");
        }
        String expireTime = user.getExpireTime();
        String currentTime = user.getCreateTime();
        if (expireTime.compareTo(currentTime)<0){
            throw new LoginException("账户已失效");
        }
        String lockState = user.getLockState();
        if ("0".equals(lockState)){
            throw new LoginException("账户已锁定");
        }
        String alwIps = user.getAllowIps();
        if(!alwIps.contains(ip)){
            System.out.println(alwIps+"22222222222222222222");
            throw new LoginException("ip地址受限");
        }


        return user;
    }
}
