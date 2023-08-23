package tipitapi.drawmytoday.domain.ticket.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;
import tipitapi.drawmytoday.domain.ticket.domain.TicketType;
import tipitapi.drawmytoday.domain.ticket.repository.TicketRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public void createTicketByJoin(User user) {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            tickets.add(Ticket.of(user, TicketType.JOIN));
        }
        ticketRepository.saveAll(tickets);
    }

    @Transactional
    public void createTicketByAdReward(User user) {
        ticketRepository.save(Ticket.of(user, TicketType.AD_REWARD));
    }
}
