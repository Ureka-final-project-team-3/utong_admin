package ureak.team3.utong_admin.common.exception.business;

import ureak.team3.utong_admin.common.exception.BusinessException;
import ureak.team3.utong_admin.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(String message) {
    super(ErrorCode.USER_NOT_FOUND, message);
  }
}