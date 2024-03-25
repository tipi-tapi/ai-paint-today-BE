package tipitapi.drawmytoday.domain.generator.api.gpt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ChatCompletionsRole {
    @JsonProperty("system")
    system,
    @JsonProperty("user")
    user,
    @JsonProperty("assistant")
    assistant;

    ChatCompletionsRole() {
    }
}
