package com.demo.contnetcenter.dao;

import com.demo.contnetcenter.domain.entity.User;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<User> {

    List<User> listUser();
}