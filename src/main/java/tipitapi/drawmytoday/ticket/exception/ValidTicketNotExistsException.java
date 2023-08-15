package tipitapi.drawmytoday.ticket.exception;

import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class ValidTicketNotExistsException extends BusinessException {

    public ValidTicketNotExistsException() {
        super(ErrorCode.VALID_TICKET_NOT_EXISTS);
    }
}
