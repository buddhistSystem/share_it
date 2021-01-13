package com.demo.usercenter.service.user;

import com.demo.usercenter.dao.user.UserMapper;
import com.demo.usercenter.domain.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public User findById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
