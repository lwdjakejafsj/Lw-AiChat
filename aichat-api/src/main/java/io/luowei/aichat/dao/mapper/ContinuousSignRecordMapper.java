package io.luowei.aichat.dao.mapper;

import io.luowei.aichat.dao.po.ContinuousSignRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;


@Mapper
public interface ContinuousSignRecordMapper {

    void insert(ContinuousSignRecord continuousSignRecord);

    void update(ContinuousSignRecord continuousSignRecord);

    void batchDelete(List<Long> ids);

    ContinuousSignRecord queryContinuousSignRecordByEndTime(@Param("openid")String openId, @Param("date")Date date);

    ContinuousSignRecord queryContinuousSignRecordByStartTime(@Param("openid")String openId, @Param("date")Date date);
}
