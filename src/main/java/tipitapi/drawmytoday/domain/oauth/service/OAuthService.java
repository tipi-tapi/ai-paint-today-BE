package tipitapi.drawmytoday.domain.oauth.service;

import static tipitapi.drawmytoday.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOAuthService googleOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final ValidateUserService validateUserService;

    @Transactional
    public void deleteAccount(Long userId) {
        User user = validateUserService.validateUserById(userId);
        if (user.getSocialCode() == SocialCode.GOOGLE) {
            googleOAuthService.deleteAccount(user);
        } else if (user.getSocialCode() == SocialCode.APPLE) {
            appleOAuthService.deleteAccount(user);
        } else {
            throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
    }
}
