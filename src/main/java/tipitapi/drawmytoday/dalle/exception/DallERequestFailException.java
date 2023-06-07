package tipitapi.drawmytoday.dalle.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class DallERequestFailException extends Exception {

    public DallERequestFailException() {
        super(ErrorCode.DALLE_REQUEST_FAIL.getMessage());
    }

    public DallERequestFailException(Throwable throwable) {
        super(ErrorCode.DALLE_REQUEST_FAIL.getMessage(), throwable);
    }
}
