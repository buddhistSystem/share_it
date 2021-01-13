package com.demo.usercenter.controller.user;

import com.demo.usercenter.domain.entity.User;
import com.demo.usercenter.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        log.info("user-center findById 方法被调用！");
        return this.userService.findById(id);
    }

}