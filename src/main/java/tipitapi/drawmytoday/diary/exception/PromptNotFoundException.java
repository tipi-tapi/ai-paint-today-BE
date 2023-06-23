package tipitapi.drawmytoday.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class PromptNotFoundException extends BusinessException {

    public PromptNotFoundException() {
        super(ErrorCode.PROMPT_NOT_FOUND);
    }
}
