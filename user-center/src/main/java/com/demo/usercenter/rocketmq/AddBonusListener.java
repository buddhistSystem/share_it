package com.demo.usercenter.rocketmq;

import com.demo.usercenter.dao.BonusEventLogMapper;
import com.demo.usercenter.dao.user.UserMapper;
import com.demo.usercenter.domain.dto.messaging.UserAddBonusMsgDto;
import com.demo.usercenter.domain.entity.BonusEventLog;
import com.demo.usercenter.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 间挺add-bonus主题
 */
@Service
@RocketMQMessageListener(topic = "add-bonus", consumerGroup = "test-group")
@Slf4j
public class AddBonusListener implements RocketMQListener<UserAddBonusMsgDto> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BonusEventLogMapper bonusEventLogMapper;

    @Override

    public void onMessage(UserAddBonusMsgDto userAddBonusMsgDto) {
        //1.为用户增加积分
        Integer userId = userAddBonusMsgDto.getUserId();
        User user = this.userMapper.selectByPrimaryKey(userId);
        Integer bonus = userAddBonusMsgDto.getBonus();
        user.setBonus(bonus);
        this.userMapper.updateByPrimaryKey(user);
        //2.记录日志到bonus_event_log表中
        this.bonusEventLogMapper.insertSelective(BonusEventLog.builder()
                .userId(userId)
                .value(bonus)
                .event("CONTRIBUTE")
                .createTime(new Date())
                .description("投稿加积分")
                .build());
    }
}
