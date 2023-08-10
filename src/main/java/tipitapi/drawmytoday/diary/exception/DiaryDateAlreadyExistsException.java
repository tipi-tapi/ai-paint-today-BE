package tipitapi.drawmytoday.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class DiaryDateAlreadyExistsException extends BusinessException {

    public DiaryDateAlreadyExistsException() {
        super(ErrorCode.DIARY_DATE_ALREADY_EXISTS);
    }
}
