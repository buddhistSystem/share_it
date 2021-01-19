package com.demo.usercenter.controller.user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.demo.usercenter.auth.CheckLogin;
import com.demo.usercenter.domain.dto.user.JwtTokenResponseDto;
import com.demo.usercenter.domain.dto.user.LoginResponseDto;
import com.demo.usercenter.domain.dto.user.UserLoginDto;
import com.demo.usercenter.domain.dto.user.UserResponseDto;
import com.demo.usercenter.domain.entity.User;
import com.demo.usercenter.service.user.UserService;
import com.demo.usercenter.util.JwtOperator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxMaService wxMaService;

    @Resource
    private JwtOperator jwtOperator;

    @CheckLogin
    @RequestMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        log.info("user-center findById 方法被调用！");
        return this.userService.findById(id);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody UserLoginDto userLoginDto) throws WxErrorException {
        //用code请求微信api，验证用户已经登录过微信小程序
        WxMaJscode2SessionResult result = this.wxMaService.getUserService().getSessionInfo(userLoginDto.getCode());
        //如果没有登录微信小程序就会抛出WxErrorException
        String openid = result.getOpenid();
        User user = this.userService.login(userLoginDto, openid);
        //颁发token
        Map<String, Object> jwtParam = new HashMap<>();
        jwtParam.put("id", user.getId());
        jwtParam.put("wxNickName", user.getWxNickname());
        jwtParam.put("role", user.getRoles());
        String token = jwtOperator.generateToken(jwtParam);
        log.info("用户{}登录成功，生成的token= {}，有效期到：{}",
                userLoginDto.getWxNickName(),
                token,
                jwtOperator.getExpirationDateFromToken(token));

        //构建响应
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .user(
                        UserResponseDto.builder()
                                .id(user.getId())
                                .avatarUrl(user.getAvatarUrl())
                                .bonus(user.getBonus())
                                .wxNickName(user.getWxNickname())
                                .build()
                )
                .token(
                        JwtTokenResponseDto.builder()
                                .token(token)
                                .expirationTime(jwtOperator.getExpirationDateFromToken(token).getTime())
                                .build()
                )
                .build();
        return loginResponseDto;
    }


    @GetMapping("/gen-test-token")
    public String genToken() {
        Map<String, Object> jwtParam = new HashMap<>();
        jwtParam.put("id", "1");
        jwtParam.put("wxNickName", "");
        jwtParam.put("role", "admin");
        String token = jwtOperator.generateToken(jwtParam);
        return token;
    }


}
