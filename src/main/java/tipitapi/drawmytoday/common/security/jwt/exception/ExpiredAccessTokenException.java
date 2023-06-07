package tipitapi.drawmytoday.common.security.jwt.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class ExpiredAccessTokenException extends TokenException {

    public ExpiredAccessTokenException() {
        super(ErrorCode.EXPIRED_JWT_ACCESS_TOKEN);
    }
}
