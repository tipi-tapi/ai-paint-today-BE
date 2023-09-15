package tipitapi.drawmytoday.domain.gallery.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class PaintingOwnerHeartException extends BusinessException {

    public PaintingOwnerHeartException() {
        super(ErrorCode.PAINTING_OWNER_HEART);
    }
}
