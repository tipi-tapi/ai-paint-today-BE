package tipitapi.drawmytoday.domain.generator.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;

public class ImageInputStreamFailException extends ImageGeneratorException {

    public ImageInputStreamFailException() {
        super(ErrorCode.IMAGE_INPUT_STREAM_FAIL);
    }

    public ImageInputStreamFailException(Throwable throwable) {
        super(ErrorCode.IMAGE_INPUT_STREAM_FAIL, throwable);
    }
}
