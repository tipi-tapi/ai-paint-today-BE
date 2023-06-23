package tipitapi.drawmytoday.user.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateUserService {

    private final UserRepository userRepository;

    public User validateUserById(Long userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    public User validateRegisteredUserByEmail(String email, SocialCode socialCode) {
        return userRepository.findAllByEmail(email).stream()
            .filter(user -> user.getDeletedAt() == null && user.getSocialCode() == socialCode)
            .findFirst()
            .orElse(null);
    }

    public User validateNotDrawDiaryUser(Long userId) {
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(UserNotFoundException::new);
        if (user.getLastDiaryDate() == null) {
            return user;
        } else if (user.getLastDiaryDate().toLocalDate().equals(LocalDate.now())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_DRAW_DIARY);
        } else {
            return user;
        }
    }
}
