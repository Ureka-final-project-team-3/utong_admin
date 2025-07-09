package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class GroupCodeNotFoundException extends BusinessException {
    public GroupCodeNotFoundException() {
        super(ErrorCode.GROUP_CODE_NOT_FOUND);
    }
    public GroupCodeNotFoundException(String message) {
        super(ErrorCode.GROUP_CODE_NOT_FOUND, message);
    }

}
