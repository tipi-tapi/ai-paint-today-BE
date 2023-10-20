package tipitapi.drawmytoday.domain.diary.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class SelectedImageDeletionDeniedException extends BusinessException {

    public SelectedImageDeletionDeniedException() {
        super(ErrorCode.SELECTED_IMAGE_DELETION_DENIED);
    }
}
