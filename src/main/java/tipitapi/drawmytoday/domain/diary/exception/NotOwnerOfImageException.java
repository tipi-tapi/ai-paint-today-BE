package tipitapi.drawmytoday.domain.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class NotOwnerOfImageException extends BusinessException {

    public NotOwnerOfImageException() {
        super(ErrorCode.IMAGE_NOT_OWNER);
    }
}
