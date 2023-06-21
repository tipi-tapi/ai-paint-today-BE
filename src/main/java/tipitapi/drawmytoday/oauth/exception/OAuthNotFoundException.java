package tipitapi.drawmytoday.oauth.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class OAuthNotFoundException extends BusinessException {

    public OAuthNotFoundException() {
        super(ErrorCode.OAUTH_NOT_FOUND);
    }
}
