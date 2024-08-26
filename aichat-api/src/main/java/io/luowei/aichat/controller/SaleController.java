package io.luowei.aichat.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import io.luowei.aichat.common.annotation.AccessInterceptor;
import io.luowei.aichat.common.constants.Constants;
import io.luowei.aichat.common.mq.OrderMessageProducer;
import io.luowei.aichat.controller.dto.SaleProductDTO;
import io.luowei.aichat.model.Response;
import io.luowei.aichat.model.order.entity.PayOrderEntity;
import io.luowei.aichat.model.order.entity.ProductEntity;
import io.luowei.aichat.model.order.entity.ShopCartEntity;
import io.luowei.aichat.service.auth.IAuthService;
import io.luowei.aichat.service.order.IOrderService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售卖服务
 * author: luowei
 * date:
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/sale")
public class SaleController {

    @Autowired(required = false)
    private NotificationParser notificationParser;

    @Resource
    private IOrderService orderService;

    @Resource
    private IAuthService authService;

    @Resource
    private OrderMessageProducer orderMessageProducer;

    @Value("${alipay.config.alipay-public-key}")
    private String alipayPublicKey;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 商品查询列表
     * author: luowei
     * date:
     */
    @GetMapping("/query_product_list")
    public Response<List<SaleProductDTO>> queryProductList(@RequestHeader("Authorization") String token) {
        try {
            // 校验token
            boolean success = authService.checkToken(token);
            if (!success) {
                return Response.<List<SaleProductDTO>>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 查询商品
            List<ProductEntity> productEntityList = orderService.queryProductList();
            log.info("商品查询 {}", JSON.toJSONString(productEntityList));

            List<SaleProductDTO> mallProductDTOS = new ArrayList<>();
            for (ProductEntity productEntity : productEntityList) {
                SaleProductDTO mallProductDTO = SaleProductDTO.builder()
                        .productId(productEntity.getProductId())
                        .productName(productEntity.getProductName())
                        .productDesc(productEntity.getProductDesc())
                        .price(productEntity.getPrice())
                        .quota(productEntity.getQuota())
                        .build();
                mallProductDTOS.add(mallProductDTO);
            }

            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(mallProductDTOS)
                    .build();

        } catch (Exception e) {
            log.error("商品查询失败", e);
            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 用户商品下单
     * author: luowei
     * date:
     */
    @Timed(value = "create_par_order", description = "订单接口")
    @AccessInterceptor(key = "token", fallbackMethod = "saleErr", permitsPerSecond = 1.0d, blacklistCount = 10)
    @PostMapping("/create_pay_order")
    public Response<String> createParOrder(@RequestHeader("Authorization") String token, @RequestParam Integer productId) {
        try {
            //校验token
            boolean success = authService.checkToken(token);
            if (!success) {
                return Response.<String>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            //解析token
            String openId = authService.openId(token);
            assert null != openId;
            log.info("用户商品下单，根据商品ID创建支付单开始 openid:{} productId:{}", openId, productId);

            ShopCartEntity shopCartEntity = ShopCartEntity.builder()
                    .openid(openId)
                    .productId(productId).build();

            PayOrderEntity payOrder = orderService.createOrder(shopCartEntity);

            log.info("用户商品下单，根据商品ID创建支付单完成 openid: {} productId: {} orderPay: {}", openId, productId, payOrder.toString());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(payOrder.getPayUrl())
                    .build();

        } catch (Exception e) {
            log.error("用户商品下单，根据商品ID创建支付单失败", e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 微信支付回调
     * author: luowei
     * date:
     */
    @PostMapping("wechat_pay_notify")
    public void payNotify(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 随机串
            String nonceStr = request.getHeader("Wechatpay-Nonce");
            // 微信传递过来的签名
            String signature = request.getHeader("Wechatpay-Signature");
            // 证书序列号（微信平台）
            String serialNo = request.getHeader("Wechatpay-Serial");
            // 时间戳
            String timestamp = request.getHeader("Wechatpay-Timestamp");

            // 构造 RequestParam
            com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
                    .serialNumber(serialNo)
                    .nonce(nonceStr)
                    .signature(signature)
                    .timestamp(timestamp)
                    .body(requestBody)
                    .build();

            // 以支付通知回调为例，验签、解密并转换成 Transaction
            Transaction transaction = notificationParser.parse(requestParam, Transaction.class);
            Transaction.TradeStateEnum tradeState = transaction.getTradeState();

            if (Transaction.TradeStateEnum.SUCCESS.equals(tradeState)) {
                // 支付单号
                String orderId = transaction.getOutTradeNo();
                String transactionId = transaction.getTransactionId();
                Integer total = transaction.getAmount().getTotal();
                String successTime = transaction.getSuccessTime();
                log.info("支付成功 orderId:{} total:{} successTime: {}", orderId, total, successTime);
                // 更新订单
                boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, new BigDecimal(total).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP), dateFormat.parse(successTime));
                if (isSuccess) {
                    // 发布消息
                    orderMessageProducer.send(orderId);
                }
                response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
            } else {
                response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
            }

        } catch (Exception e) {
            log.error("支付失败", e);
            response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
        }
    }

    /**
     * 支付宝支付回调
     * author: luowei
     * date:
     */
    @PostMapping("ali_pay_notify")
    public void payNotify(HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            log.info("支付回调，消息接收 {}", request.getParameter("trade_status"));
            if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParams = request.getParameterMap();
                for (String name : requestParams.keySet()) {
                    params.put(name, request.getParameter(name));
                }

                String orderId = params.get("out_trade_no");
                String transactionId = params.get("trade_no");
                String payAmount = params.get("buyer_pay_amount");
                String successTime = params.get("gmt_payment");

                String sign = params.get("sign");
                String content = AlipaySignature.getSignCheckContentV1(params);
                boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名
                // 支付宝验签
                if (checkSignature) {
                    // 验签通过
                    log.info("支付回调，交易名称: {}", params.get("subject"));
                    log.info("支付回调，交易状态: {}", params.get("trade_status"));
                    log.info("支付回调，支付宝交易凭证号: {}", params.get("trade_no"));
                    log.info("支付回调，商户订单号: {}", params.get("out_trade_no"));
                    log.info("支付回调，交易金额: {}", params.get("total_amount"));
                    log.info("支付回调，买家在支付宝唯一id: {}", params.get("buyer_id"));
                    log.info("支付回调，买家付款时间: {}", params.get("gmt_payment"));
                    log.info("支付回调，买家付款金额: {}", params.get("buyer_pay_amount"));
                    log.info("支付回调，支付回调，更新订单 {}", orderId);
                    // 更新订单未已支付
                    boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, new BigDecimal(payAmount), dateFormat.parse(successTime));

                    // 推送消息
                    if (isSuccess) {
                        orderMessageProducer.send(orderId);
                    }

                    response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
                }
            } else {
                response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
            }

        } catch (Exception e) {
            log.error("支付回调，处理失败", e);
            response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
        }
    }

    public Response<String> saleErr() {
        return Response.<String>builder()
                .code(Constants.ResponseCode.RATE_LIMIT.getCode())
                .info(Constants.ResponseCode.RATE_LIMIT.getInfo())
                .build();
    }

}