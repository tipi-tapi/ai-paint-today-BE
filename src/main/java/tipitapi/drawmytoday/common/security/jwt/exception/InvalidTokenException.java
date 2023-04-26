package tipitapi.drawmytoday.common.security.jwt.exception;


import tipitapi.drawmytoday.common.exception.ErrorCode;

public class InvalidTokenException extends TokenException {

    public InvalidTokenException(Throwable e) {
        super(ErrorCode.INVALID_TOKEN, e);
    }

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
