package tipitapi.drawmytoday.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.exception.UserAccessDeniedException;
import tipitapi.drawmytoday.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateUserService {

    private final UserRepository userRepository;

    public User validateUserById(Long userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    public User validateRegisteredUserByEmail(String email, SocialCode socialCode) {
        return userRepository.findAllByEmail(email).stream()
            .filter(user -> user.getSocialCode() == socialCode)
            .findFirst()
            .orElse(null);
    }

    public User validateAdminUserById(Long userId) {
        User user = validateUserById(userId);
        if (user.isAdmin()) {
            return user;
        } else {
            throw new UserAccessDeniedException();
        }
    }
}
