package tipitapi.drawmytoday.domain.dalle.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class ImageInputStreamFailException extends DallEException {

    public ImageInputStreamFailException() {
        super(ErrorCode.IMAGE_INPUT_STREAM_FAIL.getMessage());
    }

    public ImageInputStreamFailException(Throwable throwable) {
        super(ErrorCode.IMAGE_INPUT_STREAM_FAIL.getMessage(), throwable);
    }
}
