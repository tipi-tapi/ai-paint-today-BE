package tipitapi.drawmytoday.dalle.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class DallERequestFailException extends BusinessException {

    public DallERequestFailException() {
        super(ErrorCode.DALLE_REQUEST_FAIL);
    }
}
