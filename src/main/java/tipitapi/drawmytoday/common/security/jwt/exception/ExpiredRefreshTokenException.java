package tipitapi.drawmytoday.common.security.jwt.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class ExpiredRefreshTokenException extends TokenException {

    public ExpiredRefreshTokenException() {
        super(ErrorCode.EXPIRED_JWT_REFRESH_TOKEN);
    }
}
