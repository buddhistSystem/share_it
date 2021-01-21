package com.demo.usercenter.service.user;

import com.demo.usercenter.dao.BonusEventLogMapper;
import com.demo.usercenter.dao.user.UserMapper;
import com.demo.usercenter.domain.dto.messaging.UserAddBonusMsgDto;
import com.demo.usercenter.domain.dto.user.UserAddBonusDto;
import com.demo.usercenter.domain.dto.user.UserLoginDto;
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
        Integer bonus = userAddBonusMsgDto.getBonus() + user.getBonus();
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

    public User login(UserLoginDto userLoginDto, String openid) {
        // 通过用户的微信id查询用户是否注册
        User user = this.userMapper.selectOne(User.
                builder().
                wxId(openid)
                .build()
        );
        // 用户不存在，注册到user表中
        if (user == null) {
            User userToSave = User.builder()
                    .wxId(openid)
                    .wxNickname(userLoginDto.getWxNickName())
                    .roles("user")//普通用户
                    .avatarUrl(userLoginDto.getAvatarUrl())
                    .bonus(0)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            this.userMapper.insertSelective(userToSave);
            return userToSave;
        }
        //用户存在，直接返回该用户
        return user;
    }

    /**
     * 增加用户积分
     */
    public User addBonus(UserAddBonusDto userAddBonusDto) {
        Integer userId = userAddBonusDto.getUserId();
        Integer bonus = userAddBonusDto.getBonus();
        User user = this.userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setBonus(user.getBonus() + bonus);
        this.userMapper.updateByPrimaryKey(user);
        return user;
    }
}