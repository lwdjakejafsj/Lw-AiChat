package io.luowei.sdk.executor.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 条目
 *
 * author：luowei
 */
@Data
public class Item implements Serializable {

    private static final long serialVersionUID = 3244723712850679296L;

    private String url;
    //    @JsonProperty("b64_json")
//    private String b64Json;
    @JsonProperty("revised_prompt")
    private String revisedPrompt;
}
