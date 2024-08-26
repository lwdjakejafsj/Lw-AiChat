package io.luowei.aichat.dao.repository;

import io.luowei.aichat.model.sign.ContinuousSignRecordEntity;
import io.luowei.aichat.model.sign.MonthSignEntity;

import java.sql.Date;
import java.util.List;

public interface ISignRepository {


    void todaySignUpdateContinuousSignRecord(String openId);

    void updateContinuousSignRecord(ContinuousSignRecordEntity continuousSignRecordEntity);

    void insert(ContinuousSignRecordEntity continuousSignRecordEntity);

    void batchDelete(List<Long> ids);

    ContinuousSignRecordEntity queryContinuousSignRecordByStartTime(String openId, Date date);

    ContinuousSignRecordEntity queryContinuousSignRecordByEndTime(String openId, Date date);

    void updateMonthSign(MonthSignEntity monthSignEntity);

    MonthSignEntity getMonthSign(String openid,String month);

}
