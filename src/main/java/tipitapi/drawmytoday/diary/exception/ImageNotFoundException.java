package tipitapi.drawmytoday.diary.exception;

import tipitapi.drawmytoday.common.exception.CustomException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class ImageNotFoundException extends CustomException {

  public ImageNotFoundException() {
    super(ErrorCode.IMAGE_NOT_FOUND);
  }
}
