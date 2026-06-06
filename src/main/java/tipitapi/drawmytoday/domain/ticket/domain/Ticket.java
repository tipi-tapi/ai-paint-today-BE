package tipitapi.drawmytoday.domain.ticket.domain;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity {

    private String ticketId;

    @NotNull
    private User user;

    @NotNull
    private TicketType ticketType;

    private LocalDateTime usedAt;

    private Ticket(User user, TicketType type) {
        this.user = user;
        this.ticketType = type;
    }

    private Ticket(String ticketId, User user, TicketType ticketType, LocalDateTime usedAt, LocalDateTime createdAt) {
        super(createdAt);
        this.ticketId = ticketId;
        this.user = user;
        this.ticketType = ticketType;
        this.usedAt = usedAt;
    }

    public static Ticket of(User user, TicketType type) {
        return new Ticket(user, type);
    }

    public static Ticket restore(String ticketId, User user, TicketType ticketType,
                                 LocalDateTime usedAt, LocalDateTime createdAt) {
        return new Ticket(ticketId, user, ticketType, usedAt, createdAt);
    }

    public void use() {
        this.usedAt = LocalDateTime.now();
    }
}
