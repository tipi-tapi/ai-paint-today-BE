package tipitapi.drawmytoday.domain.user.domain;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String key;

    UserRole(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
