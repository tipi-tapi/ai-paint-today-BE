package tipitapi.drawmytoday.common.testdata;

import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;

public class TestUser {

    public static User createUser() {
        return User.create(SocialCode.GOOGLE);
    }

    public static User createUserWithId(Long userId) {
        User user = createUser();
        ReflectionTestUtils.setField(user, "userId", userId);
        return user;
    }
}
