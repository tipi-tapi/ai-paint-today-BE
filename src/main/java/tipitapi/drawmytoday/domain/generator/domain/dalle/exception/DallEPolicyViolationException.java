package tipitapi.drawmytoday.domain.generator.domain.dalle.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

public class DallEPolicyViolationException extends ImageGeneratorException {

    public DallEPolicyViolationException(Throwable throwable) {
        super(ErrorCode.DALLE_CONTENT_POLICY_VIOLATION, throwable);
    }
}
