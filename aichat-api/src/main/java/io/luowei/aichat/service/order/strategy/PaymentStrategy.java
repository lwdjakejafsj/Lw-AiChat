package io.luowei.aichat.service.order.strategy;

import io.luowei.aichat.model.order.entity.PayOrderEntity;

import java.math.BigDecimal;

public interface PaymentStrategy {
    PayOrderEntity doPrepayOrder(String openid, String orderId, String productName, BigDecimal amountTotal);
}
