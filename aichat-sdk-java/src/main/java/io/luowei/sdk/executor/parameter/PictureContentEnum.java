package io.luowei.sdk.executor.parameter;

import lombok.AllArgsConstructor;
import lombok.Getter;


public class PictureContentEnum {

    @Getter
    @AllArgsConstructor
    public enum ContentType{
        IMAGE("image"),
        TEXT("text");

        private String value;
    }
}
