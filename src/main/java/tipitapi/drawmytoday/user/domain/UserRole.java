package tipitapi.drawmytoday.user.domain;

public enum UserRole {
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER");

    private final String key;

    UserRole(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
