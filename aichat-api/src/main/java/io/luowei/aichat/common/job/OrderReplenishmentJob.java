package io.luowei.aichat.common.job;

import io.luowei.aichat.common.mq.OrderMessageProducer;
import io.luowei.aichat.service.order.IOrderService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单补货
 * author: luowei
 * date:
 */
@Slf4j
@Component
public class OrderReplenishmentJob {

    @Resource
    private IOrderService orderService;

    @Resource
    private OrderMessageProducer orderMessageProducer;

    /**
     * 执行订单补货，超时3分钟，已支付，待发货未发货的订单
     */
    @Timed(value = "order_replenishment_job", description = "定时任务，订单补货")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            List<String> orderIds = orderService.queryReplenishmentOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，订单补货不存在，查询 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                log.info("定时任务，订单补货开始。orderId: {}", orderId);
                orderMessageProducer.send(orderId);
            }
        } catch (Exception e) {
            log.error("定时任务，订单补货失败。", e);
        }
    }

}
