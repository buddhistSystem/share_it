package com.demo.contnetcenter.service.share;

import com.demo.contnetcenter.dao.share.ShareMapper;
import com.demo.contnetcenter.domain.dto.content.ShareDto;
import com.demo.contnetcenter.domain.dto.user.UserDto;
import com.demo.contnetcenter.domain.entity.Share;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class ShareService {

    @Resource
    private ShareMapper shareMapper;

    public ShareDto findById(Integer id) {
        //获取分享详情
        Share share = shareMapper.selectByPrimaryKey(id);
        //通过share中的用户id获取用户信息
        Integer userId = share.getId();
        //调用用user-center服务
        RestTemplate restTemplate = new RestTemplate();
        UserDto userDto = restTemplate.getForObject("http://localhost:8080/users/{id}", UserDto.class, userId);
        ShareDto shareDto = new ShareDto();
        BeanUtils.copyProperties(share,shareDto);
        shareDto.setWxNickname(userDto.getWxNickname());
        return shareDto;
    }

}
