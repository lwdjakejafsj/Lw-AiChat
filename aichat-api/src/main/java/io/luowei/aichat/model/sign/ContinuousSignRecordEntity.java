package io.luowei.aichat.model.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContinuousSignRecordEntity implements Serializable {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 用户ID；这里用的是微信ID作为唯一ID，你也可以给用户创建唯一ID，之后绑定微信ID
     */
    private String openid;

    private Date startTime;

    private Date endTime;

    private Integer signLength;
}