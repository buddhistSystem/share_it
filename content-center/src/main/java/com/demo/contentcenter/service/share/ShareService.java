package com.demo.contentcenter.service.share;

import com.demo.contentcenter.dao.share.ShareMapper;
import com.demo.contentcenter.domain.dto.content.ShareDto;
import com.demo.contentcenter.domain.dto.user.UserDto;
import com.demo.contentcenter.domain.entity.Share;
import com.demo.contentcenter.feignclient.UserCenterFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class ShareService {

    @Resource
    private ShareMapper shareMapper;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private UserCenterFeignClient userCenterFeignClient;

    /**
     * ribbon+restTemplate实现远程调用
     *
     * @param id
     * @return
     */
    public ShareDto findById(Integer id) {
        //获取分享详情
        Share share = shareMapper.selectByPrimaryKey(id);
        //通过share中的用户id获取用户信息
        Integer userId = share.getId();

        //使用http调用用user-center服务,传统方式url是写死的，微服务能动态获取
        /**
         List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
         //客户端负载均衡
         List<String> urls = instances.stream()
         .map(instance -> instance.getUri() + "/users/{id}")
         .collect(Collectors.toList());
         int random = ThreadLocalRandom.current().nextInt(urls.size());
         String url = urls.get(random);
         System.out.println(url);
         **/

        //使用Ribbon 和 restTemplate 实现客户端侧负载均衡
        String url = "http://user-center/users/{id}";

        System.out.println(url);
        UserDto userDto = restTemplate.getForObject(url, UserDto.class, userId);
        ShareDto shareDto = new ShareDto();
        BeanUtils.copyProperties(share, shareDto);
        shareDto.setWxNickname(userDto.getWxNickname());
        return shareDto;
    }

    public ShareDto feignFindById(Integer id) {
        //获取分享详情
        Share share = shareMapper.selectByPrimaryKey(id);
        //通过share中的用户id获取用户信息
        Integer userId = share.getId();
        //通过feign方式调用
        UserDto userDto = userCenterFeignClient.findById(userId);
        ShareDto shareDto = new ShareDto();
        BeanUtils.copyProperties(share, shareDto);
        shareDto.setWxNickname(userDto.getWxNickname());
        return shareDto;
    }

}
