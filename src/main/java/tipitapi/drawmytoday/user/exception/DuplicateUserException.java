package tipitapi.drawmytoday.user.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class DuplicateUserException extends BusinessException {

    public DuplicateUserException() {
        super(ErrorCode.DUPLICATE_USER);
    }
}
