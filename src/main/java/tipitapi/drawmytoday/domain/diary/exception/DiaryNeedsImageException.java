package tipitapi.drawmytoday.domain.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class DiaryNeedsImageException extends BusinessException {

    public DiaryNeedsImageException() {
        super(ErrorCode.DIARY_NEEDS_IMAGE);
    }
}
