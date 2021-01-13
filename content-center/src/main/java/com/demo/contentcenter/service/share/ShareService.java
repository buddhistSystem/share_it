package com.demo.contentcenter.service.share;

import com.demo.contentcenter.dao.share.ShareMapper;
import com.demo.contentcenter.domain.dto.content.ShareAuditDto;
import com.demo.contentcenter.domain.dto.content.ShareDto;
import com.demo.contentcenter.domain.dto.messaging.UserAddBonusMsgDto;
import com.demo.contentcenter.domain.dto.user.UserDto;
import com.demo.contentcenter.domain.entity.Share;
import com.demo.contentcenter.feignclient.UserCenterFeignClient;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Objects;

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

    @Resource
    private RocketMQTemplate rocketMQTemplate;

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

    public Share auditById(Integer id, ShareAuditDto shareAuditDto) {
        //1.查询share是否存在
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("该分享不存在！");
        }
        //2.审核，修改状态
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("该分享已经审核，或者审核不通过！");
        }
        share.setAuditStatus(shareAuditDto.getAuditStatusEnum().toString());
        share.setReason(shareAuditDto.getReason());
        this.shareMapper.updateByPrimaryKeySelective(share);
        //3.如果PASS，为发布人增加积分,积分服务并不太重要，异步执行，改善用户体验
        // 当前有4种方式实现异步 1.AsyncRestTemplate 2.@Async注解 3.WebClient spring5.0引入， 4.MQ
        // 当前将消息发送到mq，user-center去消费，并为发布人增加积分
        this.rocketMQTemplate.convertAndSend("add-bonus",
                UserAddBonusMsgDto.
                        builder()
                        .userId(share.getUserId())
                        .bonus(50)
                        .build());
        return share;
    }
}
