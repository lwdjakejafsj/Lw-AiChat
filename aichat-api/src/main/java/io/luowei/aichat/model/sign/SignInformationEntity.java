package io.luowei.aichat.model.sign;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInformationEntity {

    // 今天是否签到
    private Boolean isSign;

    private Integer totalSignCount;

    private Integer continuousSignCount;

}
