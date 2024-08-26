package io.luowei.aichat.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInformationDTO {

    // 今天是否签到
    private Boolean isSign;

    private Integer totalSignCount;

    private Integer continuousSignCount;

}
