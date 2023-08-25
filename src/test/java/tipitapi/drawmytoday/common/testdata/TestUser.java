package tipitapi.drawmytoday.common.testdata;

import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.domain.UserRole;

public class TestUser {

    public static User createUser() {
        return User.create(SocialCode.GOOGLE);
    }

    public static User createUserWithId(Long userId) {
        User user = createUser();
        ReflectionTestUtils.setField(user, "userId", userId);
        return user;
    }

    public static User createAdminUserWithId(Long userId) {
        User user = createUserWithId(userId);
        ReflectionTestUtils.setField(user, "userRole", UserRole.ADMIN);
        return user;
    }

    public static User createUserWithSocialCode(SocialCode socialCode) {
        return User.create(socialCode);
    }
}
