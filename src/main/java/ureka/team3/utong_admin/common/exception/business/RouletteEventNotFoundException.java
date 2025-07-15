package ureka.team3.utong_admin.common.exception.business;

import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

public class RouletteEventNotFoundException extends BusinessException {
    public RouletteEventNotFoundException() {
        super(ErrorCode.ROULETTE_EVENT_NOT_FOUND);
    }

    public RouletteEventNotFoundException(String message) {
        super(ErrorCode.ROULETTE_EVENT_NOT_FOUND, message);
    }
}