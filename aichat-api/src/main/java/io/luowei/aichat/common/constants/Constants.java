package io.luowei.aichat.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Constants {

    public final static String SPLIT = ",";

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum ResponseCode {
        SUCCESS("0000", "成功"),
        UN_ERROR("0001", "未知失败"),
        ILLEGAL_PARAMETER("0002", "非法参数"),
        TOKEN_ERROR("0003", "权限拦截"),
        RATE_LIMIT("0004", "请勿过度发起请求，稍后在尝试"),
        ORDER_PRODUCT_ERR("OE001", "所购商品已下线，请重新选择下单商品"),
        REPEAT_SIGN("0005", "重复签到"),
        SIGN_ERROR("0006", "签到错误")
        ;
        private String code;
        private String info;
    }
}
