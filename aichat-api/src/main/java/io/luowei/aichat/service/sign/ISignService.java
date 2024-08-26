package io.luowei.aichat.service.sign;

import io.luowei.aichat.model.sign.SignInformationEntity;
import io.luowei.aichat.model.sign.SignTypeVO;

import java.util.List;

public interface ISignService {

    SignTypeVO sign(String userId, String dateStr);

    List<String> getSignRecords(String userId, String dateStr);

    SignInformationEntity getSignInformation(String openid);
}
