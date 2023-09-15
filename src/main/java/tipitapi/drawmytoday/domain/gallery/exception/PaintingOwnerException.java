package tipitapi.drawmytoday.domain.gallery.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class PaintingOwnerException extends BusinessException {

    public PaintingOwnerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
