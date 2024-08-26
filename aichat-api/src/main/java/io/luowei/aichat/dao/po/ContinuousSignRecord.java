package io.luowei.aichat.dao.po;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ContinuousSignRecord implements Serializable {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 用户ID；这里用的是微信ID作为唯一ID，你也可以给用户创建唯一ID，之后绑定微信ID
     */
    private String openid;

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date endTime;

    private Integer signLength;
}