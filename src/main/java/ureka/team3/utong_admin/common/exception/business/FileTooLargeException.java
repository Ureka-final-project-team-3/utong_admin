package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class FileTooLargeException extends BusinessException {
  public FileTooLargeException() {
    super(ErrorCode.FILE_TOO_LARGE);
  }

  public FileTooLargeException(String message) {
    super(ErrorCode.FILE_TOO_LARGE, message);
  }
}