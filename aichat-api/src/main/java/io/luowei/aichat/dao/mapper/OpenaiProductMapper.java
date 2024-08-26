package io.luowei.aichat.dao.mapper;

import io.luowei.aichat.dao.po.OpenaiProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author luowei
* @description 针对表【openai_product】的数据库操作Mapper
* @createDate 2024-06-20 15:00:20
* @Entity io.luowei.chatgpt.dao.po.OpenaiProduct
*/
@Mapper
public interface OpenaiProductMapper  {


    OpenaiProduct queryProductByProductId(Integer productId);

    List<OpenaiProduct> queryProductList();
}
