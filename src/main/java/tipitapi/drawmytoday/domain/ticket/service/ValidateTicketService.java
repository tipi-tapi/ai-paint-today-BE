package tipitapi.drawmytoday.domain.ticket.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;
import tipitapi.drawmytoday.domain.ticket.repository.TicketRepository;

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
        // mocking 이므로 주석 처리
//        ticketRepository.findValidTicket(userId)
//            .orElseThrow(ValidTicketNotExistsException::new)
//            .use();
    }
}
