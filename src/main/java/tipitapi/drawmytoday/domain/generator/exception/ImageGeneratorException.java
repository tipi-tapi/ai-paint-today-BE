package tipitapi.drawmytoday.domain.generator.exception;

import lombok.Getter;
import tipitapi.drawmytoday.common.exception.ErrorCode;

@Getter
public abstract class ImageGeneratorException extends Exception {

    private final ErrorCode errorCode;

    public ImageGeneratorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ImageGeneratorException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode;
    }
}
