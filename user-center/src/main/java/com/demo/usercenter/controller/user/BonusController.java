package com.demo.usercenter.controller.user;

import com.demo.usercenter.auth.CheckLogin;
import com.demo.usercenter.domain.dto.user.UserAddBonusDto;
import com.demo.usercenter.domain.entity.User;
import com.demo.usercenter.service.user.UserService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
public class BonusController {

    @Resource
    private UserService userService;

    @PutMapping("/add-bonus")
    @CheckLogin
    public User addBonus(@RequestBody UserAddBonusDto userAddBonusDto){
        return this.userService.addBonus(userAddBonusDto);
    }

}
