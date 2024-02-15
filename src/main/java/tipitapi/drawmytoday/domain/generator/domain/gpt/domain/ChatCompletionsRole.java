package tipitapi.drawmytoday.domain.generator.domain.gpt.domain;

public enum ChatCompletionsRole {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String role;

    ChatCompletionsRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
