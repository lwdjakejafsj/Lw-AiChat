package io.luowei.aichat.common.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import io.luowei.aichat.service.order.IOrderService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 超时关闭订单
 * author: luowei
 * date:
 */
@Slf4j
@Component
public class TimeoutCloseOrderJob {

    @Resource
    private IOrderService orderService;

    @Autowired(required = false)
    private NativePayService payService;

    @Resource
    private AlipayClient alipayClient;

    @Value("${wxpay.config.mchid}")
    private String mchid;

    @Timed(value = "time_out_close_order_job", description = "定时任务，订单超时关闭")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            if (null == payService) {
                log.info("定时任务，订单支付状态更新。应用未配置支付渠道，任务不执行。");
                return;
            }

            // TODO 这里目前只想到遍历的方式
            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
            if (orderIds.isEmpty()) {
                log.info("定时任务，超时30分钟订单关闭，暂无超时未支付订单 orderIds is null");
                return;
            }

            for (String orderId : orderIds) {
                boolean status = orderService.changeOrderClose(orderId);
                // 微信关单；暂时不需要主动关闭
//                CloseOrderRequest request = new CloseOrderRequest();
//                request.setMchid(mchid);
//                request.setOutTradeNo(orderId);
//                payService.closeOrder(request);

                // 支付宝关单
                AlipayTradeCloseRequest aliCloseRequest = new AlipayTradeCloseRequest();
                aliCloseRequest.setBizContent("{" + " \"out_trade_no\":\"" + orderId + "\"" + " }");
                AlipayTradeCloseResponse response = alipayClient.execute(aliCloseRequest);

                log.info("定时任务，超时30分钟订单关闭 orderId: {} status：{}", orderId, status);


            }
        } catch (Exception e) {
            log.error("定时任务，超时15分钟订单关闭失败", e);
        }
    }
}
