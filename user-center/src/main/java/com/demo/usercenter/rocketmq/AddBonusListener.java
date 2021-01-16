package com.demo.usercenter.rocketmq;

import com.demo.usercenter.domain.dto.messaging.UserAddBonusMsgDto;
import com.demo.usercenter.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 间挺add-bonus主题
 */
@Service
@RocketMQMessageListener(topic = "add-bonus", consumerGroup = "test-group")
@Slf4j
public class AddBonusListener implements RocketMQListener<UserAddBonusMsgDto> {

    @Resource
    private UserService userService;

    @Override

    public void onMessage(UserAddBonusMsgDto userAddBonusMsgDto) {
        this.userService.receive(userAddBonusMsgDto);
    }
}
