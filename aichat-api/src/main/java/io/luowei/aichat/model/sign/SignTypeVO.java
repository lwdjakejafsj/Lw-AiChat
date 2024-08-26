package io.luowei.aichat.model.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * author luowei
 * description 微信公众号消息类型值对象，用于描述对象属性的值，为值对象。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SignTypeVO {

    REPEAT_SIGN("is_sign","已经签到"),
    SIGN("sign","签到成功"),
    SIGN_ERROR("sign_error","签到失败");

    private String code;
    private String desc;

}
