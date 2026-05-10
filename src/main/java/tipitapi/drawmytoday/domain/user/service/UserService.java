package tipitapi.drawmytoday.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.ticket.service.TicketService;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TicketService ticketService;

    @Transactional
    public User registerUser(String email, SocialCode socialCode, String refreshToken,
                             String appleIdToken) {
        User user = User.builder().email(email).socialCode(socialCode).build();
        user.setRefreshToken(refreshToken);
        user.setAppleIdToken(appleIdToken);
        User saved = userRepository.save(user);
        ticketService.createTicketByJoin(saved);
        return saved;
    }

    @Transactional
    public void deleteUser(User user) {
        user.deleteUser();
        userRepository.save(user);
    }
}
