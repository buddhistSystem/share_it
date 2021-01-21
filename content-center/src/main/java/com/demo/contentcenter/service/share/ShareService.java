package com.demo.contentcenter.service.share;

import com.alibaba.fastjson.JSON;
import com.demo.contentcenter.dao.RocketmqTransactionLogMapper;
import com.demo.contentcenter.dao.share.MidUserShareMapper;
import com.demo.contentcenter.dao.share.ShareMapper;
import com.demo.contentcenter.domain.dto.content.ShareAuditDto;
import com.demo.contentcenter.domain.dto.content.ShareDto;
import com.demo.contentcenter.domain.dto.messaging.UserAddBonusMsgDto;
import com.demo.contentcenter.domain.dto.user.UserAddBonusDto;
import com.demo.contentcenter.domain.dto.user.UserDto;
import com.demo.contentcenter.domain.entity.MidUserShare;
import com.demo.contentcenter.domain.entity.RocketmqTransactionLog;
import com.demo.contentcenter.domain.entity.Share;
import com.demo.contentcenter.domain.enums.AuditStatusEnum;
import com.demo.contentcenter.feignclient.UserCenterFeignClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ShareService {

    @Resource
    private ShareMapper shareMapper;

    @Resource
    private MidUserShareMapper midUserShareMapper;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private UserCenterFeignClient userCenterFeignClient;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Resource
    private Source source;

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
        //2. 如果不是未审核，直接抛异常
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("该分享已经审核，或者审核不通过！");
        }
        //3.如果PASS，为发布人增加积分,积分服务并不太重要，异步执行，改善用户体验
        // 当前有4种方式实现异步 1.AsyncRestTemplate 2.@Async注解 3.WebClient spring5.0引入， 4.MQ
        if (AuditStatusEnum.PASS.equals(shareAuditDto.getAuditStatusEnum())) {
            //发送rocketmq事务消息
            String transactionId = UUID.randomUUID().toString();
            this.sendRocketMqMessage(share, transactionId, id, shareAuditDto);
            //todo 使用stream编程模型导致producer重复注册问题为解决
            //this.sendStreamRocketMqMessage(share, transactionId, id, shareAuditDto);
        } else {
            //不通过，只更新数据库状态，不发送mq消息，增加用户积分
            this.auditByIdInDB(id, shareAuditDto);
        }

        // 发送mq之后的代码，若之后的代码发生异常，会导致事务回滚，但是mq消息已经发出，
        // 造成数据不一致，所以mq需要事务消息，监听本地事务状态
        // 。。。
        return share;
    }

    //通过stream编程模型发送rocketmq消息
    private void sendStreamRocketMqMessage(Share share, String transactionId, Integer id, ShareAuditDto shareAuditDto) {
        //使用stream编程模型，开启事务消息，以及生产组和topic都在配置文件中指定
        this.source.output().send(MessageBuilder
                .withPayload(
                        UserAddBonusMsgDto
                                .builder()
                                .userId(share.getUserId())
                                .bonus(50)
                                .build())
                .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                .setHeader("share_id", id)
                .setHeader("dto", JSON.toJSONString(shareAuditDto))
                .build());
    }


    //通过spring编程模型发送rocketmq消息
    private void sendRocketMqMessage(Share share, String transactionId, Integer id, ShareAuditDto shareAuditDto) {
        rocketMQTemplate.sendMessageInTransaction(
                "add-bonus-group", // 生产组
                "add-bonus",       // 主题
                MessageBuilder
                        .withPayload(  // 消息内容
                                UserAddBonusMsgDto
                                        .builder()
                                        .userId(share.getUserId())
                                        .bonus(50)
                                        .build())
                        .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                        .setHeader("share_id", id)
                        .build(),
                shareAuditDto //参数args
        );
    }

    public void auditByIdInDB(Integer id, ShareAuditDto shareAuditDto) {
        Share share = Share
                .builder()
                .id(id)
                .auditStatus(shareAuditDto.getAuditStatusEnum().toString())
                .reason(shareAuditDto.getReason())
                .build();
        this.shareMapper.updateByPrimaryKeySelective(share);
    }

    /**
     * 审核表本地事务更新后，记录一条rocketmq日志到数据库
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDto shareAuditDto, String transactionId) {
        this.auditByIdInDB(id, shareAuditDto);
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog
                        .builder()
                        .transactionId(transactionId)
                        .log("审核分享。。。")
                        .build()
        );
    }

    public PageInfo<Share> q(String title, Integer pageNo, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNo, pageSize);
        List<Share> shares = this.shareMapper.selectByParam(title);
        //处理过的shares，若用户未登录为游客，则分享列表不反悔下载地址
        List<Share> shareDealed = new ArrayList<>();
        if (userId == null) {
            shareDealed = shares.stream().peek(
                    share -> {
                        share.setDownloadUrl(null);
                    }
            ).collect(Collectors.toList());
        } else {
            shareDealed = shares.stream().peek(
                    share -> {
                        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                                MidUserShare.builder()
                                        .userId(userId)
                                        .shareId(share.getId())
                                        .build()
                        );
                        if (midUserShare == null) {
                            share.setDownloadUrl(null);
                        }
                    }
            ).collect(Collectors.toList());
        }
        //List<Share> shares = this.shareMapper.listShare(Share.builder().title(title).build());
        PageInfo<Share> pageInfo = new PageInfo<>(shareDealed);
        return pageInfo;
    }

    /**
     * 积分兑换指定分享
     */
    public Share exchangeById(Integer id, HttpServletRequest httpServletRequest) {
        Integer userId = (Integer) httpServletRequest.getAttribute("id");
        //根据id查询share是否存在
        Share share = this.shareMapper.selectOne(
                Share.builder()
                        .id(id)
                        .auditStatus("PASS")
                        .build()
        );
        if (share == null) {
            throw new IllegalArgumentException("该分享不存在！或者还未审核通过！");
        }
        //查询mid_user_share表查询该用户是否已经兑换过该分享内容
        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                MidUserShare.builder()
                        .userId(userId)
                        .shareId(id)
                        .build()
        );
        if (midUserShare != null) {
            return share;
        }
        //根据当前登录人id，查询该用户积分是否足够
        UserDto userDto = this.userCenterFeignClient.findById(userId);
        if (share.getPrice() > userDto.getBonus()) {
            throw new IllegalArgumentException("积分不够！");
        }
        this.userCenterFeignClient.addBonus(UserAddBonusDto.builder()
                .userId(userId)
                .bonus(0 - share.getPrice())
                .build()
        );
        this.midUserShareMapper.insertSelective(
                MidUserShare.builder()
                        .userId(userId)
                        .shareId(id)
                        .build()
        );
        return share;
    }
}
