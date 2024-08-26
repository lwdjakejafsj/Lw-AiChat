package io.luowei.aichat.common.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import io.luowei.aichat.common.mq.OrderMessageProducer;
import io.luowei.aichat.service.order.IOrderService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 调单补偿
 * author: luowei
 * date:
 */
@Slf4j
@Component
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;

    @Autowired(required = false)
    private NativePayService payService;

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private OrderMessageProducer orderMessageProducer;

    @Value("${wxpay.config.mchid}")
    private String mchid;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssXXX");

    @Timed(value = "no_pay_notify_order_job", description = "定时任务，订单支付状态更新")
    @Scheduled(cron = "0 0/1 * * * ?")
    public void exec() {
        try {
            if (null == payService && null == alipayClient) {
                log.info("定时任务，订单支付状态更新。应用未配置支付渠道，任务不执行。");
                return;
            }

            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，订单支付状态更新，暂无未更新订单 orderIds is null");
                return;
            }

            for (String orderId : orderIds) {

                // 构造请求参数以调用接口
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                AlipayTradeQueryModel model = new AlipayTradeQueryModel();

                // 设置订单支付时传入的商户订单号
                model.setOutTradeNo(orderId);

                // 设置支付宝交易号
//                model.setTradeNo("2014112611001004680 073956707");

                // 设置查询选项
                List<String> queryOptions = new ArrayList<String>();
                queryOptions.add("trade_settle_info");
                model.setQueryOptions(queryOptions);

                request.setBizModel(model);
                // 第三方代调用模式下请设置app_auth_token
                // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

                AlipayTradeQueryResponse response = alipayClient.execute(request);
                System.out.println(response.getBody());

                if (!response.isSuccess()) {
                    log.info("支付宝订单还未创建，用户在账号登录期间退出");
                    // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
                    String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
                    log.info(diagnosisUrl);
                    continue;
                }

                if (response.getTradeStatus().equals("TRADE_SUCCESS")) {
                    log.info("定时任务，订单支付状态更新，当前订单已支付 orderId is {}", orderId);
                    // 支付单号
                    String transactionId = response.getTradeNo();
                    String payAmount = response.getBuyerPayAmount();
                    Date successTime = response.getSendPayDate();


                    // 更新订单
                    boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, new BigDecimal(payAmount), successTime);
                    if (isSuccess) {
                        // 发布消息
                        orderMessageProducer.send(orderId);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
