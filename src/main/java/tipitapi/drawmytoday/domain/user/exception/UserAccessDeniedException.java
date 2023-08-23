package tipitapi.drawmytoday.domain.user.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class UserAccessDeniedException extends BusinessException {

    public UserAccessDeniedException() {
        super(ErrorCode.USER_ACCESS_DENIED);
    }
}
