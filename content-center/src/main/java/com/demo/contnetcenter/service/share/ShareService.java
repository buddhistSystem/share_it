package com.demo.contnetcenter.service.share;

import com.demo.contnetcenter.dao.share.ShareMapper;
import com.demo.contnetcenter.domain.dto.content.ShareDto;
import com.demo.contnetcenter.domain.dto.user.UserDto;
import com.demo.contnetcenter.domain.entity.Share;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ShareService {

    @Resource
    private ShareMapper shareMapper;

    @Resource
    private DiscoveryClient discoveryClient;

    public ShareDto findById(Integer id) {
        //获取分享详情
        Share share = shareMapper.selectByPrimaryKey(id);
        //通过share中的用户id获取用户信息
        Integer userId = share.getId();
        //使用http调用用user-center服务,传统方式url是写死的，微服务能动态获取
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        //客户端负载均衡
        List<String> urls = instances.stream()
                .map(instance -> instance.getUri() + "/users/{id}")
                .collect(Collectors.toList());
        int random = ThreadLocalRandom.current().nextInt(urls.size());
        String url = urls.get(random);
        System.out.println(url);

        RestTemplate restTemplate = new RestTemplate();
        UserDto userDto = restTemplate.getForObject(url, UserDto.class, userId);

        ShareDto shareDto = new ShareDto();
        BeanUtils.copyProperties(share, shareDto);
        shareDto.setWxNickname(userDto.getWxNickname());
        return shareDto;
    }

}
