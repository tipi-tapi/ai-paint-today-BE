package tipitapi.drawmytoday.s3.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class S3FailedException extends BusinessException {

    public S3FailedException(Throwable throwable) {
        super(ErrorCode.S3_FAILED, throwable);
    }
}
