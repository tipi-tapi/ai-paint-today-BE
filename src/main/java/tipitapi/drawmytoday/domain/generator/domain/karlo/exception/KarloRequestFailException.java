package tipitapi.drawmytoday.domain.generator.domain.karlo.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

public class KarloRequestFailException extends ImageGeneratorException {

    public KarloRequestFailException() {
        super(ErrorCode.KARLO_REQUEST_FAIL);
    }

    public KarloRequestFailException(Throwable throwable) {
        super(ErrorCode.KARLO_REQUEST_FAIL, throwable);
    }
}
