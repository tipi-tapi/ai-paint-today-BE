package tipitapi.drawmytoday.domain.ticket.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    private LocalDateTime usedAt;

    private Ticket(User user, TicketType type) {
        this.user = user;
        this.ticketType = type;
    }

    public static Ticket of(User user, TicketType type) {
        return new Ticket(user, type);
    }

    public void use() {
        this.usedAt = LocalDateTime.now();
    }
}
