package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class PriceNotFoundException extends BusinessException {
    public PriceNotFoundException(String message) { super(ErrorCode.PRICE_NOT_FOUND, message); }

    public PriceNotFoundException() { super(ErrorCode.PRICE_NOT_FOUND); }
}
