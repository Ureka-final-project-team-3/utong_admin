package ureak.team3.utong_admin.common.exception.business;

import ureak.team3.utong_admin.common.exception.BusinessException;
import ureak.team3.utong_admin.common.exception.ErrorCode;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD);
    }

    public InvalidPasswordException(String message) {
        super(ErrorCode.INVALID_PASSWORD, message);
    }
}
