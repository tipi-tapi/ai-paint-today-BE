package tipitapi.drawmytoday.common.security.jwt.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class JwtTokenNotFoundException extends TokenException {

    public JwtTokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtTokenNotFoundException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode, throwable);
    }
}
