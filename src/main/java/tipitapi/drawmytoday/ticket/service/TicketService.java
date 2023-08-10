package tipitapi.drawmytoday.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.ticket.domain.Ticket;
import tipitapi.drawmytoday.ticket.domain.TicketType;
import tipitapi.drawmytoday.ticket.repository.TicketRepository;
import tipitapi.drawmytoday.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void createTicketByJoin(User user) {
        for (int i = 0; i < 7; i++) {
            createTicket(user, TicketType.JOIN);
        }
    }

    private void createTicket(User user, TicketType type) {
        ticketRepository.save(Ticket.of(user, type));
    }
}
