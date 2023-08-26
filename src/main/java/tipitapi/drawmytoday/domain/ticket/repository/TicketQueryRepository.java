package tipitapi.drawmytoday.domain.ticket.repository;

import java.util.Optional;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;

public interface TicketQueryRepository {

    Optional<Ticket> findValidTicket(Long userId);
}