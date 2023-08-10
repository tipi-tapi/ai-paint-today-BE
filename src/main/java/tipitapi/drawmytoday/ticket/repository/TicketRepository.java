package tipitapi.drawmytoday.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.ticket.domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketQueryRepository {

}
