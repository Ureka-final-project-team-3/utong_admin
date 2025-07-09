package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class ReviewNotFoundException extends BusinessException {
    public ReviewNotFoundException() {
        super(ErrorCode.REVIEW_NOT_FOUND);
    }

    public ReviewNotFoundException(String message) {
        super(ErrorCode.ALREADY_REVIEWED, message);
    }
}
