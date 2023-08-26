package tipitapi.drawmytoday.domain.ticket.repository;

import static tipitapi.drawmytoday.domain.ticket.domain.QTicket.ticket;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;

@RequiredArgsConstructor
public class TicketQueryRepositoryImpl implements TicketQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Ticket> findValidTicket(Long userId) {
        return Optional.ofNullable(
            queryFactory.selectFrom(ticket)
                .where(ticket.usedAt.isNull()
                    .and(ticket.user.userId.eq(userId)))
                .orderBy(ticket.createdAt.asc())
                .fetchFirst());
    }
}
