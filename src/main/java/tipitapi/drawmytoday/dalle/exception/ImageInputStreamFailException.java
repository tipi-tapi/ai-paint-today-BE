package tipitapi.drawmytoday.dalle.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class ImageInputStreamFailException extends Exception {

    public ImageInputStreamFailException() {
        super(ErrorCode.IMAGE_INPUT_STREAM_FAIL.getMessage());
    }

    public ImageInputStreamFailException(Throwable throwable) {
        super(ErrorCode.IMAGE_INPUT_STREAM_FAIL.getMessage(), throwable);
    }
}
