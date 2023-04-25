package tipitapi.drawmytoday.diary.exception;

import tipitapi.drawmytoday.common.exception.CustomException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class DiaryNotFoundException extends CustomException {

  public DiaryNotFoundException() {
    super(ErrorCode.DIARY_NOT_FOUND);
  }
}
