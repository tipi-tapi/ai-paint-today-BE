package tipitapi.drawmytoday.domain.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketQueryRepository {

}
