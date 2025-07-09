package ureak.team3.utong_admin.common.exception.business;

import ureak.team3.utong_admin.common.exception.BusinessException;
import ureak.team3.utong_admin.common.exception.ErrorCode;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException() {
        super(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    public EmailAlreadyExistsException(String message) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, message);
    }
}
