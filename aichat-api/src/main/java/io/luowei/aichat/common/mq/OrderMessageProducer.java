package io.luowei.aichat.common.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
public class OrderMessageProducer {
    @Value("${rocketmq.producer.sendMessageTimeout}")
    private Integer messageTimeOut;

    // 正常规模项目统一用一个 TOPIC
    private static final String topic = "MY_GPT";

    // 标签
    private static final String tag = ":success_order";

    // 直接注入使用，用于发送消息到 broker 服务器

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 普通发送
     */
    public void send(String orderId) {
        // 自带重试机制 2次
        rocketMQTemplate.convertAndSend(topic + tag, orderId);
//      rocketMQTemplate.send(topic + ":tag1", MessageBuilder.withPayload(user).build()); // 等价于上面一行
    }

    /**
     * 同步方法
     * syncSend() 方法会阻塞当前线程。  成功返回 SendResult 对象，包含了消息的发送状态、消息ID等信息；失败 syncSend 会抛出 MessagingException 异常。
     */
    public SendResult sendMsg(String msgBody) {
        SendResult sendResult = rocketMQTemplate.syncSend(topic+tag, MessageBuilder.withPayload(msgBody).build());
        log.info("【sendMsg】sendResult={}", JSON.toJSONString(sendResult));
        return sendResult;
    }

    /**
     * 异步方法
     * 不会阻塞当前线程，asyncSend方法会立即返回。如果需要等待消息发送完成并处理发送结果，可以使用SendCallback回调接口。
     */
    public void sendAsyncMsg(String msgBody) {
        rocketMQTemplate.asyncSend(topic+tag, MessageBuilder.withPayload(msgBody).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                // 处理消息发送成功逻辑
            }
            @Override
            public void onException(Throwable throwable) {
                // 处理消息发送异常逻辑
            }
        });
    }

    /**
     * 单向消息（只负责发送消息，不等应答，不关心结果）
     */
    public void sendOneWayMsg(String msgBody) {
        rocketMQTemplate.sendOneWay(topic+tag, MessageBuilder.withPayload(msgBody).build());
    }

    /**
     * 单向顺序消息
     */
    public void sendOneWayOrderlyMsg(String msgBody,String id){
        rocketMQTemplate.sendOneWayOrderly(topic+tag, MessageBuilder.withPayload(msgBody).build(),id);
    }

    /**
     * 同步顺序消息
     */
    public void sendSyncOneWayOrderlyMsg(String msgBody,String id){
        SendResult sendResult =  rocketMQTemplate.syncSendOrderly(topic+tag, MessageBuilder.withPayload(msgBody).build(),id);
        log.info("【sendSyncOneWayOrderlyMsg】sendResult={}", JSON.toJSONString(sendResult));
    }

    /**
     * 异步顺序消息
     */
    public void sendAsyncOneWayOrderlyMsg(String msgBody,String id){
        rocketMQTemplate.asyncSendOrderly(topic+tag, MessageBuilder.withPayload(msgBody).build(),id, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info(sendResult.toString());
            }

            @Override
            public void onException(Throwable e) {
                log.info(e.getMessage());
            }
        });
    }

    /**
     * 延时消息
     */
    public void syncSendDelayTimeSecondsMsg(String msgBody, int delayLevel) {
        // 秒级
        SendResult sendResult = rocketMQTemplate.syncSendDelayTimeSeconds(topic+tag,msgBody, delayLevel);
        // 毫秒级
        // SendResult sendResult =rocketMQTemplate.syncSendDelayTimeMills(topic+tag2,msgBody, delayLevel);
        log.info("【syncSendDelayTimeSecondsMsg】sendResult={}", JSON.toJSONString(sendResult));
    }

    /**
     * 定时消息
     */
    public void syncSendDeliverTimeMillsMsg(String msgBody) {
        // 每天凌晨处理
        long time = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        SendResult sendResult = rocketMQTemplate.syncSendDeliverTimeMills(topic+tag,msgBody, time);
        log.info("【syncSendDelayTimeSecondsMsg】sendResult={}", JSON.toJSONString(sendResult));
    }

    /**
     * 批量发送
     */
    public void syncSendBatchMessage(List<String> msgs) {
        SendResult sendResult = rocketMQTemplate.syncSend(topic+tag,msgs);
        log.info("【syncSendBatchMessage】sendResult={}", JSON.toJSONString(sendResult));
    }

    /**
     * 事务消息
     */
    public void sendMessageInTransactionMsg(String msgBody) {
        Message<String> msgs = MessageBuilder.withPayload(JSON.toJSONString(msgBody))
                .setHeader("KEYS", msgBody)
                //设置事务ID
                .setHeader(RocketMQHeaders.TRANSACTION_ID,"KEY_"+msgBody)
                .build();
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(topic+tag,msgs,null);
        log.info("【sendMessageInTransactionMsg】transactionSendResult={}", JSON.toJSONString(transactionSendResult));
    }
}
