package tipitapi.drawmytoday.user.exception;

import tipitapi.drawmytoday.common.exception.CustomException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class UserNotFoundException extends CustomException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }
}
