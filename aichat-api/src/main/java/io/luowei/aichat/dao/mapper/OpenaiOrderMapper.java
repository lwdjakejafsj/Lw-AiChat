package io.luowei.aichat.dao.mapper;

import io.luowei.aichat.dao.po.OpenaiOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author luowei
* @description 针对表【openai_order】的数据库操作Mapper
* @createDate 2024-06-20 15:00:26
* @Entity io.luowei.chatgpt.dao.po.OpenaiOrder
*/
@Mapper
public interface OpenaiOrderMapper  {


    OpenaiOrder queryUnpaidOrder(OpenaiOrder order);

    void insert(OpenaiOrder orderPO);

    void updateOrderPayInfo(OpenaiOrder orderPO);

    OpenaiOrder queryOrderByOrderId(String orderId);

    int updateOrderStatusDeliverGoods(String orderId);

    List<String> queryReplenishmentOrder();

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    int changeOrderPaySuccess(OpenaiOrder orderPO);
}
