package tipitapi.drawmytoday.domain.generator.api.stability.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

public class StabilityRequestFailException extends ImageGeneratorException {

    public StabilityRequestFailException() {
        super(ErrorCode.STABILITY_REQUEST_FAIL);
    }

    public StabilityRequestFailException(Throwable throwable) {
        super(ErrorCode.STABILITY_REQUEST_FAIL, throwable);
    }
}
