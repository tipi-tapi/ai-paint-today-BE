package tipitapi.drawmytoday.domain.ticket.repository;

import java.util.List;
import java.util.Optional;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    List<Ticket> saveAll(List<Ticket> tickets);

    Optional<Ticket> findByTicketId(String ticketId);

    List<Ticket> findAllByUserId(String userId);

    List<Ticket> findAllByUserIdAndUsedAtIsNull(String userId);

    Optional<Ticket> findValidTicket(String userId);

    Optional<Ticket> useTicketAtomically(String userId);
}
