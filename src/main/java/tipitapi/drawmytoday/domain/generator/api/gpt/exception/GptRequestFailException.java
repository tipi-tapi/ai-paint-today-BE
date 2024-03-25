package tipitapi.drawmytoday.domain.generator.api.gpt.exception;

import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.generator.exception.TextGeneratorException;

public class GptRequestFailException extends TextGeneratorException {

    public GptRequestFailException() {
        super(ErrorCode.GPT_REQUEST_FAIL);
    }

    public GptRequestFailException(Throwable throwable) {
        super(ErrorCode.GPT_REQUEST_FAIL, throwable);
    }
}
