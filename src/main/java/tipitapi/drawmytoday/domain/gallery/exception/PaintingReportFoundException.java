package tipitapi.drawmytoday.domain.gallery.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class PaintingReportFoundException extends BusinessException {

    public PaintingReportFoundException() {
        super(ErrorCode.PAINTING_REPORT_FOUND);
    }
}
