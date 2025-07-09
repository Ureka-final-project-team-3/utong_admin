package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class FileProcessingException extends BusinessException {
  public FileProcessingException() {
    super(ErrorCode.FILE_PROCESSING_ERROR);
  }

  public FileProcessingException(String message) {
    super(ErrorCode.FILE_PROCESSING_ERROR, message);
  }
}