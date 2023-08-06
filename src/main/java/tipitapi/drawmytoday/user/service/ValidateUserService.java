package tipitapi.drawmytoday.user.service;

import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.adreward.service.UseAdRewardService;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
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
    private final UseAdRewardService useAdRewardService;

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

    public User validateUserWithDrawLimit(Long userId, ZoneId timezone) {
        User user = validateUserById(userId);
        if (user.checkDrawLimit(timezone)) {
            return user;
        } else {
            if (useAdRewardService.useReward(userId)) {
                return user;
            }
            throw new BusinessException(ErrorCode.USER_ALREADY_DRAW_DIARY);
        }
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
