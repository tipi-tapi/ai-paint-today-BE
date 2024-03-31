package tipitapi.drawmytoday.domain.generator.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public abstract class TextGeneratorException extends BusinessException {

    public TextGeneratorException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TextGeneratorException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode, throwable);
    }
}
