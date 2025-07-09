package ureak.team3.utong_admin.common.exception.business;

import ureak.team3.utong_admin.common.exception.BusinessException;
import ureak.team3.utong_admin.common.exception.ErrorCode;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }

    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, message);
    }
}
