package tipitapi.drawmytoday.domain.ticket.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;
import tipitapi.drawmytoday.domain.ticket.exception.ValidTicketNotExistsException;
import tipitapi.drawmytoday.domain.ticket.repository.TicketRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateTicketService {

    private final TicketRepository ticketRepository;

    public Optional<Ticket> findValidTicket(String userId) {
        return ticketRepository.findValidTicket(userId);
    }

    public void validateTicketExists(String userId) {
        ticketRepository.findValidTicket(userId)
            .orElseThrow(ValidTicketNotExistsException::new);
    }

    public void findAndUseTicket(String userId) {
        ticketRepository.useTicketAtomically(userId)
            .orElseThrow(ValidTicketNotExistsException::new);
    }
}
