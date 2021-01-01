package com.demo.contnetcenter.service.user;

import com.demo.contnetcenter.dao.user.UserMapper;
import com.demo.contnetcenter.domain.entity.user.User;
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
