package io.luowei.aichat.model.order.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author luowei
 * description 支付类型
 */
@Getter
@AllArgsConstructor
public enum PayTypeVO {

    WEIXIN_NATIVE(0, "微信Native支付"),
    ALI_NATIVE(0, "支付宝Native支付"),
            ;

    private final Integer code;
    private final String desc;
    public static PayTypeVO get(Integer code){
        switch (code){
            case 0:
                return PayTypeVO.WEIXIN_NATIVE;
            default:
                return PayTypeVO.WEIXIN_NATIVE;
        }
    }
}