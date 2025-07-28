package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class NotMyReviewException extends BusinessException {
    public NotMyReviewException() {
        super(ErrorCode.NOT_MY_REVIEW);
    }

    public NotMyReviewException(String message) {
        super(ErrorCode.NOT_MY_REVIEW, message);
    }
}
