package tipitapi.drawmytoday.domain.gallery.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class PaintingNotFoundException extends BusinessException {

    public PaintingNotFoundException() {
        super(ErrorCode.PAINTING_NOT_FOUND);
    }
}
