package tipitapi.drawmytoday.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.oauth.repository.AuthRepository;
import tipitapi.drawmytoday.domain.ticket.service.TicketService;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final TicketService ticketService;

    @Transactional
    public User registerUser(String email, SocialCode socialCode, String refreshToken) {
        User user = userRepository.save(User.builder()
            .email(email)
            .socialCode(socialCode)
            .build());
        authRepository.save(Auth.create(user, refreshToken));
        ticketService.createTicketByJoin(user);
        return user;
    }
}
