package tipitapi.drawmytoday.domain.adreward.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.ticket.service.TicketService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdRewardService {

    private final ValidateUserService validateUserService;
    private final TicketService ticketService;

    @Transactional
    public void createTicket(String userId) {
        User user = validateUserService.validateUserById(userId);
        ticketService.createTicketByAdReward(user);
    }
}
