package tipitapi.drawmytoday.diary.exception;

import tipitapi.drawmytoday.common.exception.CustomException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class NotOwnerOfDiaryException extends CustomException {

  public NotOwnerOfDiaryException(ErrorCode errorCode) {
    super(ErrorCode.DIARY_NOT_OWNER);
  }
}
