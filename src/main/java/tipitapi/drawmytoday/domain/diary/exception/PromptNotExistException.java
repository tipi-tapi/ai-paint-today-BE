package tipitapi.drawmytoday.domain.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class PromptNotExistException extends BusinessException {

    public PromptNotExistException() {
        super(ErrorCode.PROMPT_NOT_EXIST);
    }
}
