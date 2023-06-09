package tipitapi.drawmytoday.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class NotOwnerOfDiaryException extends BusinessException {

    public NotOwnerOfDiaryException() {
        super(ErrorCode.DIARY_NOT_OWNER);
    }
}
