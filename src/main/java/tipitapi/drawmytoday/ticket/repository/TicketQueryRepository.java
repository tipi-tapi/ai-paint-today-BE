package tipitapi.drawmytoday.ticket.repository;

import java.util.Optional;
import tipitapi.drawmytoday.ticket.domain.Ticket;

public interface TicketQueryRepository {

    Optional<Ticket> findValidTicket(Long userId);
}