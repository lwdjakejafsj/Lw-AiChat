package io.luowei.aichat.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountEntity {

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

}
