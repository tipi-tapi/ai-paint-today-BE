package tipitapi.drawmytoday.domain.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class NotOwnerOfImageException extends BusinessException {

    public NotOwnerOfImageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
