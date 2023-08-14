package tipitapi.drawmytoday.ticket.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.ticket.domain.Ticket;
import tipitapi.drawmytoday.ticket.exception.ValidTicketNotExistsException;
import tipitapi.drawmytoday.ticket.repository.TicketRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateTicketService {

    private final TicketRepository ticketRepository;

    public Optional<Ticket> findValidTicket(Long userId) {
        return ticketRepository.findValidTicket(userId);
    }

    @Transactional
    public void findAndUseTicket(Long userId) {
        ticketRepository.findValidTicket(userId)
            .orElseThrow(ValidTicketNotExistsException::new)
            .use();
    }
}
