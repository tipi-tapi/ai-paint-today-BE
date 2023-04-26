package tipitapi.drawmytoday.common.security.jwt.exception;


import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class TokenException extends BusinessException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode, throwable);
    }
}
