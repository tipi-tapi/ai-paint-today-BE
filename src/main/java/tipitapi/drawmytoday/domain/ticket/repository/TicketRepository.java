package tipitapi.drawmytoday.domain.ticket.repository;

import java.util.List;
import java.util.Optional;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    List<Ticket> saveAll(List<Ticket> tickets);

    Optional<Ticket> findByTicketId(Long ticketId);

    List<Ticket> findAllByUserId(Long userId);

    List<Ticket> findAllByUserIdAndUsedAtIsNull(Long userId);

    Optional<Ticket> findValidTicket(Long userId);
}
