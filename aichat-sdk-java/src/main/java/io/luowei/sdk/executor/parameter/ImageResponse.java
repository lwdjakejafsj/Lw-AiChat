package io.luowei.sdk.executor.parameter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片响应
 *
 * author：luowei
 */
@Data
public class ImageResponse implements Serializable {

    private static final long serialVersionUID = 7794686357934848547L;

    /**
     * 条目数据
     */
    private List<Item> data;
    /**
     * 创建时间
     */
    private long created;
}
