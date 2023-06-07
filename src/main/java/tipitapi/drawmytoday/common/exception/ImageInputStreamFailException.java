package tipitapi.drawmytoday.common.exception;

public class ImageInputStreamFailException extends BusinessException {

    public ImageInputStreamFailException() {
        super(ErrorCode.IMAGE_INPUT_STREAM_FAIL);
    }
}
