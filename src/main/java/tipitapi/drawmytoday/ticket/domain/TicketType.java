package tipitapi.drawmytoday.ticket.domain;

public enum TicketType {
    JOIN("JOIN"),
    AD_REWARD("AD_REWARD");

    private final String key;

    TicketType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
