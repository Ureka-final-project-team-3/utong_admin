package ureka.team3.utong_admin.common.exception.business;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class AlreadyReviewedException extends BusinessException {
    public AlreadyReviewedException() {
        super(ErrorCode.ALREADY_REVIEWED);
    }

    public AlreadyReviewedException(String message) {
        super(ErrorCode.ALREADY_REVIEWED, message);
    }
}
