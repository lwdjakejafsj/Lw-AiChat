package io.luowei.aichat.dao.mapper;

import io.luowei.aichat.dao.po.MonthSign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author luowei
* @description 针对表【month_sign】的数据库操作Mapper
*/
@Mapper
public interface MonthSignMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MonthSign record);

    int insertSelective(MonthSign record);

    MonthSign selectByOpenidAndMonth(@Param("openid") String openid, @Param("month") String month);

    int updateByPrimaryKeySelective(MonthSign record);

    void updateByPrimaryKey(MonthSign record);

}
