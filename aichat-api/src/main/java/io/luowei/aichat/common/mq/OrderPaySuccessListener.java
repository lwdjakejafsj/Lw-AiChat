package io.luowei.aichat.common.mq;


import io.luowei.aichat.service.order.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * 订单支付成功监听
 * 1. 订单支付成功回调，最好是快速变更订单状态，避免超时重试次数上限后不能做业务。所以推送出MQ消息来做【发货】流程
  */
@Slf4j
@Component
public class OrderPaySuccessListener {

    @Resource
    private IOrderService orderService;

//    @Subscribe
//    public void handleEvent(String orderId) {
//        try {
//            log.info("支付完成，发货并记录，开始。订单：{}", orderId);
//            orderService.deliverGoods(orderId);
//        } catch (Exception e) {
//            log.error("支付完成，发货并记录，失败。订单：{}", orderId, e);
//        }
//    }

    /**
     * topic要和生产者topic一致；consumerGroup 必须指定；selectorExpression 是tag，默认为“*”，不设置会监听所有消息。
     */
    @Service
    @RocketMQMessageListener(topic = "MY_GPT", selectorExpression = "success_order", consumerGroup = "ORDER_CON_GROUP")
    public class ConsumerSend implements RocketMQListener<String> {
        // 监听到消息就会执行此方法
        @Override
        public void onMessage(String orderId) {
            try {
                log.info("支付完成，发货并记录，开始。订单：{}", orderId);
                orderService.deliverGoods(orderId);
            } catch (Exception e) {
                log.error("支付完成，发货并记录，失败。订单：{}", orderId, e);
            }
        }
    }

    // MessageExt：是一个消息接收通配符，不管发送的是String还是对象，都可接收。
//    @Service
//    @RocketMQMessageListener(topic = "MY_GPT", selectorExpression = "test", consumerGroup = "Con_Group_Three")
//    public class Consumer implements RocketMQListener<MessageExt> {
//        @Override
//        public void onMessage(MessageExt messageExt) {
//            byte[] body = messageExt.getBody();
//            String msg = new String(body);
//            log.info("监听到消息：msg={}", msg);
//        }
//    }
}
