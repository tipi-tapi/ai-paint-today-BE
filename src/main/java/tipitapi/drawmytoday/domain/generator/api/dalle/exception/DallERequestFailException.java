package tipitapi.drawmytoday.domain.generator.api.dalle.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

public class DallERequestFailException extends ImageGeneratorException {

    public DallERequestFailException() {
        super(ErrorCode.DALLE_REQUEST_FAIL);
    }

    public DallERequestFailException(Throwable throwable) {
        super(ErrorCode.DALLE_REQUEST_FAIL, throwable);
    }

    public static DallERequestFailException violatePolicy() {
        return new DallERequestFailException(ErrorCode.DALLE_CONTENT_POLICY_VIOLATION);
    }

    private DallERequestFailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
