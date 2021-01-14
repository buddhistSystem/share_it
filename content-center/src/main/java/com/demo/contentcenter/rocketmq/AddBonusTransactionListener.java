package com.demo.contentcenter.rocketmq;

import com.demo.contentcenter.dao.RocketmqTransactionLogMapper;
import com.demo.contentcenter.domain.dto.content.ShareAuditDto;
import com.demo.contentcenter.domain.entity.RocketmqTransactionLog;
import com.demo.contentcenter.service.share.ShareService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import javax.annotation.Resource;

/**
 * 监听增加积分本地事务
 */
@RocketMQTransactionListener(txProducerGroup = "add-bonus-group")
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    @Resource
    private ShareService shareService;

    @Resource
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    /**
     * 执行本地事务
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        MessageHeaders headers = message.getHeaders();

        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer shareId = Integer.valueOf((String) headers.get("share_id"));
        try {
            this.shareService.auditByIdWithRocketMqLog(shareId, (ShareAuditDto) o, transactionId);
            //若执行完更新DB后，该应用挂了，还未执行下一句，通知RocketMQ本地事务已经提交，此时就需要回查
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 回查本地事务是否执行成功
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageHeaders headers = message.getHeaders();

        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        RocketmqTransactionLog rocketmqTransactionLog = this.rocketmqTransactionLogMapper.selectOne(
                RocketmqTransactionLog
                        .builder()
                        .transactionId(transactionId)
                        .build()
        );
        if (rocketmqTransactionLog != null) {
            //如果日志表不为空，证明本地事务已经提交
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
