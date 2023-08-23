package tipitapi.drawmytoday.domain.emotion.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class EmotionNotFoundException extends BusinessException {

    public EmotionNotFoundException() {
        super(ErrorCode.EMOTION_NOT_FOUND);
    }
}
