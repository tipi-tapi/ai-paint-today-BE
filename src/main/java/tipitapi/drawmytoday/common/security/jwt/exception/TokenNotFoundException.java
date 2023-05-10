package tipitapi.drawmytoday.common.security.jwt.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class TokenNotFoundException extends TokenException {

    public TokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenNotFoundException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode, throwable);
    }
}
