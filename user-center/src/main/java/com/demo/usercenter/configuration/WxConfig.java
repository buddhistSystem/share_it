package com.demo.usercenter.configuration;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置类
 */
@Configuration
public class WxConfig {

    @Bean
    public WxMaConfig wxMaConfig(){
        WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
        wxMaDefaultConfig.setAppid("wx5833b4bc5da75cf4");
        wxMaDefaultConfig.setSecret("b436b16919d647c60474fc423687b587");
        return wxMaDefaultConfig;
    }

    @Bean
    public WxMaService wxMaService(WxMaConfig wxMaConfig){
        WxMaServiceImpl wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }
}
