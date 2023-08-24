package tipitapi.drawmytoday.dalle.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class DallERequestFailException extends Exception {

    public DallERequestFailException() {
        super(ErrorCode.DALLE_REQUEST_FAIL.getMessage());
    }

    public DallERequestFailException(Throwable throwable) {
        super(ErrorCode.DALLE_REQUEST_FAIL.getMessage(), throwable);
    }

    public static DallERequestFailException violatePolicy() {
        return new DallERequestFailException(ErrorCode.DALLE_CONTENT_POLICY_VIOLATION.getMessage());
    }

    private DallERequestFailException(String message) {
        super(message);
    }
}
