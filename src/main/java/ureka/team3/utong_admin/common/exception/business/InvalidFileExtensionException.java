package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class InvalidFileExtensionException extends BusinessException {
  public InvalidFileExtensionException() {
    super(ErrorCode.INVALID_FILE_EXTENSION);
  }

  public InvalidFileExtensionException(String message) {
    super(ErrorCode.INVALID_FILE_EXTENSION, message);
  }
}