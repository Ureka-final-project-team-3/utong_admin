package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class CodeNotFoundException extends BusinessException {
    public CodeNotFoundException() { super(ErrorCode.CODE_NOT_FOUND); }

    public CodeNotFoundException(String message) { super(ErrorCode.CODE_NOT_FOUND, message); }
}
