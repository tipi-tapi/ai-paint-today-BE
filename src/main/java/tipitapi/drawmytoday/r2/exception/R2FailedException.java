package tipitapi.drawmytoday.r2.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class R2FailedException extends BusinessException {

    public R2FailedException(Throwable throwable) {
        super(ErrorCode.R2_FAILED, throwable);
    }
}
