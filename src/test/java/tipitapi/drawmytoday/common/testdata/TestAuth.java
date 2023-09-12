package tipitapi.drawmytoday.common.testdata;

import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.user.domain.User;

public class TestAuth {

    public static Auth createAuth(User user) {
        return Auth.create(user, "refreshToken");
    }
}
