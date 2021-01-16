package com.demo.usercenter.service.user;

import com.demo.usercenter.dao.BonusEventLogMapper;
import com.demo.usercenter.dao.user.UserMapper;
import com.demo.usercenter.domain.dto.messaging.UserAddBonusMsgDto;
import com.demo.usercenter.domain.entity.BonusEventLog;
import com.demo.usercenter.domain.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BonusEventLogMapper bonusEventLogMapper;

    public User findById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void receive(UserAddBonusMsgDto userAddBonusMsgDto) {
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