package tipitapi.drawmytoday.common.testdata;

import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.domain.UserRole;

public class TestUser {

    public static User createUser() {
        return User.builder().email("email@gmail.com").socialCode(SocialCode.GOOGLE).build();
    }

    public static User createUserWithId(Long userId) {
        return User.restore(userId, "email@gmail.com", SocialCode.GOOGLE, UserRole.USER, null, null, null, null);
    }

    public static User createAdminUserWithId(Long userId) {
        return User.restore(userId, "email@gmail.com", SocialCode.GOOGLE, UserRole.ADMIN, null, null, null, null);
    }

    public static User createUserWithSocialCode(SocialCode socialCode) {
        return User.builder().email("email@gmail.com").socialCode(socialCode).build();
    }
}
