package io.luowei.aichat.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDTO implements Serializable {

    /**
     * 用户ID；这里用的是微信ID作为唯一ID，你也可以给用户创建唯一ID，之后绑定微信ID
     */
    private String openid;

    private String userName;

    private String avatar;

    private Integer integral;

    /**
     * 总量额度；分配的总使用次数
     */
    private Integer totalQuota;

    /**
     * 剩余额度；剩余的可使用次数
     */
    private Integer surplusQuota;



    private static final long serialVersionUID = 1L;
}