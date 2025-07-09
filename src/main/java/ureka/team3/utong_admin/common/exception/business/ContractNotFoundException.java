package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class ContractNotFoundException extends BusinessException {
  public ContractNotFoundException() {
    super(ErrorCode.CONTRACT_NOT_FOUND);
  }

  public ContractNotFoundException(String message) {
    super(ErrorCode.CONTRACT_NOT_FOUND, message);
  }
}