package tipitapi.drawmytoday.domain.adreward.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.adreward.domain.AdReward;
import tipitapi.drawmytoday.domain.adreward.repository.AdRewardRepository;
import tipitapi.drawmytoday.domain.ticket.service.TicketService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdRewardService {

    private final AdRewardRepository adRewardRepository;
    private final ValidateUserService validateUserService;
    private final TicketService ticketService;

    @Transactional
    public void createAdReward(Long userId) {
        User user = validateUserService.validateUserById(userId);
        AdReward adReward = new AdReward(user);
        adRewardRepository.save(adReward);
        ticketService.createTicketByAdReward(user);
    }
}
